package com.ivancea.MTGRules;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.Toast;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ivancea.MTGRules.constants.Actions;
import com.ivancea.MTGRules.constants.Events;
import com.ivancea.MTGRules.constants.Preferences;
import com.ivancea.MTGRules.model.HistoryItem;
import com.ivancea.MTGRules.model.Rule;
import com.ivancea.MTGRules.model.RulesSource;
import com.ivancea.MTGRules.services.RulesService;
import com.ivancea.MTGRules.ui.main.AboutFragment;
import com.ivancea.MTGRules.ui.main.MainFragment;
import com.ivancea.MTGRules.ui.main.MainViewModel;

import org.apache.commons.lang3.StringUtils;

public class MainActivity extends AppCompatActivity {

    @Inject
    RulesService rulesService;

    private FirebaseAnalytics mFirebaseAnalytics;

    private MainViewModel viewModel;

    private Boolean ttsOk = null;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean useLightTheme = getPreferences(Context.MODE_PRIVATE).getBoolean(Preferences.USE_LIGHT_THEME, false);
        setTheme(useLightTheme);

        ((MtgRulesApplication) getApplicationContext()).appComponent.inject(this);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setLogo(R.drawable.ic_launcher_foreground);
        getSupportActionBar().setTitle(R.string.app_name);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow();
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        if (viewModel.getCurrentRules().getValue().isEmpty()) {
            RulesSource rulesSource = rulesService.getLatestRulesSource();

            useRules(rulesSource);
        }

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                ttsOk = true;
                tts.setLanguage(Locale.ENGLISH);
            } else {
                ttsOk = false;
            }
        });

        handleIntent(getIntent());
    }

    @Override
    protected void onStop() {
        if (tts != null) {
            tts.stop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        boolean addToHistory = !intent.getBooleanExtra(Actions.BACK, false);

        switch (intent.getAction()) {
            case Intent.ACTION_SEARCH: {
                String searchString = intent.getStringExtra(SearchManager.QUERY);
                searchRules(searchString);

                if (addToHistory) {
                pushHistoryItem(new HistoryItem(HistoryItem.Type.Search, searchString));
                }

                logEvent(Events.SEARCH_RULES);
                break;
            }

            case Actions.ACTION_READ: {
                if (ttsOk == null) {
                    Toast.makeText(this, R.string.toast_tts_not_ready, Toast.LENGTH_SHORT);
                } else if (!ttsOk) {
                    Toast.makeText(this, R.string.toast_tts_not_working, Toast.LENGTH_SHORT);
                } else {
                    tts.speak(intent.getStringExtra(Actions.DATA), TextToSpeech.QUEUE_FLUSH, null, "rules");
                    logEvent(Events.READ_RULE);
                }
                break;
            }

            case Actions.ACTION_NAVIGATE_RULE: {
                String title = intent.getStringExtra(Actions.DATA);

                if (title.isEmpty()) {
                    viewModel.getVisibleRules().setValue(viewModel.getCurrentRules().getValue());
                    viewModel.getSelectedRuleTitle().setValue(null);

                    if (addToHistory) {
                        pushHistoryItem(new HistoryItem(HistoryItem.Type.Rule, title));
                    }
                } else {
                    Rule existingRule = viewModel.getVisibleRules().getValue().stream()
                        .filter(r -> r.getTitle().equals(title))
                        .findAny().orElse(null);

                    if (existingRule == null || !existingRule.getSubRules().isEmpty() || !isLastHistoryItemNavigation()) {
                        List<Rule> rules = findRule(title);

                        if (!rules.isEmpty()) {
                            Rule rule = rules.get(rules.size() - 1);
                            if (rule.getSubRules().isEmpty()) {
                                rules.remove(rules.size() - 1);
                            }

                            rules.addAll(rules.get(rules.size() - 1).getSubRules());

                            viewModel.getVisibleRules().setValue(rules);

                            if (addToHistory) {
                                pushHistoryItem(new HistoryItem(HistoryItem.Type.Rule, title));
                            }
                        }
                    }

                    viewModel.getSelectedRuleTitle().setValue(title);
                }

                break;
            }

            case Actions.ACTION_RANDOM_RULE: {
                List<Rule> rules = viewModel.getCurrentRules().getValue();

                int ruleCount = (int) rules.stream()
                    .flatMap(this::flattenRule)
                    .count();

                Random random = new Random();

                int seed = intent.getIntExtra(Actions.DATA, random.nextInt(Integer.MAX_VALUE));
                random.setSeed(seed);

                int rulesToSkip = random.nextInt(ruleCount);

                rules.stream()
                    .flatMap(this::flattenRule)
                    .skip(rulesToSkip)
                    .findFirst()
                    .ifPresent(rule -> {
                        viewModel.getVisibleRules().setValue(findRule(rule.getTitle()));
                        viewModel.getSelectedRuleTitle().setValue(null);

                        if (addToHistory) {
                            pushHistoryItem(new HistoryItem(HistoryItem.Type.Random, seed));
                        }
                    });

                logEvent(Events.RANDOM_RULE);

                break;
            }

            case Actions.ACTION_CHANGE_THEME: {
                boolean useLightTheme = !getPreferences(Context.MODE_PRIVATE).getBoolean(Preferences.USE_LIGHT_THEME, false);

                setTheme(useLightTheme);

                getPreferences(Context.MODE_PRIVATE).edit()
                    .putBoolean(Preferences.USE_LIGHT_THEME, useLightTheme)
                    .apply();

                recreate();

                break;
            }
        }
    }

    private void pushHistoryItem(HistoryItem historyItem) {
        ArrayList<HistoryItem> newHistory = new ArrayList<>(viewModel.getHistory().getValue());
        newHistory.add(historyItem);
        viewModel.getHistory().setValue(newHistory);
    }

    private boolean popHistoryItem() {
        if (viewModel.getHistory().getValue().size() < 2) {
            return false;
        }

        ArrayList<HistoryItem> newHistory = new ArrayList<>(viewModel.getHistory().getValue());
        newHistory.remove(newHistory.size() - 1);
        HistoryItem historyItem = newHistory.get(newHistory.size() - 1);
        viewModel.getHistory().setValue(newHistory);

        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra(Actions.BACK, true);

        switch (historyItem.getType()) {
            case Rule: {
                intent.setAction(Actions.ACTION_NAVIGATE_RULE);
                intent.putExtra(Actions.DATA, historyItem.getValue());
                break;
            }
            case Search: {
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, historyItem.getValue());
                break;
            }
            case Random: {
                intent.setAction(Actions.ACTION_RANDOM_RULE);
                intent.putExtra(Actions.DATA, historyItem.getValue());
                break;
            }
        }
        startActivity(intent);

        return true;
    }

    private boolean isLastHistoryItemNavigation() {
        List<HistoryItem> history = viewModel.getHistory().getValue();
        return !history.isEmpty() &&
            history.get(history.size() - 1).getType().equals(HistoryItem.Type.Rule);
    }

    private void logEvent(String event) {
        mFirebaseAnalytics.logEvent(event, null);
    }

    private void setTheme(boolean useLightTheme) {
        setTheme(useLightTheme ? R.style.LightTheme : R.style.DarkTheme);
    }

    private void searchRules(String searchText) {
        List<Rule> filteredRules = viewModel.getCurrentRules().getValue().stream()
            .flatMap(this::flattenRule)
            .filter(rule -> StringUtils.containsIgnoreCase(rule.getTitle(), searchText)
                || StringUtils.containsIgnoreCase(rule.getText(), searchText))
            .collect(Collectors.toList());

        viewModel.getVisibleRules().setValue(filteredRules);
        viewModel.getSelectedRuleTitle().setValue(null);

        /*if (addToHistory)
        {
            PushHistoryItem(new HistoryItem(HistoryType.Search, text));
        }
        LogEvent(EventType.SearchText);*/
    }

    private List<Rule> findRule(String title) {
        if (!Character.isDigit(title.charAt(0))) {
            return viewModel.getCurrentRules().getValue().stream()
                .filter(r -> r.getTitle().equals("Glossary"))
                .findAny()
                .map(g -> {
                    List<Rule> rules = new ArrayList<>();

                    rules.add(g);
                    g.getSubRules().stream()
                        .filter(r -> r.getTitle().equals(title))
                        .findAny()
                        .ifPresent(rules::add);

                    return rules;
                }).orElseThrow(() -> new RuntimeException("No glosary found"));
        }

        return viewModel.getCurrentRules().getValue().stream()
            .flatMap(rule -> {
                if (rule.getTitle().charAt(0) != title.charAt(0)) {
                    return Stream.empty();
                }

                if (rule.getTitle().equals(title)) {
                    return Stream.of(rule);
                }

                return Stream.concat(
                    Stream.of(rule),
                    rule.getSubRules().stream()
                        .flatMap(subRule -> {
                            if (!subRule.getTitle().substring(0, 3).equals(title.substring(0, 3))) {
                                return Stream.empty();
                            }

                            if (subRule.getTitle().equals(title)) {
                                return Stream.of(subRule);
                            }

                            return Stream.concat(
                                Stream.of(subRule),
                                subRule.getSubRules().stream()
                                    .filter(r -> r.getTitle().equals(title))
                            );
                        })
                );
            })
            .collect(Collectors.toCollection(ArrayList::new));
    }

    private Stream<Rule> flattenRule(Rule rule) {
        return Stream.concat(
            Stream.of(rule),
            rule.getSubRules().stream()
                .flatMap(this::flattenRule)
        );
    }

    private void useRules(RulesSource rulesSource) {
        List<Rule> rules = rulesService.loadRules(rulesSource);

        viewModel.getCurrentRules().setValue(rules);
        viewModel.getVisibleRules().setValue(rules);
        viewModel.getSelectedRuleTitle().setValue(null);
        viewModel.getHistory().setValue(Collections.singletonList(new HistoryItem(HistoryItem.Type.Rule, "")));

        getSupportActionBar().setSubtitle(getString(R.string.action_bar_rules) + ": " + rulesSource.getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        menu.findItem(R.id.home).setOnMenuItemClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(Actions.ACTION_NAVIGATE_RULE);
            intent.putExtra(Actions.DATA, "");
            startActivity(intent);
            return true;
        });

        menu.findItem(R.id.randomRule).setOnMenuItemClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(Actions.ACTION_RANDOM_RULE);
            startActivity(intent);

            return true;
        });

        menu.findItem(R.id.changeTheme).setOnMenuItemClickListener(view -> {
            new AlertDialog.Builder(this)
                .setMessage(R.string.change_theme_restart)
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {})
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setAction(Actions.ACTION_CHANGE_THEME);
                    startActivity(intent);
                })
                .show();

            return true;
        });

        menu.findItem(R.id.changeRules).setOnMenuItemClickListener(view -> {
            List<String> formattedRulesSources = rulesService.getRulesSources().stream()
                .map(rulesSource -> rulesSource.getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
                .collect(Collectors.toCollection(ArrayList::new));

            Collections.reverse(formattedRulesSources);

            new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_select_rules)
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {})
                .setItems(formattedRulesSources.toArray(new String[0]), (dialog, which) -> {
                    RulesSource rulesSource = rulesService.getRulesSources().get(rulesService.getRulesSources().size() - which - 1);

                    useRules(rulesSource);

                    logEvent(Events.CHANGE_RULES);
                })
                .show();

            return true;
        });

        menu.findItem(R.id.about).setOnMenuItemClickListener(view -> {
            new AboutFragment().show(getSupportFragmentManager(), null);

            return true;
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (!popHistoryItem()) {
            super.onBackPressed();
        }
    }
}

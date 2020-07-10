package com.ivancea.MTGRules;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SearchView;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.ivancea.MTGRules.constants.Actions;
import com.ivancea.MTGRules.constants.Preferences;
import com.ivancea.MTGRules.model.Rule;
import com.ivancea.MTGRules.model.RulesSource;
import com.ivancea.MTGRules.services.RulesService;
import com.ivancea.MTGRules.ui.main.MainFragment;
import com.ivancea.MTGRules.ui.main.MainViewModel;

public class MainActivity extends AppCompatActivity {

    @Inject
    RulesService rulesService;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean useLightTheme = !getPreferences(Context.MODE_PRIVATE).getBoolean(Preferences.USE_LIGHT_THEME, false);
        setTheme(useLightTheme);

        ((MtgRulesApplication) getApplicationContext()).appComponent.inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow();
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        RulesSource rulesSource = rulesService.getLatestRulesSource();
        List<Rule> rules = rulesService.loadRules(rulesSource);

        viewModel.getCurrentRules().setValue(rules);
        viewModel.getVisibleRules().setValue(rules);

        handleIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_SEARCH: {
                searchRules(intent.getStringExtra(SearchManager.QUERY));
                break;
            }

            case Actions.ACTION_NAVIGATE_RULE: {
                String title = intent.getStringExtra(Actions.DATA);

                if (title.isEmpty()) {
                    viewModel.getVisibleRules().setValue(viewModel.getCurrentRules().getValue());
                } else {
                    Rule ruleToFocus = viewModel.getVisibleRules().getValue().stream()
                        .filter(r -> r.getTitle().equals(title))
                        .findAny().orElse(null);

                    if (ruleToFocus == null || !ruleToFocus.getSubRules().isEmpty()) {
                        List<Rule> rules = findRule(title);

                        if (!rules.isEmpty()) {
                            Rule rule = rules.get(rules.size() - 1);
                            if (rule.getSubRules().isEmpty()) {
                                ruleToFocus = rule;
                                rules.remove(rules.size() - 1);
                            }

                            rules.addAll(rules.get(rules.size() - 1).getSubRules());

                            viewModel.getVisibleRules().setValue(rules);
                        }
                    }
                }

                break;
            }

            case Actions.ACTION_RANDOM_RULE: {

                List<Rule> rules = viewModel.getCurrentRules().getValue();

                int ruleCount = (int) rules.stream()
                    .flatMap(this::flattenRule)
                    .count();

                int rulesToSkip = new Random().nextInt(ruleCount);

                rules.stream()
                    .flatMap(this::flattenRule)
                    .skip(rulesToSkip)
                    .findFirst()
                    .ifPresent(rule -> {
                        viewModel.getVisibleRules().setValue(findRule(rule.getTitle()));
                    });

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

    private void setTheme(boolean useLightTheme) {
        setTheme(useLightTheme ? R.style.LightTheme : R.style.DarkTheme);
    }

    private void searchRules(String searchText) {
        List<Rule> filteredRules = viewModel.getCurrentRules().getValue().stream()
            .flatMap(this::flattenRule)
            .filter(rule -> rule.getTitle().contains(searchText) || rule.getText().contains(searchText))
            .collect(Collectors.toList());

        viewModel.getVisibleRules().setValue(filteredRules);

        /*if (addToHistory)
        {
            PushHistoryItem(new HistoryItem(HistoryType.Search, text));
        }
        LogEvent(EventType.SearchText);*/
    }

    private List<Rule> findRule(String title) {
        if (!Character.isDigit(title.charAt(0))) {
            return viewModel.getCurrentRules().getValue().stream()
                .filter(r -> r.getTitle().equals("Glosary"))
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
                    List<Rule> rules = rulesService.loadRules(rulesService.getRulesSources().get(rulesService.getRulesSources().size() - which - 1));

                    viewModel.getCurrentRules().setValue(rules);
                    viewModel.getVisibleRules().setValue(rules);
                })
                .show();

            return true;
        });

        return super.onCreateOptionsMenu(menu);
    }
}
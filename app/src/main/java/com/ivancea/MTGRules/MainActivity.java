package com.ivancea.MTGRules;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ivancea.MTGRules.constants.Actions;
import com.ivancea.MTGRules.constants.Events;
import com.ivancea.MTGRules.model.HistoryItem;
import com.ivancea.MTGRules.model.Rule;
import com.ivancea.MTGRules.model.RulesSource;
import com.ivancea.MTGRules.services.PermissionsRequesterService;
import com.ivancea.MTGRules.services.RulesComparisonService;
import com.ivancea.MTGRules.services.RulesService;
import com.ivancea.MTGRules.services.StorageService;
import com.ivancea.MTGRules.ui.main.AboutFragment;
import com.ivancea.MTGRules.ui.main.MainViewModel;
import com.ivancea.MTGRules.utils.IntentSender;
import com.ivancea.MTGRules.utils.RuleUtils;
import com.ivancea.MTGRules.utils.RulesSearchUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

	@Inject
	StorageService storageService;

	@Inject
	RulesService rulesService;

	@Inject
	RulesComparisonService rulesComparisonService;

	@Inject
	PermissionsRequesterService permissionsRequesterService;

	private FirebaseAnalytics mFirebaseAnalytics;

	private MainViewModel viewModel;

	private Boolean ttsOk = null;
	private TextToSpeech tts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set variables
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
		viewModel = new ViewModelProvider(this).get(MainViewModel.class);
		tts = new TextToSpeech(this, status -> {
			if (status == TextToSpeech.SUCCESS) {
				ttsOk = true;
				tts.setLanguage(Locale.ENGLISH);
			} else {
				ttsOk = false;
			}
		});

		// Save the last rules source
		LocalDate currentRulesSource = rulesService.getLatestRulesSource().getDate();
		storageService.setLastRulesSource(currentRulesSource);

		// Set theme
		boolean useLightTheme = storageService.getUseLightTheme();
		setTheme(useLightTheme);

		// Load ViewModel
		boolean showSymbols = storageService.getShowSymbols();
		viewModel.getShowSymbols().setValue(showSymbols);

		if (viewModel.getCurrentRules().getValue().isEmpty()) {
			RulesSource rulesSource = rulesService.getLatestRulesSource();

			useRules(rulesSource);
		}

		// Inflate layout and fragment
		setContentView(R.layout.main_activity);

		permissionsRequesterService.requestNotifications();

		// Configure action bar
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setLogo(R.drawable.ic_launcher_foreground);
			actionBar.setTitle(R.string.app_name);
			viewModel.getActionbarSubtitle().observe(this, actionBar::setSubtitle);
		}


		// Handle initial intent
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
		if (intent == null || intent.getAction() == null) {
			return;
		}

		boolean addToHistory = !intent.getBooleanExtra(Actions.IGNORE_HISTORY, false);

		switch (intent.getAction()) {
			case Intent.ACTION_SEARCH -> {
				String searchString = intent.getStringExtra(SearchManager.QUERY);
				String rootRule = intent.getStringExtra(Actions.ROOT_RULE);
				searchRules(searchString, rootRule);

				if (addToHistory) {
					pushHistoryItem(new HistoryItem(HistoryItem.Type.Search, new String[] { searchString, rootRule }));
				}

				logEvent(Events.SEARCH_RULES);
			}

			case Actions.ACTION_READ -> {
				if (ttsOk == null) {
					Toast.makeText(this, R.string.toast_tts_not_ready, Toast.LENGTH_SHORT).show();
				} else if (!ttsOk) {
					Toast.makeText(this, R.string.toast_tts_not_working, Toast.LENGTH_SHORT).show();
				} else {
					tts.speak(intent.getStringExtra(Actions.DATA), TextToSpeech.QUEUE_FLUSH, null, "rules");
					logEvent(Events.READ_RULE);
				}
			}

			case Actions.ACTION_NAVIGATE_RULE -> {
				String title = intent.getStringExtra(Actions.DATA);

				if (title.isEmpty()) {
					viewModel.getVisibleRules().setValue(viewModel.getCurrentRules().getValue());
					viewModel.getSelectedRuleTitle().setValue(null);
					viewModel.getSearchText().setValue(null);

					if (addToHistory) {
						pushHistoryItem(new HistoryItem(HistoryItem.Type.Rule, title));
					}
				} else {
					Rule existingRule = viewModel.getVisibleRules().getValue().stream()
						.filter(r -> r.getTitle().equals(title))
						.findAny().orElse(null);

					if (existingRule == null || !existingRule.getSubRules().isEmpty() || !isLastHistoryItemNavigation()) {
						List<Rule> rules = RuleUtils.getRuleAndParents(
							viewModel.getCurrentRules().getValue(),
							title
						).collect(Collectors.toCollection(ArrayList::new));

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
					viewModel.getSearchText().setValue(null);
				}
			}

			case Actions.ACTION_RANDOM_RULE -> {
				List<Rule> rules = viewModel.getCurrentRules().getValue();

				List<Rule> allRules = rules.stream()
					.flatMap(RuleUtils::flatten)
					.filter(r -> r.getSubRules().isEmpty())
					.collect(Collectors.toList());

				Random random = new Random();

				int seed = intent.getIntExtra(Actions.DATA, random.nextInt(Integer.MAX_VALUE));
				random.setSeed(seed);

				int rulesToSkip = random.nextInt(allRules.size());

				allRules.stream()
					.skip(rulesToSkip)
					.findFirst()
					.ifPresent(rule -> {
						List<Rule> newVisibleRules = RuleUtils.getRuleAndParents(
							viewModel.getCurrentRules().getValue(),
							rule.getTitle()
						).collect(Collectors.toList());

						viewModel.getVisibleRules().setValue(newVisibleRules);
						viewModel.getSelectedRuleTitle().setValue(null);
						viewModel.getSearchText().setValue(null);

						if (addToHistory) {
							pushHistoryItem(new HistoryItem(HistoryItem.Type.Random, seed));
						}
					});

				logEvent(Events.RANDOM_RULE);
			}

			case Actions.ACTION_CHANGE_THEME -> {
				boolean newUseLightTheme = !storageService.getUseLightTheme();

				setTheme(newUseLightTheme);

				storageService.setUseLightTheme(newUseLightTheme);

				recreate();
			}

			case Actions.ACTION_TOGGLE_SYMBOLS -> {
				boolean newShowSymbols = !storageService.getShowSymbols();

				viewModel.getShowSymbols().setValue(newShowSymbols);

				storageService.setShowSymbols(newShowSymbols);
			}
		}
	}

	private void pushHistoryItem(HistoryItem historyItem) {
		ArrayList<HistoryItem> newHistory = new ArrayList<>(viewModel.getHistory().getValue());
		newHistory.add(historyItem);
		viewModel.getHistory().setValue(newHistory);
	}

	private boolean popHistoryItem() {
		ArrayList<HistoryItem> newHistory = new ArrayList<>(viewModel.getHistory().getValue());

		if (newHistory.isEmpty()) {
			return false;
		}

		newHistory.remove(newHistory.size() - 1);

		while (!newHistory.isEmpty() && newHistory.get(newHistory.size() - 1).getType().equals(HistoryItem.Type.Ignored)) {
			newHistory.remove(newHistory.size() - 1);
		}

		if (newHistory.isEmpty()) {
			return false;
		}

		HistoryItem historyItem = newHistory.get(newHistory.size() - 1);
		viewModel.getHistory().setValue(newHistory);

		switch (historyItem.getType()) {
			case Rule -> {
				IntentSender.openRule(this, (String) historyItem.getValue(), true);
			}
			case Search -> {
				String[] values = (String[]) historyItem.getValue();
				String searchText = values[0];
				String rootRule = values.length == 2 ? values[1] : null;
				IntentSender.openSearch(this, searchText, rootRule, true);
			}
			case Random -> {
				IntentSender.openRandomRule(this, (int) historyItem.getValue(), true);
			}
		}

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

	private void searchRules(String searchText, @Nullable String rootRule) {
		List<Rule> rules = viewModel.getCurrentRules().getValue();
		List<Rule> rulesToSearch = rootRule == null ? rules : RuleUtils.getRuleAndSubsections(rules, rootRule);

		List<Rule> filteredRules = RulesSearchUtils.search(searchText, rulesToSearch);

		viewModel.getVisibleRules().setValue(filteredRules);
		viewModel.getSelectedRuleTitle().setValue(null);
		viewModel.getSearchText().setValue(searchText);
	}

	private void useRules(RulesSource rulesSource) {
		List<Rule> rules = rulesService.loadRules(rulesSource);

		viewModel.getCurrentRules().setValue(rules);
		viewModel.getVisibleRules().setValue(rules);
		viewModel.getSelectedRuleTitle().setValue(null);
		viewModel.getSearchText().setValue(null);
		viewModel.getHistory().setValue(Collections.singletonList(new HistoryItem(HistoryItem.Type.Rule, "")));

		viewModel.getActionbarSubtitle()
			.setValue(getString(R.string.action_bar_rules) + ": " + rulesSource.getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

		searchView.setQueryHint(getString(R.string.search_hint));
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		String[] columnNames = { SearchManager.SUGGEST_COLUMN_TEXT_1 };
		int[] viewIds = { android.R.id.text1 };
		CursorAdapter adapter = new SimpleCursorAdapter(
			this,
			android.R.layout.simple_list_item_1,
			null,
			columnNames,
			viewIds,
			0
		);
		searchView.setSuggestionsAdapter(adapter);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				String[] columns = new String[] { "_id", SearchManager.SUGGEST_COLUMN_TEXT_1 };

				MatrixCursor cursor = new MatrixCursor(columns);

				String newTextUpperCase = newText.toUpperCase();

				List<Rule> rules = viewModel.getCurrentRules().getValue();

				if (!newText.isEmpty()) {
					rules.get(rules.size() - 1).getSubRules().stream()
						.map(Rule::getTitle)
						.filter(r -> r.toUpperCase().contains(newTextUpperCase))
						.forEach(r -> cursor.newRow().add(r.hashCode()).add(r));
				}

				adapter.changeCursor(cursor);
				adapter.notifyDataSetChanged();

				return true;
			}
		});

		searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
			@Override
			public boolean onSuggestionSelect(int position) {
				return false;
			}

			@Override
			public boolean onSuggestionClick(int position) {
				adapter.getCursor().moveToPosition(position);
				String title = adapter.getCursor().getString(1);

				searchView.clearFocus();

				IntentSender.openRule(MainActivity.this, title, false);

				return true;
			}
		});

		menu.findItem(R.id.home).setOnMenuItemClickListener(view -> {
			IntentSender.openRule(this, "", false);
			return true;
		});

		menu.findItem(R.id.randomRule).setOnMenuItemClickListener(view -> {
			IntentSender.openRandomRule(this, null, false);

			return true;
		});

		menu.findItem(R.id.changeTheme).setOnMenuItemClickListener(view -> {
			IntentSender.changeTheme(this);

			return true;
		});

		menu.findItem(R.id.toggleSymbols).setOnMenuItemClickListener(view -> {
			IntentSender.toggleSymbols(this);

			return true;
		});

		menu.findItem(R.id.compareRules).setOnMenuItemClickListener(view -> {
			List<String> formattedRulesSources = rulesService.getRulesSources().stream()
				.map(rulesSource -> rulesSource.getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
				.collect(Collectors.toCollection(ArrayList::new));

			Collections.reverse(formattedRulesSources);

			showPicker(R.string.dialog_select_source_rules, formattedRulesSources)
				.thenAccept(selectedSourceIndex ->
					showPicker(R.string.dialog_select_target_rules, formattedRulesSources)
						.thenAccept(selectedTargetIndex -> {
							RulesSource sourceRulesSource = rulesService.getRulesSources().get(rulesService.getRulesSources().size() - selectedSourceIndex - 1);
							RulesSource targetRulesSource = rulesService.getRulesSources().get(rulesService.getRulesSources().size() - selectedTargetIndex - 1);

							List<Rule> sourceRules = rulesService.loadRules(sourceRulesSource);
							List<Rule> targetRules = rulesService.loadRules(targetRulesSource);

							List<Rule> comparedRules = rulesComparisonService.compareRules(sourceRules, targetRules);

							viewModel.getVisibleRules().setValue(comparedRules);
							viewModel.getSelectedRuleTitle().setValue(null);
							viewModel.getSearchText().setValue(null);

							pushHistoryItem(new HistoryItem(HistoryItem.Type.Ignored, null));

							logEvent(Events.COMPARE_RULES);
						})
				);

			return true;
		});

		menu.findItem(R.id.changeRules).setOnMenuItemClickListener(view -> {
			List<String> formattedRulesSources = rulesService.getRulesSources().stream()
				.map(rulesSource -> rulesSource.getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
				.collect(Collectors.toCollection(ArrayList::new));

			Collections.reverse(formattedRulesSources);

			showPicker(R.string.dialog_select_rules, formattedRulesSources)
				.thenAccept(selectedIndex -> {
					RulesSource rulesSource = rulesService.getRulesSources().get(rulesService.getRulesSources().size() - selectedIndex - 1);

					useRules(rulesSource);

					logEvent(Events.CHANGE_RULES);
				});

			return true;
		});

		menu.findItem(R.id.about).setOnMenuItemClickListener(view -> {
			new AboutFragment().show(getSupportFragmentManager(), null);

			return true;
		});

		return super.onCreateOptionsMenu(menu);
	}

	private CompletableFuture<Integer> showPicker(int titleId, List<String> pickElement) {
		CompletableFuture<Integer> future = new CompletableFuture<>();

		new AlertDialog.Builder(this)
			.setTitle(titleId)
			.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
			})
			.setItems(pickElement.toArray(new String[0]), (dialog, which) ->
				future.complete(which)
			)
			.show();

		return future;
	}

	@Override
	public void onBackPressed() {
		if (!popHistoryItem()) {
			super.onBackPressed();
		}
	}
}

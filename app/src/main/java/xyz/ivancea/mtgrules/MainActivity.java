package xyz.ivancea.mtgrules;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Application;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import xyz.ivancea.mtgrules.model.Rule;
import xyz.ivancea.mtgrules.model.RulesSource;
import xyz.ivancea.mtgrules.services.RulesService;
import xyz.ivancea.mtgrules.ui.main.MainFragment;
import xyz.ivancea.mtgrules.ui.main.MainViewModel;

public class MainActivity extends AppCompatActivity {

    @Inject
    RulesService rulesService;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            searchRules(intent.getStringExtra(SearchManager.QUERY));
        }
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

        return super.onCreateOptionsMenu(menu);
    }
}

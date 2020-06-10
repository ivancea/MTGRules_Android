package xyz.ivancea.mtgrules.ui.main;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import xyz.ivancea.mtgrules.MainActivity;
import xyz.ivancea.mtgrules.MtgRulesApplication;
import xyz.ivancea.mtgrules.R;
import xyz.ivancea.mtgrules.model.Rule;
import xyz.ivancea.mtgrules.services.RulesService;

public class MainFragment extends Fragment {

    private List<Rule> rules = Collections.emptyList();

    private MainViewModel viewModel;

    private RecyclerView recyclerView;

    private RuleListAdapter recyclerViewAdapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Inject
    RulesService rulesService;

    @Override
    public void onAttach(Context context) {
        ((MtgRulesApplication) getActivity().getApplication()).appComponent.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        recyclerView = rootView.findViewById(R.id.rulesList);
        recyclerViewAdapter = new RuleListAdapter(rules);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        List<Rule> rules = new ArrayList<>();

        for (int i=0; i<100; i++) {
            rules.add(new Rule("200.", "Test 2a aaaaaaaaaa aaaaa 710.1b aaaa a aaaaaaa a aaa aaaaaaaa a\nExample: asd das 100.5 aaaaaaaaaaaa a a aa a a "));
        }

        viewModel.setCurrentRules(rules);
        viewModel.setVisibleRules(rules);
        recyclerViewAdapter.setRules(rules);
    }

}

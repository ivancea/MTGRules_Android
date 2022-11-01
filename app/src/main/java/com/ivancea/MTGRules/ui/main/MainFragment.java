package com.ivancea.MTGRules.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivancea.MTGRules.MtgRulesApplication;
import com.ivancea.MTGRules.R;
import com.ivancea.MTGRules.model.Rule;

import java.util.List;
import java.util.stream.IntStream;

public class MainFragment extends Fragment {

    private MainViewModel viewModel;

    private RecyclerView recyclerView;

    private RuleListAdapter recyclerViewAdapter;

    private LinearLayoutManager layoutManager;

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
        recyclerViewAdapter = new RuleListAdapter(getContext());

        layoutManager = new LinearLayoutManager(getActivity());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
            recyclerView.getContext(),
            layoutManager.getOrientation()
        );

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(recyclerViewAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        viewModel.getVisibleRules().observe(getViewLifecycleOwner(), rules -> {
            recyclerViewAdapter.setRules(rules);
        });

        viewModel.getSearchText().observe(getViewLifecycleOwner(), searchText -> {
            recyclerViewAdapter.setSearchText(searchText);
        });

        viewModel.getSelectedRuleTitle().observe(getViewLifecycleOwner(), ruleTitle -> {
            recyclerViewAdapter.setSelectedRuleTitle(ruleTitle);

            if (ruleTitle == null) {
                layoutManager.scrollToPosition(0);
            } else {
                List<Rule> rules = viewModel.getVisibleRules().getValue();

                IntStream.range(0, rules.size())
                    .filter(i -> rules.get(i).getTitle().equals(ruleTitle))
                    .findFirst()
                    .ifPresent(i -> layoutManager.scrollToPositionWithOffset(i, 50));
            }
        });
    }

}

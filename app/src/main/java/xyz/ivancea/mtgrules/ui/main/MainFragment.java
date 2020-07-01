package xyz.ivancea.mtgrules.ui.main;

import androidx.lifecycle.ViewModelProvider;
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
import xyz.ivancea.mtgrules.model.RulesSource;
import xyz.ivancea.mtgrules.services.RulesService;

public class MainFragment extends Fragment {

    private MainViewModel viewModel;

    private RecyclerView recyclerView;

    private RuleListAdapter recyclerViewAdapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

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
        recyclerViewAdapter = new RuleListAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
    }

}

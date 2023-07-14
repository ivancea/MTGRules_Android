package com.ivancea.MTGRules.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ivancea.MTGRules.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.stream.IntStream

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var viewModel: MainViewModel? = null
    private var recyclerView: RecyclerView? = null
    private var recyclerViewAdapter: RuleListAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.main_fragment, container, false)
        recyclerView = rootView.findViewById(R.id.rulesList)
        recyclerViewAdapter = RuleListAdapter(activity)
        layoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView!!.getContext(),
            layoutManager!!.orientation
        )
        recyclerView!!.setLayoutManager(layoutManager)
        recyclerView!!.addItemDecoration(dividerItemDecoration)
        recyclerView!!.setAdapter(recyclerViewAdapter)
        return rootView
    }

    override fun onStart() {
        super.onStart()

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        lifecycleScope.launch {
            viewModel!!.showSymbols
                .collect { showSymbols ->
                    recyclerViewAdapter!!.setShowSymbols(
                        showSymbols
                    )
                }
        }

        lifecycleScope.launch {
            viewModel!!.visibleRules
                .collect { rules ->
                    recyclerViewAdapter!!.setRules(
                        rules
                    )
                }
        }

        lifecycleScope.launch {
            viewModel!!.searchText
                .collect { searchText ->
                    recyclerViewAdapter!!.setSearchText(
                        searchText
                    )
                }
        }

        lifecycleScope.launch {
            viewModel!!.selectedRuleTitle
                .collect { ruleTitle ->
                    recyclerViewAdapter!!.setSelectedRuleTitle(ruleTitle)
                    if (ruleTitle == null) {
                        layoutManager!!.scrollToPosition(0)
                    } else {
                        val rules = viewModel!!.visibleRules.value
                        IntStream.range(0, rules.size)
                            .filter { i: Int -> rules[i].title == ruleTitle }
                            .findFirst()
                            .ifPresent { i: Int ->
                                layoutManager!!.scrollToPositionWithOffset(
                                    i,
                                    50
                                )
                            }
                    }
                }
        }
    }
}

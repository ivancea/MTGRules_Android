package com.ivancea.MTGRules.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;

import com.ivancea.MTGRules.model.HistoryItem;
import com.ivancea.MTGRules.model.Rule;

@Getter
public class MainViewModel extends ViewModel {
    @NonNull
    private final MutableLiveData<List<Rule>> currentRules = new MutableLiveData<>(Collections.emptyList());
    @NonNull
    private final MutableLiveData<List<Rule>> visibleRules = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<String> selectedRuleTitle = new MutableLiveData<>(null);
    private final MutableLiveData<String> searchText = new MutableLiveData<>(null);

    @NonNull
    private final MutableLiveData<List<HistoryItem>> history = new MutableLiveData<>(Collections.emptyList());

    @NonNull
    private final MutableLiveData<String> actionbarSubtitle = new MutableLiveData<>(null);
}

package com.ivancea.MTGRules.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import com.ivancea.MTGRules.model.Rule;

@Getter
public class MainViewModel extends ViewModel {
    private final MutableLiveData<List<Rule>> currentRules = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<List<Rule>> visibleRules = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<String> selectedRuleTitle = new MutableLiveData<>(null);
}

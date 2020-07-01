package xyz.ivancea.mtgrules.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xyz.ivancea.mtgrules.model.Rule;

@Getter
public class MainViewModel extends ViewModel {
    private final MutableLiveData<List<Rule>> currentRules = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<List<Rule>> visibleRules = new MutableLiveData<>(Collections.emptyList());
}

package xyz.ivancea.mtgrules.ui.main;

import androidx.lifecycle.ViewModel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import xyz.ivancea.mtgrules.model.Rule;

@Getter
@Setter
public class MainViewModel extends ViewModel {
    private List<Rule> currentRules;
    private List<Rule> visibleRules;
}

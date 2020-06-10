package xyz.ivancea.mtgrules.ui.main;

import androidx.lifecycle.ViewModel;

import java.util.List;

import lombok.Data;
import xyz.ivancea.mtgrules.model.Rule;

@Data
public class MainViewModel extends ViewModel {
    private List<Rule> currentRules;
    private List<Rule> visibleRules;
}

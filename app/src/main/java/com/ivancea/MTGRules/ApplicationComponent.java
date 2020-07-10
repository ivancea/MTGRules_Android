package com.ivancea.MTGRules;

import dagger.Component;
import com.ivancea.MTGRules.ui.main.MainFragment;

@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(MainActivity mainActivity);
    void inject(MainFragment mainFragment);
}

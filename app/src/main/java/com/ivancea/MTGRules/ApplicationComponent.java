package com.ivancea.MTGRules;

import com.ivancea.MTGRules.receivers.MyPackageReplacedReceiver;
import com.ivancea.MTGRules.ui.main.MainFragment;

import dagger.Component;

@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(MainActivity mainActivity);
    void inject(MainFragment mainFragment);
    void inject(MyPackageReplacedReceiver myPackageReplacedReceiver);
}

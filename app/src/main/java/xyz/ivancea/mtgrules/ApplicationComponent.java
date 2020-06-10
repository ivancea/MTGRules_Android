package xyz.ivancea.mtgrules;

import dagger.Component;
import xyz.ivancea.mtgrules.ui.main.MainFragment;

@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(MainActivity mainActivity);
    void inject(MainFragment mainFragment);
}

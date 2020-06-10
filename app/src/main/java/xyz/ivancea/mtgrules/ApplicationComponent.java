package xyz.ivancea.mtgrules;

import dagger.Component;
import xyz.ivancea.mtgrules.ui.main.MainFragment;

@Component
public interface ApplicationComponent {
    void inject(MainActivity mainActivity);
    void inject(MainFragment mainFragment);
}

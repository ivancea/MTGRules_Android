package xyz.ivancea.mtgrules;

import android.app.Application;

public class MtgRulesApplication extends Application {
    public final ApplicationComponent appComponent = DaggerApplicationComponent.builder()
        .applicationModule(new ApplicationModule(this))
        .build();
}

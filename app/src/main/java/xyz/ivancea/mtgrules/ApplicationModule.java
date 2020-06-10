package xyz.ivancea.mtgrules;

import android.content.Context;

import androidx.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
	private Context context;

	public ApplicationModule(@NonNull Context context) {
		this.context = context;
	}

	@Provides
	@NonNull
	public Context context(){
		return context;
	}
}

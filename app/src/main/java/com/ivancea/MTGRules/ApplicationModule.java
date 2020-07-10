package com.ivancea.MTGRules;

import android.content.Context;

import androidx.annotation.NonNull;

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

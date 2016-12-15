package com.chall.qonto;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatDelegate;
import android.text.format.Formatter;

import com.bumptech.glide.Glide;
import com.chall.qonto.api.RestClient;
import com.chall.qonto.utils.EventBusModule;

import timber.log.Timber;

public final class AppApplication extends Application {
	@Override
	public final void onCreate() {
		super.onCreate();

		Timber.plant(new Timber.DebugTree());

		RestClient.initialize(this);

		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

		AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
	}

	@Override
	public final void onLowMemory() {
		super.onLowMemory();

		EventBusModule.clear();

		Glide.get(this).clearMemory();
	}

	@Override
	public final void onTrimMemory(final int level) {
		super.onTrimMemory(level);

		if ((level >= Application.TRIM_MEMORY_MODERATE && level <= Application.TRIM_MEMORY_COMPLETE) ||
			(level >= Application.TRIM_MEMORY_RUNNING_MODERATE && level <= Application.TRIM_MEMORY_RUNNING_CRITICAL)) {
			Timber.d("Number of bytes actually released %s",
					 Formatter.formatFileSize(this, SQLiteDatabase.releaseMemory()));
		}
	}
}

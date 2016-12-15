package com.chall.qonto.datastore;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.chall.qonto.data.DatabaseHelper;

import timber.log.Timber;

public abstract class Storage implements DatabaseHelper.Listener {
	final DatabaseHelper dbHelper;

	Storage(@NonNull final DatabaseHelper helper) {
		(this.dbHelper = helper).setListener(this);
	}

	@NonNull
	public final DatabaseHelper getDatabaseHelper() {
		return this.dbHelper;
	}

	@Override
	public final void onOpen(@NonNull final SQLiteDatabase db) {
		Timber.d("%s is opened", this.getClass().getSimpleName());
	}

	@Override
	public final void onCreate(@NonNull final SQLiteDatabase db) {
		Timber.d("%s is created", this.getClass().getSimpleName());
	}
}

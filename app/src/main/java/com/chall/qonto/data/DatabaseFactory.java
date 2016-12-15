package com.chall.qonto.data;

import android.content.Context;

import com.chall.qonto.datastore.ApplicationStorage;
import com.chall.qonto.datastore.Storage;

public final class DatabaseFactory {
	private static Storage sAppStorage;

	private DatabaseFactory() {}

	public static Storage getApplicationStorage(final Context context) {
		if (null == DatabaseFactory.sAppStorage) {
			DatabaseFactory.sAppStorage = new ApplicationStorage(context);
		}

		return DatabaseFactory.sAppStorage;
	}

	static DatabaseHelper getApplicationDatabaseHelper(final Context context) {
		return DatabaseFactory.getApplicationStorage(context).getDatabaseHelper();
	}
}

package com.chall.qonto.data.migration;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

public interface SchemaMigration {
	int getNewDbVersion();
	void upgrade(@NonNull final SQLiteDatabase db);
}

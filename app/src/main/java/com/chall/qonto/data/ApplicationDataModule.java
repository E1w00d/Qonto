package com.chall.qonto.data;

import android.content.Context;

import com.chall.qonto.data.migration.CreateInitialApplicationTableMigration;
import com.chall.qonto.data.migration.SchemaMigration;

import java.util.Arrays;
import java.util.List;

public final class ApplicationDataModule implements DataModule {
	@Override
	public final int getSchemaVersion() {
		return 1;
	}

	@Override
	public final List<SchemaMigration> getSchemaMigrations() {
		return Arrays.asList(new SchemaMigration[] {new CreateInitialApplicationTableMigration(),});
	}

	public final DatabaseHelper getDatabaseHelper(final Context context) {
		return new DatabaseHelper(context,
								  "qonto.db",
								  this.getSchemaVersion(),
								  this.getSchemaMigrations());
	}
}

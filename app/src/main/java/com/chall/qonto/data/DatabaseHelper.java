package com.chall.qonto.data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chall.qonto.data.migration.SchemaMigration;
import com.chall.qonto.utils.StringUtils;

import java.util.List;

import okhttp3.internal.Util;
import timber.log.Timber;

public final class DatabaseHelper extends SQLiteOpenHelper {
	public interface Listener {
		void onOpen(@NonNull final SQLiteDatabase db);
		void onCreate(@NonNull final SQLiteDatabase db);
	}

	private Listener listener;

	private final int dbVersion;
	private final List<SchemaMigration> migrations;

	DatabaseHelper(@NonNull final Context context, @Nullable final String name,
				   @IntRange(from=1) final int databaseVersion,
				   @NonNull final List<SchemaMigration> migrations) {
		super(context, name, null, databaseVersion);

		this.migrations = migrations;
		this.dbVersion = databaseVersion;
	}

	public final void setListener(@Nullable final Listener listener) {
		this.listener = listener;
	}

	@Override
	public final void onCreate(final SQLiteDatabase db) {
		Timber.d("Create %s at version %d", this.getDatabaseName(), this.dbVersion);

		for (final SchemaMigration migration : this.migrations) {
			if (migration.getNewDbVersion() <= this.dbVersion) {
				migration.upgrade(db);
			}
		}

		if (null != this.listener) {
			this.listener.onCreate(db);
		}
	}

	@Override
	public final void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		Timber.d("Upgrading %s from version %d to version %d", this.getDatabaseName(), oldVersion, newVersion);

		for (final SchemaMigration migration : this.migrations) {
			final int schemaVersion = migration.getNewDbVersion();

			if (schemaVersion > oldVersion) {
				if (schemaVersion <= newVersion) {
					Timber.d("Applying schema migration for %s version: %d",
							 this.getDatabaseName(), schemaVersion);

					migration.upgrade(db);
				}
				else {
					return;
				}
			}
		}
	}

	@Override
	public final void onDowngrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		Timber.d("Downgrading %s from version %d to version %d by clearing and recreating db",
				 this.getDatabaseName(), oldVersion, newVersion);

		this.clear(db);

		this.onCreate(db);
	}

	@Override
	public final void onOpen(final SQLiteDatabase db) {
		super.onOpen(db);

		if (null != this.listener) {
			this.listener.onOpen(db);
		}
	}

	@Override
	public final void onConfigure(final SQLiteDatabase db) {
		super.onConfigure(db);

		if (!db.isReadOnly()) {
			Cursor cursor = null;

			for (final String sql : new String[] {"PRAGMA foreign_keys=ON;",
												  "PRAGMA temp_store=MEMORY;",
												  "PRAGMA journal_mode=MEMORY;",
												  "PRAGMA case_sensitive_like=OFF;",}) {
				try {
					if (null != (cursor = db.rawQuery(sql, null))) {
						cursor.moveToFirst();
					}
				}
				catch (final SQLException ex) {
					Timber.e(ex, "Query %s has failed", sql);
				}
				finally {
					Util.closeQuietly(cursor);
				}
			}
		}

		db.setMaxSqlCacheSize(StrictMath.min(50, SQLiteDatabase.MAX_SQL_CACHE_SIZE));
	}

	private void clear(final SQLiteDatabase db) {
		final Cursor cursor = db.query("sqlite_master", new String[] {"type", "name",},
									   null, null, null, null, null);

		final StringBuilder sb = StringUtils.acquireStringBuilder();

		try {
			if (null != cursor && cursor.moveToFirst()) {
				String name;

				do {
					name = cursor.getString(1);

					if (!name.startsWith("android_") && !name.startsWith("sqlite_")) {
						try {
							db.execSQL(sb.append("DROP ")
										 .append(cursor.getString(0))
										 .append(" IF EXISTS ")
										 .append(name)
										 .toString());
						}
						catch (final SQLiteException ex) {
							Timber.e(ex, "Error executing sql '%s'", sb);
						}
						finally {
							sb.setLength(0);
						}
					}
				} while (cursor.moveToNext());
			}
		}
		finally {
			Util.closeQuietly(cursor);

			StringUtils.releaseStringBuilder(sb);
		}
	}
}

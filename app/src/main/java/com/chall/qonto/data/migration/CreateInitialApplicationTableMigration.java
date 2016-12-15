package com.chall.qonto.data.migration;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.chall.qonto.data.CreateTableSqlBuilder;

public final class CreateInitialApplicationTableMigration implements SchemaMigration {
	@Override
	public final int getNewDbVersion() {
		return 1;
	}

	@Override
	public final void upgrade(@NonNull final SQLiteDatabase db) {
		db.execSQL(new CreateTableSqlBuilder("address").addTextColumn("city")
													   .addTextColumn("suite")
													   .addTextColumn("street")
													   .addTextColumn("zip_code")
													   .addRealColumn("lat")
													   .addRealColumn("lng")
													   .build());

		db.execSQL(new CreateTableSqlBuilder("company").addTextColumn("bs")
													   .addTextColumn("name")
													   .addTextColumn("catch_phrase")
													   .build());

		db.execSQL(new CreateTableSqlBuilder("users").addTextColumn("user_id")
													 .addTextColumn("first_name")
													 .addTextColumn("last_name")
													 .addTextColumn("full_name")
													 .addTextColumn("email")
													 .addTextColumn("phone")
													 .addTextColumn("website")
													 .addLongColumn("address")
													 .addLongColumn("company")
													 .build());

		db.execSQL(CreateTableSqlBuilder.createIndexSql("users", "user_id"));

		db.execSQL(new CreateTableSqlBuilder("albums").addTextColumn("user_id")
													  .addTextColumn("album_id")
													  .addTextColumn("title")
													  .build());

		db.execSQL(CreateTableSqlBuilder.createIndexSql("albums", "album_id"));

		db.execSQL(new CreateTableSqlBuilder("photos").addTextColumn("album_id")
													  .addTextColumn("photo_id")
													  .addTextColumn("title")
													  .addTextColumn("url")
													  .addTextColumn("thumbnail")
													  .build());

		db.execSQL(CreateTableSqlBuilder.createIndexSql("photos", "photo_id"));
	}
}

package com.chall.qonto.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chall.qonto.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class CreateTableSqlBuilder extends SqlBuilder {
	private final List<Column> columns = new ArrayList<>();

	public CreateTableSqlBuilder(@NonNull final String tableName) {
		this.name = tableName;
	}

	public final CreateTableSqlBuilder addTextColumn(@NonNull final String columnName) {
		return this.addColumn(columnName, ColumnType.TEXT);
	}

	public final CreateTableSqlBuilder addRealColumn(@NonNull final String columnName) {
		return this.addRealColumnWithDefault(columnName, null);
	}

	private CreateTableSqlBuilder addRealColumnWithDefault(@NonNull final String columnName, @Nullable final String defaultValue) {
		return this.addColumn(columnName, ColumnType.REAL, defaultValue);
	}

	public final CreateTableSqlBuilder addLongColumn(@NonNull final String columnName) {
		return this.addLongColumnWithDefault(columnName, null);
	}

	private CreateTableSqlBuilder addLongColumnWithDefault(@NonNull final String columnName, @Nullable final String defaultValue) {
		return this.addColumn(columnName, ColumnType.INTEGER, defaultValue);
	}

	@NonNull
	@Override
	public final String build() {
		final StringBuilder sb = StringUtils.acquireStringBuilder();

		try {
			sb.append("CREATE TABLE IF NOT EXISTS ")
			  .append(this.name)
			  .append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT");

			for (final Column column : this.columns) {
				sb.append(", ")
				  .append(column.name)
				  .append(' ')
				  .append(column.type)
				  .append(" DEFAULT ")
				  .append(this.getDefaultForColumn(column));
			}

			return sb.append(");").toString();
		}
		finally {
			this.columns.clear();

			StringUtils.releaseStringBuilder(sb);
		}
	}

	private CreateTableSqlBuilder addColumn(final String columnName, final ColumnType columnType) {
		return this.addColumn(columnName, columnType, null);
	}

	private CreateTableSqlBuilder addColumn(final String columnName, final ColumnType columnType, final String defaultValue) {
		this.columns.add(new Column(columnName, columnType, defaultValue));
		return this;
	}

	private String getDefaultForColumn(final Column column) {
		return !TextUtils.isEmpty(column.defaultValue)
					? column.defaultValue
					: column.type.defaultValue;
	}

	public static String createIndexSql(@NonNull final String tableName, @NonNull final String columnName) {
		final StringBuilder sb = StringUtils.acquireStringBuilder();

		try {
			return sb.append("CREATE INDEX ")
					 .append(tableName)
					 .append('_')
					 .append(columnName)
					 .append(" ON ")
					 .append(tableName)
					 .append(" (")
					 .append(columnName)
					 .append(");")
					 .toString();
		}
		finally {
			StringUtils.releaseStringBuilder(sb);
		}
	}

	private enum ColumnType {
		INTEGER("0"),
		REAL("0.0"),
		TEXT("NULL");

		private String defaultValue;

		ColumnType(final String defaultValue) {
			this.defaultValue = defaultValue;
		}
	}

	private static final class Column {
		private final String name;
		private final String defaultValue;

		private final ColumnType type;

		private Column(final String name, final ColumnType type, final String defaultValue) {
			this.type = type;

			this.name = name;
			this.defaultValue = defaultValue;
		}
	}
}

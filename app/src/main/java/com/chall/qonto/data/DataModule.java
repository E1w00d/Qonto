package com.chall.qonto.data;

import com.chall.qonto.data.migration.SchemaMigration;

import java.util.List;

interface DataModule {
	int getSchemaVersion();
	List<SchemaMigration> getSchemaMigrations();
}

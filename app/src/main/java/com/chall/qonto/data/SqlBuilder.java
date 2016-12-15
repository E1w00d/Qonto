package com.chall.qonto.data;

import android.support.annotation.NonNull;

abstract class SqlBuilder {
	String name;

	@NonNull
	public abstract String build();
}

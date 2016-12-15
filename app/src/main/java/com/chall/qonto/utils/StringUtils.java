package com.chall.qonto.utils;

import android.support.annotation.NonNull;

public final class StringUtils {
	private StringUtils() {}

	private static final ThreadLocal<StringBuilderPool> sStringBuilderPool = new ThreadLocal<StringBuilderPool>() {
		@Override
		protected final StringBuilderPool initialValue() {
			return new StringBuilderPool();
		}
	};

	public static StringBuilder acquireStringBuilder() {
		return StringUtils.sStringBuilderPool.get().acquire();
	}

	public static void releaseStringBuilder(@NonNull final StringBuilder sb) {
		StringUtils.sStringBuilderPool.get().release(sb);
	}

	private static final class StringBuilderPool {
		private int mAcquiredCount;
		private final StringBuilder mStringBuilder;

		private StringBuilderPool() {
			this.mStringBuilder = new StringBuilder(256);
		}

		private StringBuilder acquire() {
			if (1 == ++this.mAcquiredCount) {
				return this.mStringBuilder;
			}

			return new StringBuilder(256);
		}

		private void release(final StringBuilder sb) {
			if (1 > this.mAcquiredCount) {
				throw new IllegalStateException("Cannot release more StringBuilders than have been acquired");
			}
			else if (1 != this.mAcquiredCount || sb == this.mStringBuilder) {
				sb.setLength(0);

				--this.mAcquiredCount;
			}
			else {
				throw new IllegalArgumentException("Tried to release wrong StringBuilder instance");
			}
		}
	}
}

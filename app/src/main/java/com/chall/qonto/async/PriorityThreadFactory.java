package com.chall.qonto.async;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.chall.qonto.utils.Preconditions;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class PriorityThreadFactory implements ThreadFactory {
	private final String mName;
	private final int mPriority;
	private final ThreadFactory mDefaultFactory;

	private final AtomicInteger mCount = new AtomicInteger(1);

	public PriorityThreadFactory(final String name, @IntRange(from=-20, to=19) final int priority) {
		this.mPriority = priority;

		this.mDefaultFactory = Executors.defaultThreadFactory();

		this.mName = Preconditions.checkNotEmpty(name, "Name must not be null or empty");
	}

	@Override
	public final Thread newThread(@NonNull final Runnable r) {
		final Thread thread = this.mDefaultFactory.newThread(new PriorityRunnable(r, this.mPriority));

		thread.setName(String.format("%s [%d]", this.mName, this.mCount.getAndIncrement()));

		return thread;
	}
}

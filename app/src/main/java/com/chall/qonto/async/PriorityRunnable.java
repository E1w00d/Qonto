package com.chall.qonto.async;

import android.os.Process;

import timber.log.Timber;

final class PriorityRunnable implements Runnable {
	private final int mPriority;
	private final Runnable mRunnable;

	PriorityRunnable(final Runnable runnable, final int priority) {
		this.mRunnable = runnable;

		this.mPriority = priority;
	}

	@Override
	public final void run() {
		try {
			Process.setThreadPriority(this.mPriority);

			this.mRunnable.run();
		}
		catch (final Throwable ex) {
			Timber.e(ex, "Task execution has failed");
		}
	}
}

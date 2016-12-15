package com.chall.qonto.async;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.concurrent.Executor;

import timber.log.Timber;

final class SequentialExecutor implements Executor {
	private boolean executingRunnable;

	private final Executor underlyingExecutor;

	private final Object lock = new Object();
	private final LinkedList<Runnable> runnableQueue = new LinkedList<>();

	private final Runnable executeRunnable = new Runnable() {
		@Override
		public final void run() {
			while (SequentialExecutor.this.executingRunnable) {
				synchronized (SequentialExecutor.this.lock) {
					final Runnable runnable = SequentialExecutor.this.runnableQueue.poll();

					if (SequentialExecutor.this.executingRunnable = null != runnable) {
						try {
							//noinspection ConstantConditions
							runnable.run();
						}
						catch (final Throwable ex) {
							Timber.e(ex, "Failed to execute sequential task");
						}
					}
				}
			}
		}
	};

	public SequentialExecutor(@QualifierAnnotations.Parallel final Executor executor) {
		this.underlyingExecutor = executor;
	}

	@Override
	public final void execute(@NonNull final Runnable command) {
		boolean shouldExecuteRunnable = false;

		synchronized (this.lock) {
			this.runnableQueue.add(command);

			if (!this.executingRunnable) {
				this.executingRunnable = shouldExecuteRunnable = true;
			}
		}

		if (shouldExecuteRunnable) {
			this.underlyingExecutor.execute(this.executeRunnable);
		}
	}
}

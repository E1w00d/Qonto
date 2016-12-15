package com.chall.qonto.async;

import android.os.Process;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class AsyncModule {
	@QualifierAnnotations.Parallel
	public static Executor getParallelExecutor() {
		return new ThreadPoolExecutor(4, 10, 1L, TimeUnit.SECONDS,
									  new LinkedBlockingQueue<Runnable>(128),
									  new PriorityThreadFactory("api-pool",
																Process.THREAD_PRIORITY_LESS_FAVORABLE),
									  new ThreadPoolExecutor.CallerRunsPolicy());
	}

	@QualifierAnnotations.Sequential
	public static Executor getSequentialExecutor(@QualifierAnnotations.Parallel final Executor executor) {
		return new SequentialExecutor(executor);
	}
}

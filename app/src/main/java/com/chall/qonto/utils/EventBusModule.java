package com.chall.qonto.utils;

import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;

import com.chall.qonto.api.ResponseEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;

import timber.log.Timber;

public final class EventBusModule {
	private static final SparseArrayCompat<Object> sQueue = new SparseArrayCompat<>();

	private EventBusModule() {}

	public static void clear() {
		EventBusModule.sQueue.clear();

		EventBus.getDefault().removeAllStickyEvents();
	}

	public static void post(@NonNull final Object event) {
		if (EventBus.getDefault().hasSubscriberForEvent(event.getClass())) {
			EventBus.getDefault().post(event);
		}
		else {
			EventBusModule.sQueue.append(event instanceof ResponseEvent
											 ? ((ResponseEvent)event).getRequestId()
											 : Utils.elapsedRealtime(),
										 event);

			Timber.tag("PubSub")
				  .wtf("No subscriber for event: %s, queue size: %d",
					   event.getClass().getSimpleName(), EventBusModule.sQueue.size());
		}
	}

	public static void register(@NonNull final Object subscriber) {
		try {
			if (!EventBus.getDefault().isRegistered(subscriber)) {
				EventBus.getDefault().register(subscriber);
			}

			//noinspection StatementWithEmptyBody
			for (int idx = EventBusModule.sQueue.size(); --idx >= 0;
				 EventBusModule.post(EventBusModule.sQueue.valueAt(idx)));
		}
		catch (final IllegalStateException | NoClassDefFoundError | EventBusException | ArrayIndexOutOfBoundsException ex) {
			Timber.tag("PubSub").wtf(ex, "Subscription to event bus");
		}
		finally {
			EventBusModule.sQueue.clear();
		}
	}

	public static void unregister(@NonNull final Object subscriber) {
		try {
			EventBus.getDefault().unregister(subscriber);
		}
		catch (final IllegalStateException | NoClassDefFoundError | EventBusException ex) {
			Timber.tag("PubSub").wtf(ex, "Unsubscription to event bus");
		}
	}
}

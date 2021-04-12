package com.gbsoft.smartpatient.utils;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

public class EventObserver<T> implements Observer<Event<T>> {
    private final Listener<T> listener;

    public EventObserver(Listener<T> listener) {
        this.listener = listener;
    }

    @Override
    public void onChanged(@Nullable Event<T> tEvent) {
        if (tEvent == null) return;
        if (listener == null) return;

        T content = tEvent.getContentIfNotHandled();
        if (content == null) return;
        listener.onUnhandledContent(content);
    }

    public interface Listener<T> {
        void onUnhandledContent(T data);
    }
}

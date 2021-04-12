package com.gbsoft.smartpatient.utils;

public class Event<T> {
    private boolean hasBeenHandled = false;
    private final T content;

    public Event(T content) {
        this.content = content;
    }

    public T getContentIfNotHandled() {
        if (hasBeenHandled)
            return null;
        else {
            hasBeenHandled = true;
            return content;
        }

    }

    public T peekContent() {
        return content;
    }
}

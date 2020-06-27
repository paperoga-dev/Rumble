package com.github.rumble.api.simple;

import com.github.rumble.api.AbstractCompletionInterface;

public interface CompletionInterface<T> extends AbstractCompletionInterface<T> {
    void onSuccess(T result);
}

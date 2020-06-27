package com.github.rumble.api.array;

import com.github.rumble.api.AbstractCompletionInterface;

import java.util.List;

public interface CompletionInterface<T, W extends ContentInterface<T>> extends AbstractCompletionInterface<W> {
    void onSuccess(List<T> result, int offset, int limit, int count);
}

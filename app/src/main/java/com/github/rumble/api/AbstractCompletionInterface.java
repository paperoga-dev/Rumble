package com.github.rumble.api;

import com.github.rumble.exception.BaseException;

public interface AbstractCompletionInterface<T> {
    void onFailure(BaseException e);
}

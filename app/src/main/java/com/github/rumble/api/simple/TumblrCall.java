package com.github.rumble.api.simple;

import android.os.Handler;
import android.os.Looper;

import com.github.rumble.api.Api;

import org.scribe.model.OAuthRequest;

public class TumblrCall<T> extends com.github.rumble.api.TumblrCall<T> {
    private final CompletionInterface<T> onCompletion;

    protected TumblrCall(Api<T> api, OAuthRequest request, CompletionInterface<T> onCompletion) {
        super(api, request, onCompletion);

        this.onCompletion = onCompletion;
    }

    @Override
    protected void process(final T output) {
        new Handler(Looper.getMainLooper()).post(
                new Runnable() {
                    @Override
                    public void run() {
                        onCompletion.onSuccess(output);
                    }
                });
    }
}

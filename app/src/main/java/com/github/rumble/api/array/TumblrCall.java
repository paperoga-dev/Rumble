package com.github.rumble.api.array;

import android.os.Handler;
import android.os.Looper;

import org.scribe.model.OAuthRequest;

public class TumblrCall<T, W extends ContentInterface<T>> extends com.github.rumble.api.TumblrCall<W> {
    private final CompletionInterface<T, W> onCompletion;
    private final Api<T, W> api;

    protected TumblrCall(Api<T, W> api, OAuthRequest request, CompletionInterface<T, W> onCompletion) {
        super(api, request, onCompletion);

        this.api = api;
        this.onCompletion = onCompletion;
    }

    @Override
    protected void process(final W output) {
        new Handler(Looper.getMainLooper()).post(
                new Runnable() {
                    @Override
                    public void run() {
                        onCompletion.onSuccess(
                                output.getItems(),
                                api.getOffset(),
                                api.getLimit(),
                                output.getCount()
                        );
                    }
                });
    }
}

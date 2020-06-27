package com.github.rumble.api.simple;

import android.content.Context;

import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.Map;

public abstract class Api<T> extends com.github.rumble.api.Api<T> implements ApiInterface<T> {

    protected Api(Context context,
                  OAuthService service,
                  Token authToken,
                  String appId,
                  String appVersion) {
        super(context, service, authToken, appId, appVersion);
    }

    @Override
    public Runnable call(Map<String, String> queryParams, CompletionInterface<T> onCompletion) {
        return new TumblrCall<>(this, setupCall(queryParams), onCompletion);
    }
}

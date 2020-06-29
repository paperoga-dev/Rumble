package com.github.rumble.api.simple;

import java.util.Map;

public interface ApiInterface<T> {
    /*
     * Any class that implements this interface MUST have a public constructor with the
     * following signature:
     *
     * Class(Context context,
     *   OAuthService service,
     *   Token authToken,
     *   String appId,
     *   String appVersion);
    */

    Runnable call(Map<String, String> queryParams, CompletionInterface<T> onCompletion);
}

package com.github.rumble.blog.simple;

public interface ApiInterface<T> extends com.github.rumble.api.simple.ApiInterface<T> {
    /*
     * Any class that implements this interface MUST have a public constructor with the
     * following signature:
     *
     * Class(Context context,
     *   OAuthService service,
     *   Token authToken,
     *   String appId,
     *   String appVersion,
     *   String blogId);
     */

    String getBlogId();
}
package com.github.rumble.blog.array;

import com.github.rumble.api.array.ContentInterface;

public interface ApiInterface<T, W extends ContentInterface<T>> extends com.github.rumble.api.array.ApiInterface<T, W> {
    /*
     * Any class that implements this interface MUST have a public constructor with the
     * following signature:
     *
     * Class(Context context,
     *   OAuthService service,
     *   Token authToken,
     *   String appId,
     *   String appVersion,
     *   Integer offset,
     *   Integer limit,
     *   String blogId);
     */

    String getBlogId();
}

package com.github.rumble.api.array;

import java.util.List;
import java.util.Map;

public interface ApiInterface<T, W extends ContentInterface<T>> {
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
     *   Integer limit);
     */

    int getLimit();
    int getOffset();

    Runnable call(
            List<T> container,
            Map<String, String> queryParams,
            int offset,
            int limit,
            CompletionInterface<T, W> onCompletion
    );
}

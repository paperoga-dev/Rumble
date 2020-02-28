package com.github.rumble;

import android.content.Context;

import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

public abstract class TumblrBlogId<T> extends TumblrApi<T> {
    private final String blogId;

    TumblrBlogId(
            Context context,
            OAuthService service,
            Token authToken,
            String appId,
            String appVersion,
            String[] additionalArgs) {
        super(context, service, authToken, appId, appVersion);

        this.blogId = additionalArgs[0];
    }

    @Override
    protected String getPath() {
        /*
        blog-identifier  String  Any blog identifier
        */

        return "/blog/" + getBlogId() + ".tumblr.com";
    }

    public String getBlogId() {
        return blogId;
    }
}

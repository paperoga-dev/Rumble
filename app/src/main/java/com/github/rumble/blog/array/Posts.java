package com.github.rumble.blog.array;

import android.content.Context;

import com.github.rumble.posts.Post;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.Map;

public interface Posts {
    class Api extends Id<Post.Item, Post.Data> {

        public Api(
                Context context,
                OAuthService service,
                Token authToken,
                String appId,
                String appVersion,
                Integer offset,
                Integer limit,
                String blogId) {
            super(context, service, authToken, appId, appVersion, offset, limit, blogId);
        }

        @Override
        protected String getPath() {
            return super.getPath() + "/posts";
        }

        @Override
        protected Map<String, String> defaultParams() {
            Map<String, String> m = super.defaultParams();

            m.put("npf", "true");

            return m;
        }

        @Override
        protected Post.Data readData(JSONObject jsonObject) throws JSONException {
            return new Post.Data(jsonObject);
        }
    }
}

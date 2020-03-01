package com.github.rumble;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.List;

public interface Posts {
    class Post {

    }

    class Data implements TumblrArrayItem<Post> {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public List<Post> getItems() {
            return null;
        }
    }

    class Api extends TumblrArray<Data> {

        public Api(
                Context context,
                OAuthService service,
                Token authToken,
                String appId,
                String appVersion,
                String[] additionalArgs) {
            super(context, service, authToken, appId, appVersion, additionalArgs);
        }

        @Override
        protected String getPath() {
            return super.getPath() + "/posts";
        }

        @Override
        protected Data readData(JSONObject jsonObject) throws JSONException {
            return null;
        }
    }
}

package com.github.rumble.posts.attribution;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Base {
    private String url;

    public Base(JSONObject attributionObject) throws JSONException {
        super();

        this.url = attributionObject.getString("url");
    }

    public String getUrl() {
        return url;
    }
}

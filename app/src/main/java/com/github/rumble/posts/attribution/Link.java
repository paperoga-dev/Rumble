package com.github.rumble.posts.attribution;

import org.json.JSONException;
import org.json.JSONObject;

public class Link extends Base {
    public Link(JSONObject attributionObject) throws JSONException {
        super(attributionObject);
    }

    public static Base doCreate(JSONObject attributionObject) throws JSONException {
        return new Link(attributionObject);
    }
}

package com.github.rumble.posts.attribution;

import com.github.rumble.blog.simple.Info;

import org.json.JSONException;
import org.json.JSONObject;

public class Blog extends Base {
    private Info.Base blog;

    public Blog(JSONObject attributionObject) throws JSONException {
        super(attributionObject);

        this.blog = new Info.Base(attributionObject.getJSONObject("blog"));
    }

    public Info.Base getBlog() {
        return blog;
    }

    public static Base doCreate(JSONObject attributionObject) throws JSONException {
        return new Blog(attributionObject);
    }
}

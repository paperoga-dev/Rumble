package com.github.rumble.posts.attribution;

import com.github.rumble.BlogInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class Blog extends Base {
    private BlogInfo.Base blog;

    public Blog(JSONObject attributionObject) throws JSONException {
        super(attributionObject);

        this.blog = new BlogInfo.Base(attributionObject.getJSONObject("blog"));
    }

    public BlogInfo.Base getBlog() {
        return blog;
    }
}

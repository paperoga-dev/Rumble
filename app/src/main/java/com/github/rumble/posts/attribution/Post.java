package com.github.rumble.posts.attribution;

import com.github.rumble.BlogInfo;
import com.github.rumble.posts.Posts;

import org.json.JSONException;
import org.json.JSONObject;

public class Post extends Base {
    private Posts.Base post;
    private BlogInfo.Base blog;

    public Post(JSONObject attributionObject) throws JSONException {
        super(attributionObject);

        this.post = new Posts.Base(attributionObject.getJSONObject("post"));
        this.blog = new BlogInfo.Base(attributionObject.getJSONObject("blog"));
    }

    public Posts.Base getPost() {
        return post;
    }

    public BlogInfo.Base getBlog() {
        return blog;
    }

    public static Base doCreate(JSONObject attributionObject) throws JSONException {
        return new Post(attributionObject);
    }
}

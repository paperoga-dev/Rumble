package com.github.rumble.posts.attribution;

import com.github.rumble.blog.simple.Info;

import org.json.JSONException;
import org.json.JSONObject;

public class Post extends Base {
    private com.github.rumble.posts.Post.Base post;
    private Info.Base blog;

    public Post(JSONObject attributionObject) throws JSONException {
        super(attributionObject);

        this.post = new com.github.rumble.posts.Post.Base(attributionObject.getJSONObject("post"));
        this.blog = new Info.Base(attributionObject.getJSONObject("blog"));
    }

    public com.github.rumble.posts.Post.Base getPost() {
        return post;
    }

    public Info.Base getBlog() {
        return blog;
    }

    public static Base doCreate(JSONObject attributionObject) throws JSONException {
        return new Post(attributionObject);
    }
}

package com.github.rumble.posts.video;

import org.json.JSONException;
import org.json.JSONObject;

public class EmbedIframe {
    private String url;
    private int width;
    private int height;

    public EmbedIframe(JSONObject embedIframeObject) throws JSONException {
        super();

        this.url = embedIframeObject.getString("url");
        this.width = embedIframeObject.getInt("width");
        this.height = embedIframeObject.getInt("height");
    }

    public String getUrl() {
        return url;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

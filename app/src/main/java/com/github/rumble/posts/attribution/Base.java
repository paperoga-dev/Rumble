package com.github.rumble.posts.attribution;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class Base {
    private String url;

    private static final Map<String, Class<? extends Base>> typesMap =
            new HashMap<String, Class<? extends Base>>() {{
                put("link", com.github.rumble.posts.attribution.Link.class);
                put("blog", com.github.rumble.posts.attribution.Blog.class);
                put("post", com.github.rumble.posts.attribution.Post.class);
                put("app", com.github.rumble.posts.attribution.App.class);
            }};

    public Base(JSONObject attributionObject) throws JSONException {
        super();

        this.url = attributionObject.getString("url");
    }

    public String getUrl() {
        return url;
    }

    public static Base doCreate(JSONObject attributionObject) throws JSONException {
        if (attributionObject == null)
            return null;

        String attributionType = attributionObject.getString("type");
        try {
            return typesMap.get(attributionType)
                    .getDeclaredConstructor(JSONObject.class)
                    .newInstance(attributionObject);
        } catch (InstantiationException |
                InvocationTargetException |
                NoSuchMethodException |
                IllegalAccessException e) {
            throw new RuntimeException("Add missing attribution type: " + attributionType);
        }
    }
}
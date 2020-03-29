package com.github.rumble.posts.attribution;

import com.github.rumble.posts.ContentItem;
import com.github.rumble.posts.media.Media;

import org.json.JSONException;
import org.json.JSONObject;

public class App extends Base {
    private String name;
    private String displayText;
    private Media logo;

    public App(JSONObject attributionObject) throws JSONException {
        super(attributionObject);

        this.name = attributionObject.optString("app_name", "");
        this.displayText = attributionObject.optString("display_text", "");
        this.logo = ContentItem.allocateOrNothing(Media.class, attributionObject, "logo");
    }

    public String getName() {
        return name;
    }

    public String getDisplayText() {
        return displayText;
    }

    public Media getLogo() {
        return logo;
    }

    public static Base doCreate(JSONObject attributionObject) throws JSONException {
        return new App(attributionObject);
    }
}

package com.github.rumble.posts.attribution;

import com.github.rumble.posts.media.Image;

import org.json.JSONException;
import org.json.JSONObject;

public class App extends Base {
    private String name;
    private String displayText;
    private Image logo;

    public App(JSONObject attributionObject) throws JSONException {
        super(attributionObject);

        this.name = attributionObject.getString("app_name");
        this.displayText = attributionObject.getString("display_text");
        this.logo = new Image(attributionObject.getJSONObject("logo"));
    }

    public String getName() {
        return name;
    }

    public String getDisplayText() {
        return displayText;
    }

    public Image getLogo() {
        return logo;
    }
}

/*
 * Rumble
 * Copyright (C) 2020

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.rumble.posts.attribution;

import com.github.rumble.posts.ContentItem;
import com.github.rumble.posts.media.Media;

import org.json.JSONException;
import org.json.JSONObject;

public class App extends Base {
    private String name;
    private String displayText;
    private Media logo;

    public App(JSONObject attributionObject) throws JSONException, com.github.rumble.exception.RuntimeException {
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

    public static Base doCreate(JSONObject attributionObject) throws JSONException, com.github.rumble.exception.RuntimeException {
        return new App(attributionObject);
    }
}

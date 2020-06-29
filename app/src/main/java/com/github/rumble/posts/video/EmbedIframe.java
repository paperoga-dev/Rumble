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

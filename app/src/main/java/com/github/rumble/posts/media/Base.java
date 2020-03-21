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

package com.github.rumble.posts.media;

import com.github.rumble.posts.ContentItem;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Base extends ContentItem {
    private String url;
    private String mimeType;
    private boolean originalDimensionsMissing;
    private boolean cropped;
    private boolean hasOriginalDimensions;

    public Base(JSONObject mediaObject) throws JSONException {
        super();

        this.url = mediaObject.getString("url");
        this.mimeType = mediaObject.optString("type", "");
        this.originalDimensionsMissing = mediaObject.optBoolean(
                "original_dimensions_missing",
                false
        );
        this.cropped = mediaObject.optBoolean("cropped", false);
        this.hasOriginalDimensions = mediaObject.optBoolean(
                "has_original_dimensions",
                false
        );
    }

    public String getUrl() {
        return url;
    }

    public String getMimeType() {
        return mimeType;
    }

    public boolean areOriginalDimensionsMissing() {
        return originalDimensionsMissing;
    }

    public boolean isCropped() {
        return cropped;
    }

    public boolean hasOriginalDimensions() {
        return hasOriginalDimensions;
    }

    public static ContentItem doCreate(JSONObject mediaObject) throws JSONException {
        return new Image(mediaObject);
    }
}

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

public class Media {
    private String url;
    private String mimeType;
    private boolean originalDimensionsMissing;
    private boolean cropped;
    private boolean hasOriginalDimensions;
    private int width;
    private int height;
    private Media poster;

    public Media(JSONObject mediaObject) throws JSONException, com.github.rumble.exception.RuntimeException {
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
        this.width = mediaObject.optInt("width", 0);
        this.height = mediaObject.optInt("height", 0);
        this.poster = ContentItem.allocateOrNothing(Media.class, mediaObject, "poster");
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Media getPoster() {
        return poster;
    }
}

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

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.rumble.posts.ContentItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Base extends ContentItem {
    private List<Media> media;
    private List<Integer> colors;
    private String feedbackToken;
    private com.github.rumble.posts.attribution.Base attribution;
    private String altText;

    public Base(JSONObject mediaObject) throws JSONException, com.github.rumble.exception.RuntimeException {
        super();

        JSONArray media = mediaObject.getJSONArray("media");
        this.media = new ArrayList<>();
        for (int i = 0; i < media.length(); ++i) {
            this.media.add(new Media(media.getJSONObject(i)));
        }

        this.colors = new ArrayList<>();
        JSONObject colors = mediaObject.optJSONObject("colors");
        if (colors != null) {
            Iterator<String> it = colors.keys();
            while (it.hasNext()) {
                this.colors.add(Integer.valueOf(colors.getString(it.next()), 16));
            }
        }

        this.feedbackToken = mediaObject.optString("feedback_token", "");
        this.attribution = com.github.rumble.posts.attribution.Base.doCreate(mediaObject.optJSONObject("attribution"));
        this.altText = mediaObject.optString("alt_text", "");
    }

    public List<Media> getMedia() {
        return media;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public String getFeedbackToken() {
        return feedbackToken;
    }

    public com.github.rumble.posts.attribution.Base getAttribution() {
        return attribution;
    }

    public String getAltText() {
        return altText;
    }

    @Override
    public View render(Context context, int itemWidth) {
        ImageView im = new ImageView(context);

        if (getMedia().isEmpty())
            return im;

        int nearestIndex = 0;
        int nearestDiff = Math.abs(getMedia().get(0).getWidth() - itemWidth);
        for (int i = 1; i < getMedia().size(); ++i) {
            int currentDiff = Math.abs(getMedia().get(i).getWidth() - itemWidth);
            if (currentDiff < nearestDiff) {
                nearestDiff = currentDiff;
                nearestIndex = i;
            }
        }

        im.setMinimumWidth(itemWidth);
        im.setMinimumHeight((itemWidth * getMedia().get(nearestIndex).getHeight()) / getMedia().get(nearestIndex).getWidth());

        Glide.with(im.getContext())
                .load(getMedia().get(nearestIndex).getUrl())
                .placeholder(new ColorDrawable(Color.BLACK))
                .centerCrop()
                .into(im);

        return im;
    }

    public static ContentItem doCreate(JSONObject mediaObject) throws JSONException, com.github.rumble.exception.RuntimeException {
        return new Base(mediaObject);
    }
}

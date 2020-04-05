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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.github.rumble.posts.ContentItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Base extends ContentItem {
    private List<Media> media;
    private List<Integer> colors;
    private String feedbackToken;
    private com.github.rumble.posts.attribution.Base attribution;
    private String altText;

    private class FetchTask extends AsyncTask<Void, Void, Bitmap> {
        private ImageView imageView;
        private String imageUrl;

        public FetchTask(ImageView imageView, String imageUrl) {
            super();

            this.imageView = imageView;
            this.imageUrl = imageUrl;
        }

        @Override
        protected void onPreExecute() {
            //TODO : set imageView to a "pending" image
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                URL url = new URL(imageUrl);
                URLConnection connection = url.openConnection();
                HttpURLConnection HCon = (HttpURLConnection) connection;

                if (HCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream ins = HCon.getInputStream();
                    return BitmapFactory.decodeStream(ins);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            if (image == null) {
                //TODO : set imageView to an "error" image
            } else {
                imageView.setImageBitmap(image);
            }
        }
    }

    public Base(JSONObject mediaObject) throws JSONException {
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
    public View render(Context context) {
        ImageView iv = new ImageView(context);

        if (getMedia().isEmpty())
            return iv;

        int minArea = getMedia().get(0).getWidth() * getMedia().get(0).getHeight();
        int minIndex = 0;
        for (int i = 1; i < getMedia().size(); ++i) {
            int currentArea = getMedia().get(i).getWidth() * getMedia().get(i).getHeight();
            if (currentArea < minArea) {
                minArea = currentArea;
                minIndex = i;
            }
        }

        new FetchTask(iv, getMedia().get(minIndex).getUrl()).execute();

        return iv;
    }

    public static ContentItem doCreate(JSONObject mediaObject) throws JSONException {
        return new Base(mediaObject);
    }
}

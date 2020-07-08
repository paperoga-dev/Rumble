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

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.github.rumble.posts.ContentItem;
import com.github.rumble.posts.WebViewItem;
import com.github.rumble.posts.media.Media;

import org.json.JSONException;
import org.json.JSONObject;

public class Base extends ContentItem {
    private final String url;
    private final Media media;
    private final String provider;
    private final String embedHtml;
    private final EmbedIframe embedIframe;
    private final String embedUrl;
    private final Media poster;

    // TODO: metadata

    private com.github.rumble.posts.attribution.Base attribution;
    private boolean canAutoPlayOnCellular;

    public Base(JSONObject videoObject) throws JSONException {
        super();

        this.url = videoObject.optString("url", "");
        this.media = allocateOrNothing(Media.class, videoObject, "media");
        this.provider = videoObject.optString("provider", "");
        this.embedHtml = videoObject.optString("embed_html", "");
        this.embedIframe = allocateOrNothing(EmbedIframe.class, videoObject, "embed_iframe");
        this.embedUrl = videoObject.optString("embed_url", "");
        this.poster = allocateOrNothing(Media.class, videoObject, "poster");
        this.attribution = com.github.rumble.posts.attribution.Base.doCreate(videoObject.optJSONObject("attribution"));
    }

    public String getUrl() {
        return url;
    }

    public Media getMedia() {
        return media;
    }

    public String getProvider() {
        return provider;
    }

    public String getEmbedHtml() {
        return embedHtml;
    }

    public EmbedIframe getEmbedIframe() {
        return embedIframe;
    }

    public String getEmbedUrl() {
        return embedUrl;
    }

    public Media getPoster() {
        return poster;
    }

    public com.github.rumble.posts.attribution.Base getAttribution() {
        return attribution;
    }

    public boolean canAutoPlayOnCellular() {
        return canAutoPlayOnCellular;
    }

    public static ContentItem doCreate(JSONObject videoObject) throws JSONException {
        return new Base(videoObject);
    }

    @Override
    public View render(Context context, int itemWidth) {
        if (!getEmbedHtml().isEmpty()) {
            WebView wv = new WebViewItem(context);
            wv.loadDataWithBaseURL(
                    null,
                    "<html><head></head><body>" + getEmbedHtml() + "</body></html>",
                    "text/html; charset=utf-8",
                    "UTF-8",
                    null
            );

            wv.setMinimumWidth(itemWidth);
            wv.setMinimumHeight((itemWidth * getEmbedIframe().getHeight()) / getEmbedIframe().getWidth());

            return wv;
        } if ((getMedia() != null) && !getMedia().getUrl().isEmpty()) {
            Uri uri = Uri.parse(getMedia().getUrl());

            MediaController mc = new MediaController(context);
            VideoView vv  = new VideoView(context);

            vv.setMediaController(mc);
            vv.setVideoURI(uri);
            vv.requestFocus();

            mc.setAnchorView(vv);
            mc.setMediaPlayer(vv);

            vv.setMinimumWidth(itemWidth);
            vv.setMinimumHeight((itemWidth * getMedia().getHeight()) / getMedia().getWidth());
            vv.setBackgroundColor(Color.TRANSPARENT);

            /*
            vv.setZOrderOnTop(true);
            vv.setZOrderMediaOverlay(true);
            vv.setVisibility(View.VISIBLE);
            vv.start();
            */

            return vv;
        } else {
            WebView wv = new WebViewItem(context);
            wv.loadUrl(getUrl());
            return wv;
        }
    }
}

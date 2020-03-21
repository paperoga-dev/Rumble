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

package com.github.rumble.posts;

import android.content.Context;

import com.github.rumble.BlogInfo;
import com.github.rumble.TumblrArray;
import com.github.rumble.TumblrArrayItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.List;

public interface Posts {

    class Post {
        private long id;
        private BlogInfo.Base blog;
        private List<ContentItem> content;
        private List<LayoutItem> layout;

        public Post(JSONObject postObject) throws JSONException {
            super();

            this.id = postObject.getLong("id");
            this.blog = new BlogInfo.Data(postObject);

            this.content = new ArrayList<>();
            JSONArray content = postObject.getJSONArray("content");
            for (int i = 0; i < content.length(); ++i) {
                this.content.add(ContentItem.create(content.getJSONObject(i)));
            }

            this.layout = new ArrayList<>();
            JSONArray layout = postObject.optJSONArray("layout");
            if (layout == null)
                return;

            for (int i = 0; i < layout.length(); ++i) {
                this.layout.add(LayoutItem.create(layout.getJSONObject(i)));
            }
        }

        public long getId() {
            return id;
        }

        public BlogInfo.Base getBlog() {
            return blog;
        }

        public List<ContentItem> getContent() {
            return content;
        }

        public List<LayoutItem> getLayout() {
            return layout;
        }
    }

    class Data implements TumblrArrayItem<Post> {
        private int totalPosts;
        private List<Post> posts;

        Data(JSONObject postsObject) throws JSONException {
            super();

            this.totalPosts = postsObject.getInt("total_posts");
            this.posts = new ArrayList<>();

            JSONArray posts = postsObject.getJSONArray("posts");
            for (int i = 0; i < posts.length(); ++i) {
                this.posts.add(new Post(posts.getJSONObject(i)));
            }
        }

        @Override
        public int getCount() {
            return totalPosts;
        }

        @Override
        public List<Post> getItems() {
            return posts;
        }
    }


    class Api extends TumblrArray<Data> {

        public Api(
                Context context,
                OAuthService service,
                Token authToken,
                String appId,
                String appVersion,
                String[] additionalArgs) {
            super(context, service, authToken, appId, appVersion, additionalArgs);
        }

        @Override
        protected String getPath() {
            return super.getPath() + "/posts";
        }

        @Override
        protected Data readData(JSONObject jsonObject) throws JSONException {
            return new Data(jsonObject);
        }
    }
}

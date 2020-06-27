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

package com.github.rumble.blog.array;

import android.content.Context;

import com.github.rumble.api.array.ContentInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.List;

public interface Likes {
    class Post {

    }

    class Data implements ContentInterface<Post> {
        private List<Post> likedPosts;
        private int likedCount;

        Data(JSONObject likesObject) throws JSONException {
            super();

            // TODO: Post
            // this.likedPosts = ...;
            this.likedCount = likesObject.getInt("liked_count");
        }

        @Override
        public List<Post> getItems() {
            return likedPosts;
        }

        @Override
        public int getCount() {
            return likedCount;
        }
    }

    class Api extends Id<Post, Data> {

        public Api(
                Context context,
                OAuthService service,
                Token authToken,
                String appId,
                String appVersion,
                Integer offset,
                Integer limit,
                String blogId) {
            super(context, service, authToken, appId, appVersion, offset, limit, blogId);
        }

        @Override
        protected String getPath() {
            return super.getPath() + "/likes";
        }

        @Override
        protected Data readData(JSONObject jsonObject) throws JSONException {
            return new Data(jsonObject);
        }
    }
}

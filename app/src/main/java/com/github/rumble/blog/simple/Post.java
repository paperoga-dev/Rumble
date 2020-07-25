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

package com.github.rumble.blog.simple;

import android.content.Context;

import com.github.rumble.exception.RuntimeException;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.Map;

public interface Post {
    class Api extends Id<com.github.rumble.posts.Post.Item> {
        private final String postId;

        public Api(
                Context context,
                OAuthService service,
                Token authToken,
                String appId,
                String appVersion,
                String blogId,
                String postId) {
            super(context, service, authToken, appId, appVersion, blogId);

            this.postId = postId;
        }

        @Override
        protected String getPath() {
            return super.getPath() + "/posts/" + postId;
        }

        @Override
        protected Map<String, String> defaultParams() {
            Map<String, String> m = super.defaultParams();

            m.put("npf", "true");

            return m;
        }

        @Override
        protected com.github.rumble.posts.Post.Item readData(JSONObject jsonObject) throws JSONException, RuntimeException {
            return new com.github.rumble.posts.Post.Item(jsonObject);
        }
    }
}

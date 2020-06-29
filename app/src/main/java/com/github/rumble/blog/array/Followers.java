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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface Followers {
    class User {
        private String name;
        private boolean following;
        private String url;
        private Date updated;

        User(JSONObject userObject) throws JSONException {
            super();

            this.name = userObject.getString("name");
            this.following = userObject.getBoolean("following");
            this.url = userObject.getString("url");
            this.updated = new Date(userObject.getInt("updated") * 1000L);
        }

        public String getName() {
            return name;
        }

        public boolean isFollowing() {
            return following;
        }

        public String getUrl() {
            return url;
        }

        public Date getUpdated() {
            return updated;
        }
    }

    class Data implements ContentInterface<User> {
        private int totalUsers;
        private List<User> users;

        Data(JSONObject followersObject) throws JSONException {
            super();

            this.totalUsers = followersObject.getInt("total_users");
            this.users = new ArrayList<>();

            JSONArray users = followersObject.getJSONArray("users");
            for (int i = 0; i < users.length(); ++i) {
                this.users.add(new User(users.getJSONObject(i)));
            }
        }

        @Override
        public int getCount() {
            return totalUsers;
        }

        @Override
        public List<User> getItems() {
            return users;
        }
    }

    class Api extends Id<User, Data> {

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
            return super.getPath() + "/followers";
        }

        @Override
        protected boolean requiresApiKey() {
            return false;
        }

        @Override
        protected Data readData(JSONObject jsonObject) throws JSONException {
            return new Data(jsonObject);
        }
    }
}

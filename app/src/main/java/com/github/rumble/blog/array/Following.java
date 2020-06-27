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
import com.github.rumble.blog.simple.Info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.List;

public interface Following {
    class Data implements ContentInterface<Info.Base>
    {
        private List<Info.Base> blogs;
        private int totalBlogs;

        Data(JSONObject followingObject) throws JSONException {
            super();

            this.totalBlogs = followingObject.getInt("total_blogs");

            this.blogs = new ArrayList<>();
            JSONArray blogs = followingObject.getJSONArray("blogs");
            for (int i = 0; i < blogs.length(); ++i)
                this.blogs.add(new Info.Base(blogs.getJSONObject(i)));
        }

        @Override
        public int getCount() {
            return totalBlogs;
        }

        @Override
        public List<Info.Base> getItems() {
            return blogs;
        }
    }

    class Api extends Id<Info.Base, Data> {

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
            return super.getPath() + "/following";
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

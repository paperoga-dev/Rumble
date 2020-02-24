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

package com.github.rumble;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

public interface Following {
    class Api extends TumblrApi<String> {

        private String blogId;

        public Api(
                Context context,
                OAuthService service,
                Token authToken,
                String appId,
                String appVersion,
                String[] additionalArgs) {
            super(context, service, authToken, appId, appVersion);

            this.blogId = additionalArgs[0];
        }

        @Override
        protected String getPath() {
            /*
            limit   Number  The number of results to return: 1–20, inclusive   default: 20
            offset  Number  Followed blog index to start at                    default: 0 (most-recently followed)
            */

            return "/blog/" + blogId + ".tumblr.com/following";
        }

        @Override
        protected String readData(JSONObject jsonObject) throws JSONException {
            // TODO: needs a run
            return "";
        }
    }
}

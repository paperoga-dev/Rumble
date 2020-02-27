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

import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.HashMap;
import java.util.Map;

public abstract class TumblrArray<T> extends TumblrBlogId<T> {
    private int offset;
    private int limit;

    protected TumblrArray(
            Context context,
            OAuthService service,
            Token authToken,
            String appId,
            String appVersion,
            String[] additionalArgs) {
        super(context, service, authToken, appId, appVersion, additionalArgs);

        this.offset = Integer.valueOf(additionalArgs[1]);
        this.limit = Integer.valueOf(additionalArgs[2]);
    }

    @Override
    protected Map<String, String> defaultParams() {
        Map<String, String> m = super.defaultParams();

        /*
        limit            Number  The number of results to return: 1–20, inclusive  default: 20
        offset           Number  Result to start at                                default: 0
        */
        m.put("limit", String.valueOf(limit));
        m.put("offset", String.valueOf(offset));

        return m;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }
}

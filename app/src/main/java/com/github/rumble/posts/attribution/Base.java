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

package com.github.rumble.posts.attribution;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class Base {
    private String url;

    private static final Map<String, Class<? extends Base>> typesMap =
            new HashMap<String, Class<? extends Base>>() {{
                put("link", com.github.rumble.posts.attribution.Link.class);
                put("blog", com.github.rumble.posts.attribution.Blog.class);
                put("post", com.github.rumble.posts.attribution.Post.class);
                put("app", com.github.rumble.posts.attribution.App.class);
            }};

    public Base(JSONObject attributionObject) throws JSONException {
        super();

        this.url = attributionObject.getString("url");
    }

    public String getUrl() {
        return url;
    }

    public static Base doCreate(JSONObject attributionObject) throws JSONException, com.github.rumble.exception.RuntimeException {
        if (attributionObject == null)
            return null;

        String attributionType = attributionObject.getString("type");
        try {
            return (Base) typesMap.get(attributionType)
                    .getMethod("doCreate", JSONObject.class)
                    .invoke(null, attributionObject);
        } catch (InvocationTargetException |
                NoSuchMethodException |
                IllegalAccessException e) {
            throw new com.github.rumble.exception.RuntimeException("Add missing attribution type: " + attributionType);
        }
    }
}
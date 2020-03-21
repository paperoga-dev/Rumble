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

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class LayoutItem {

    private static final Map<String, Class<? extends LayoutItem>> typesMap =
            new HashMap<String, Class<? extends LayoutItem>>(){{
                put("rows", com.github.rumble.posts.layout.Rows.class);
                put("ask", com.github.rumble.posts.layout.Ask.class);
                put("condensed", com.github.rumble.posts.layout.Condensed.class);
            }};

    static LayoutItem create(JSONObject contentItem) throws JSONException {
        String type = contentItem.getString("type");
        try {
            return (LayoutItem) typesMap.get(type)
                    .getMethod("doCreate", JSONObject.class)
                    .invoke(null, contentItem);
        } catch (NullPointerException |
                InvocationTargetException |
                NoSuchMethodException |
                IllegalAccessException e) {
            throw new RuntimeException("Add missing layout: " + type);
        }
    }
}

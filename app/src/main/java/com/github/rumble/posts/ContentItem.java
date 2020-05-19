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

public abstract class ContentItem {

    private static final Map<String, Class<? extends ContentItem>> typesMap =
            new HashMap<String, Class<? extends ContentItem>>(){{
                put("text", com.github.rumble.posts.text.Base.class);
                put("image", com.github.rumble.posts.media.Base.class);
                put("link", com.github.rumble.posts.link.Base.class);
                put("audio", com.github.rumble.posts.audio.Base.class);
                put("video", com.github.rumble.posts.video.Base.class);
            }};

    static public <T> T allocateOrNothing(Class<T> clazz, JSONObject jsonObject, String key) {
        try {
            JSONObject object = jsonObject.optJSONObject(key);
            return (object != null) ? clazz.getDeclaredConstructor(JSONObject.class).newInstance(object) : null;
        } catch (InvocationTargetException |
                NoSuchMethodException |
                IllegalAccessException |
                InstantiationException e) {
            throw new RuntimeException(clazz.getName() + "has no construction with a JSONObject argument");
        }
    }

    public abstract String render(int itemWidth);

    static ContentItem create(JSONObject contentItem) throws JSONException {
        String type = contentItem.getString("type");
        try {
            return (ContentItem) typesMap.get(type)
                    .getMethod("doCreate", JSONObject.class)
                    .invoke(null, contentItem);
        } catch (InvocationTargetException |
                NoSuchMethodException |
                IllegalAccessException e) {
            throw new RuntimeException("Add missing type: " + type);
        }
    }
}

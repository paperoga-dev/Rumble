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

package com.github.rumble.posts.text;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.github.rumble.posts.ContentItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Base extends ContentItem {
    private String text;
    private List<com.github.rumble.posts.text.formatting.Base> formattingItems;
    private static int orderedListCounter = 0;

    private static final Map<String, Class<? extends com.github.rumble.posts.text.formatting.Base>> formattingTypesMap =
            new HashMap<String, Class<? extends com.github.rumble.posts.text.formatting.Base>>() {{
                put("bold", com.github.rumble.posts.text.formatting.Bold.class);
                put("italic", com.github.rumble.posts.text.formatting.Italic.class);
                put("strikethrough", com.github.rumble.posts.text.formatting.Strikethrough.class);
                put("link", com.github.rumble.posts.text.formatting.Link.class);
                put("mention", com.github.rumble.posts.text.formatting.Mention.class);
                put("color", com.github.rumble.posts.text.formatting.Color.class);
                put("small", com.github.rumble.posts.text.formatting.Small.class);
            }};

    private static final Map<String, Class<? extends com.github.rumble.posts.text.Base>> typesMap =
            new HashMap<String, Class<? extends com.github.rumble.posts.text.Base>>() {{
                put("plain", com.github.rumble.posts.text.Plain.class);
                put("heading1", com.github.rumble.posts.text.Heading1.class);
                put("heading2", com.github.rumble.posts.text.Heading2.class);
                put("quirky", com.github.rumble.posts.text.Quirky.class);
                put("quote", com.github.rumble.posts.text.Quote.class);
                put("indented", com.github.rumble.posts.text.Indented.class);
                put("chat", com.github.rumble.posts.text.Chat.class);
                put("ordered-list-item", com.github.rumble.posts.text.OrderedListItem.class);
                put("unordered-list-item", com.github.rumble.posts.text.UnorderedListItem.class);
            }};

    public Base(JSONObject textObject) throws JSONException, com.github.rumble.exception.RuntimeException {
        super();

        this.text = textObject.getString("text");

        this.formattingItems = new ArrayList<>();
        JSONArray formattingItems = textObject.optJSONArray("formatting");
        if (formattingItems == null)
            return;

        for (int i = 0; i < formattingItems.length(); ++i) {
            JSONObject formattingItem = formattingItems.getJSONObject(i);
            String type = formattingItem.getString("type");
            try {
                this.formattingItems.add(
                        formattingTypesMap.get(type)
                                .getDeclaredConstructor(JSONObject.class)
                                .newInstance(formattingItem)
                );
            } catch (InstantiationException |
                    InvocationTargetException |
                    NoSuchMethodException |
                    IllegalAccessException e) {
                throw new com.github.rumble.exception.RuntimeException("Add missing formatting type: " + type);
            }
        }
    }

    public static ContentItem doCreate(JSONObject textObject) throws com.github.rumble.exception.RuntimeException {
        String subType = textObject.optString("subtype", "plain");

        try {
            if (subType.equalsIgnoreCase("ordered-list-item"))
                ++orderedListCounter;
            else
                orderedListCounter = 0;

            return typesMap.get(subType)
                            .getDeclaredConstructor(JSONObject.class)
                            .newInstance(textObject);
        } catch (InstantiationException |
                InvocationTargetException |
                NoSuchMethodException |
                IllegalAccessException e) {
            throw new com.github.rumble.exception.RuntimeException("Add missing text subtype: " + subType);
        }
    }

    protected SpannableStringBuilder getFormattedText() {
        SpannableStringBuilder ssb = new SpannableStringBuilder(getText());

        for (com.github.rumble.posts.text.formatting.Base formattingItem : getFormattingItems()) {
            formattingItem.apply(ssb);
        }

        return ssb;
    }

    public List<com.github.rumble.posts.text.formatting.Base> getFormattingItems() {
        return formattingItems;
    }

    public String getText() {
        return text;
    }

    protected TextView createTextView(Context context) {
        TextView tv = new TextView(context);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setFocusable(true);

        return tv;
    }

    static protected int getOrderedListCounter() {
        return orderedListCounter;
    }
}

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

package com.github.rumble.posts.text.formatting;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;

import com.github.rumble.BlogInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class Mention extends Base {
    private BlogInfo.Reference blog;

    public Mention(JSONObject formattingObject) throws JSONException {
        super(formattingObject);

        this.blog = new BlogInfo.Reference(formattingObject.getJSONObject("blog"));
    }

    public BlogInfo.Reference getBlog() {
        return blog;
    }

    @Override
    public void apply(SpannableStringBuilder stringBuilder) {
        stringBuilder.setSpan(
                new UnderlineSpan(),
                getStart(),
                getEnd(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
    }
}

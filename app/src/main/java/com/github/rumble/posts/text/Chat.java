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
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Chat extends Base {
    public Chat(JSONObject textObject) throws JSONException, com.github.rumble.exception.RuntimeException {
        super(textObject);
    }

    @Override
    public View render(Context context, int itemWidth) {
        SpannableStringBuilder ssb = getFormattedText();
        ssb.setSpan(
                Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL),
                0,
                getText().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        TextView tv = createTextView(context);
        tv.setText(ssb);

        return tv;
    }
}

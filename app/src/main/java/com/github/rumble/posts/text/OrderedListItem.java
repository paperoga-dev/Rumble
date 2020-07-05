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
import android.text.SpannableString;
import android.text.style.LeadingMarginSpan;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderedListItem extends Base {
    private int orderNumber;

    public OrderedListItem(JSONObject textObject) throws JSONException {
        super(textObject);

        this.orderNumber = Base.getOrderedListCounter();
    }

    @Override
    public View render(Context context, int itemWidth) {
        TextView tv = createTextView(context);

        SpannableString ss = new SpannableString(orderNumber + ". " + getFormattedText());
        ss.setSpan(new LeadingMarginSpan.Standard(10, 10),0, getText().length(),0);
        tv.setText(ss);

        return tv;
    }
}

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

package com.github.rumble.posts.layout;

import com.github.rumble.posts.LayoutItem;
import com.github.rumble.posts.attribution.Base;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Ask extends LayoutItem {
    private final List<Integer> blocks;
    private Base attribution;

    public Ask(JSONObject layoutObject) throws JSONException {
        super();

        this.blocks = new ArrayList<>();

        JSONArray blocks = layoutObject.getJSONArray("blocks");
        for (int i = 0; i < blocks.length(); ++i) {
            this.blocks.add(blocks.getInt(i));
        }

        JSONObject attributionObject = layoutObject.optJSONObject("attribution");
        this.attribution = null;
        if (attributionObject == null)
            return;

        this.attribution = Base.doCreate(attributionObject);
    }

    public List<Integer> getBlocks() {
        return blocks;
    }

    public Base getAttribution() {
        return attribution;
    }
}

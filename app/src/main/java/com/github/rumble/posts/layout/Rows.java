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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Rows extends LayoutItem {
    private final List<List<Integer>> display;

    public Rows(JSONObject layoutObject) throws JSONException {
        super();

        this.display = new ArrayList<>();

        JSONArray display = layoutObject.getJSONArray("display");
        for (int i = 0; i < display.length(); ++i) {
            List<Integer> tBlocks = new ArrayList<>();

            JSONArray blocks = display.getJSONObject(i).getJSONArray("blocks");
            for (int j = 0; j < blocks.length(); ++j) {
                tBlocks.add(blocks.getInt(i));
            }

            this.display.add(tBlocks);
        }
    }

    public List<List<Integer>> getDisplay() {
        return display;
    }
}

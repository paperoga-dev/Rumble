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
    enum Type {
        Plain,
        Carousel
    }

    public class Blocks {
        private final List<Integer> indexes;
        private Type type;

        public Blocks(JSONObject blockObject) throws JSONException {
            super();

            this.indexes = new ArrayList<>();

            JSONArray blocks = blockObject.getJSONArray("blocks");
            for (int i = 0; i < blocks.length(); ++i) {
                this.indexes.add(blocks.getInt(i));
            }

            this.type = Type.Plain;
            JSONObject mode = blockObject.optJSONObject("mode");
            if ((mode != null) && mode.optString("type", "").equalsIgnoreCase("carousel"))
                this.type = Type.Carousel;
        }

        public List<Integer> getIndexes() {
            return indexes;
        }

        public Type getType() {
            return type;
        }
    }

    private final List<Blocks> blocksList;

    public Rows(JSONObject layoutObject) throws JSONException {
        super();

        this.blocksList = new ArrayList<>();

        JSONArray display = layoutObject.getJSONArray("display");
        for (int i = 0; i < display.length(); ++i) {
            this.blocksList.add(new Blocks(display.getJSONObject(i)));
        }
    }

    public List<Blocks> getBlocksList() {
        return blocksList;
    }

    public static LayoutItem doCreate(JSONObject layoutObject) throws JSONException {
        return new Rows(layoutObject);
    }
}

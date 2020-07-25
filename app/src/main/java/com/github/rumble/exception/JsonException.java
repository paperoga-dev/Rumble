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

package com.github.rumble.exception;

import org.json.JSONException;

public class JsonException extends BaseException {
    private final JSONException e;
    private final String jsonData;

    public JsonException(JSONException e, String jsonData) {
        super(e.getMessage());

        this.e = e;
        this.jsonData = jsonData;
    }

    public JSONException getException() {
        return e;
    }

    public String getJsonData() {
        return jsonData;
    }
}

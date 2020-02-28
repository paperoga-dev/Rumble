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

package com.github.rumble;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

class LogOutputStream extends OutputStream {
    private final String appId;
    private String mem;

    public LogOutputStream(String appId) {
        super();

        this.appId = appId;
        mem = "";
    }

    @Override
    public void write(int b) throws IOException {
        char c = (char) (b & 0xff);
        if (c == '\n') {
            flush();
        } else {
            mem += c;
        }
    }

    @Override
    public void flush () {
        Log.v(appId, mem);
        mem = "";
    }
}

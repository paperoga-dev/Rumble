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

package com.github.rumble.blog.array;

import com.github.rumble.api.array.ContentInterface;

public interface ApiInterface<T, W extends ContentInterface<T>> extends com.github.rumble.api.array.ApiInterface<T, W> {
    /*
     * Any class that implements this interface MUST have a public constructor with the
     * following signature:
     *
     * Class(Context context,
     *   OAuthService service,
     *   Token authToken,
     *   String appId,
     *   String appVersion,
     *   Integer offset,
     *   Integer limit,
     *   String blogId);
     */

    String getBlogId();
}

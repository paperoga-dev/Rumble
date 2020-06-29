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

package com.github.rumble.api.simple;

import android.os.Handler;
import android.os.Looper;

import com.github.rumble.api.Api;

import org.scribe.model.OAuthRequest;

public class TumblrCall<T> extends com.github.rumble.api.TumblrCall<T> {
    private final CompletionInterface<T> onCompletion;

    protected TumblrCall(Api<T> api, OAuthRequest request, CompletionInterface<T> onCompletion) {
        super(api, request, onCompletion);

        this.onCompletion = onCompletion;
    }

    @Override
    protected void process(final T output) {
        new Handler(Looper.getMainLooper()).post(
                new Runnable() {
                    @Override
                    public void run() {
                        onCompletion.onSuccess(output);
                    }
                });
    }
}

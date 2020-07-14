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

package com.github.rumble.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.github.rumble.Constants;
import com.github.rumble.exception.JsonException;
import com.github.rumble.exception.NetworkException;
import com.github.rumble.exception.ResponseException;
import com.github.rumble.exception.RuntimeException;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthRequest;

public abstract class TumblrCall<T> implements Runnable {
    private final AbstractCompletionInterface<T> onCompletion;
    private final Api<T> api;
    private final OAuthRequest request;

    protected TumblrCall(Api<T> api, OAuthRequest request, AbstractCompletionInterface<T> onCompletion) {
        super();

        this.api = api;
        this.request = request;
        this.onCompletion = onCompletion;
    }

    protected abstract void process(final T output);

    @Override
    public void run() {
        if (onCompletion == null)
            return;

        Log.v(Constants.APP_NAME, "Request: " + request.toString());
        Log.v(Constants.APP_NAME, "Query Params: " + request.getQueryStringParams().asFormUrlEncodedString());
        Log.v(Constants.APP_NAME, "Headers: " + request.getHeaders().toString());

        String jsonResponse = null;

        try {
            jsonResponse = request.send().getBody();
            Log.v(Constants.APP_NAME, "JSON Response: " + jsonResponse);

            JSONObject rootObj = new JSONObject(jsonResponse);

            JSONObject metaObj = rootObj.getJSONObject("meta");
            final int responseCode = metaObj.getInt("status");
            final String responseMessage = metaObj.getString("msg");

            switch (responseCode) {
                case 200:
                case 201:
                    process(api.readData(rootObj.getJSONObject("response")));
                    break;

                default:
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            onCompletion.onFailure(
                                    new ResponseException(
                                            responseCode,
                                            responseMessage
                                    )
                            );
                        }
                    });
                    break;
            }
        } catch (final JSONException e) {
            final String jsonData = jsonResponse;

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onCompletion.onFailure(new JsonException(e, jsonData));
                }
            });
        } catch (final OAuthException e) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onCompletion.onFailure(new NetworkException(e));
                }
            });
        } catch (final com.github.rumble.exception.RuntimeException e) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onCompletion.onFailure(e);
                }
            });
        }
    }
}

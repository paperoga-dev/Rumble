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

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.HashMap;
import java.util.Map;

abstract class TumblrApi<T> {

    public interface OnCompletion<T> {
        void onSuccess(T result);
        void onFailure(TumblrException e);
    }

    private static class TumblrCall<T> implements Runnable {

        private final OnCompletion<T> onCompletion;
        private final TumblrApi<T> api;
        private final OAuthRequest request;

        private TumblrCall(TumblrApi<T> api, OAuthRequest request, OnCompletion<T> onCompletion) {
            super();

            this.api = api;
            this.request = request;
            this.onCompletion = onCompletion;
        }

        @Override
        public void run() {
            if (onCompletion == null)
                return;

            try {
                String jsonResponse = request.send().getBody();
                Log.v(Constants.APP_NAME, "JSON Response: " + jsonResponse);

                JSONObject rootObj = new JSONObject(jsonResponse);

                JSONObject metaObj = rootObj.getJSONObject("meta");
                final int responseCode = metaObj.getInt("status");
                final String responseMessage = metaObj.getString("msg");

                switch (responseCode) {
                    case 200:
                    case 201:
                        final T out = api.readData(rootObj.getJSONObject("response"));
                        new Handler(Looper.getMainLooper()).post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        onCompletion.onSuccess(out);
                                    }
                                });
                        break;

                    default:
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                onCompletion.onFailure(
                                        new TumblrResponseException(
                                                responseCode,
                                                responseMessage
                                        )
                                );
                            }
                        });
                        break;
                }
            } catch (final JSONException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onCompletion.onFailure(new TumblrJsonException(e));
                    }
                });
            } catch (final OAuthException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onCompletion.onFailure(new TumblrNetworkException(e));
                    }
                });
            }
        }
    }

    private final Context context;
    private final OAuthService service;
    private final Token authToken;
    private final String appId;
    private final String appVersion;

    TumblrApi(
            Context context,
            OAuthService service,
            Token authToken,
            String appId,
            String appVersion) {
        super();

        this.context = context;
        this.service = service;
        this.authToken = authToken;
        this.appId = appId;
        this.appVersion = appVersion;
    }

    protected Context getContext() {
        return context;
    }

    boolean requiresApiKey() {
        return true;
    }

    protected abstract String getPath();
    Map<String, String> defaultParams() {
        Map<String, String> m = new HashMap<>();

        if (requiresApiKey())
            m.put("api_key", getContext().getString(R.string.consumer_key));

        return m;
    }
    protected abstract T readData(JSONObject jsonObject) throws JSONException;

    public void call(Map<String, ?> queryParams, OnCompletion<T> onCompletion) {
        OAuthRequest request = new OAuthRequest(Verb.GET, Constants.API_ENDPOINT + getPath());

        if (defaultParams() != null) {
            for (Map.Entry<String, ?> entry : defaultParams().entrySet()) {
                request.addQuerystringParameter(entry.getKey(), entry.getValue().toString());
            }
        }

        if (queryParams != null) {
            for (Map.Entry<String, ?> entry : queryParams.entrySet()) {
                request.addQuerystringParameter(entry.getKey(), entry.getValue().toString());
            }
        }

        request.addHeader("User-Agent", appId + "/" + appVersion);

        Log.v(Constants.APP_NAME, "Request: " + request.toString());
        Log.v(Constants.APP_NAME, "Query Params: " + request.getQueryStringParams().asFormUrlEncodedString());
        Log.v(Constants.APP_NAME, "Headers: " + request.getHeaders().toString());

        service.signRequest(authToken, request);

        new Thread(new TumblrCall<>(this, request, onCompletion)).start();
    }

    public void call(OnCompletion<T> onCompletion) {
        call(null, onCompletion);
    }
}

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

import android.content.Context;

import com.github.rumble.R;
import com.github.rumble.Constants;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.HashMap;
import java.util.Map;

public abstract class Api<T> {

    private final Context context;
    private final OAuthService service;
    private final Token authToken;
    private final String appId;
    private final String appVersion;

    protected Api(
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

    protected boolean requiresApiKey() {
        return true;
    }

    protected abstract String getPath();

    protected Map<String, String> defaultParams() {
        Map<String, String> m = new HashMap<>();

        if (requiresApiKey())
            m.put("api_key", getContext().getString(R.string.consumer_key));

        return m;
    }

    protected abstract T readData(JSONObject jsonObject) throws JSONException, com.github.rumble.exception.RuntimeException;

    protected OAuthRequest setupCall(Map<String, ?> queryParams) {
        OAuthRequest request = new OAuthRequest(Verb.GET, Constants.API_ENDPOINT + getPath());

        for (Map.Entry<String, ?> entry : defaultParams().entrySet()) {
            request.addQuerystringParameter(entry.getKey(), entry.getValue().toString());
        }

        for (Map.Entry<String, ?> entry : queryParams.entrySet()) {
            request.addQuerystringParameter(entry.getKey(), entry.getValue().toString());
        }

        request.addHeader("User-Agent", appId + "/" + appVersion);

        service.signRequest(authToken, request);

        return request;
    }

    protected OAuthService getService() {
        return service;
    }

    protected Token getAuthToken() {
        return authToken;
    }

    protected String getAppId() {
        return appId;
    }

    protected String getAppVersion() {
        return appVersion;
    }
}

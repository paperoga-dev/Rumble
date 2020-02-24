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
import android.os.AsyncTask;
import android.util.Log;

import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class TumblrAuthenticate {

    public interface OnAuthenticationListener {
        void onAuthenticationRequest(
                TumblrAuthenticate authenticator,
                Token requestToken,
                String authenticationUrl
        );
        void onAuthenticationGranted(Token accessToken);
        void onFailure(OAuthException exception);
    }

    private class RequestTokenTask extends AsyncTask<Void, Void, String> {
        private TumblrAuthenticate authenticator;
        private OAuthService oAuthService;
        private OnAuthenticationListener onAuthenticationListener;

        private Token requestToken;
        private OAuthException exc;

        public RequestTokenTask(
                TumblrAuthenticate authenticator,
                OAuthService oAuthService,
                OnAuthenticationListener onAuthenticationListener) {
            super();

            this.authenticator = authenticator;
            this.oAuthService = oAuthService;
            this.onAuthenticationListener = onAuthenticationListener;

            this.requestToken = null;
            this.exc = null;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                requestToken = oAuthService.getRequestToken();
                Log.v(Constants.APP_NAME, "Request Token: " + requestToken.toString());

                return oAuthService.getAuthorizationUrl(requestToken);
            } catch (OAuthException e) {
                exc = e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String authenticationUrl) {
            if (onAuthenticationListener == null)
                return;

            if (authenticationUrl == null) {
                if (exc != null) {
                    onAuthenticationListener.onFailure(exc);
                }
            } else {
                Log.v(Constants.APP_NAME, "Authentication URL: " + authenticationUrl);

                onAuthenticationListener.onAuthenticationRequest(
                        authenticator,
                        requestToken,
                        authenticationUrl
                );
            }
        }
    }

    private class AccessTokenTask extends AsyncTask<Void, Void, Token> {
        private String authVerifier;
        private Token requestToken;
        private OAuthService oAuthService;
        private Context context;
        private OnAuthenticationListener onAuthenticationListener;
        private OAuthException exc;

        public AccessTokenTask(
                String authVerifier,
                Token requestToken,
                OAuthService oAuthService,
                Context context,
                OnAuthenticationListener onAuthenticationListener) {
            super();

            this.authVerifier = authVerifier;
            this.requestToken = requestToken;
            this.oAuthService = oAuthService;
            this.context = context;
            this.onAuthenticationListener = onAuthenticationListener;
            this.exc = null;
        }

        @Override
        protected Token doInBackground(Void... voids) {
            try {
                return oAuthService.getAccessToken(requestToken, new Verifier(authVerifier));
            } catch (OAuthException e) {
                exc = e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Token authToken) {
            if (onAuthenticationListener == null)
                return;

            if (authToken == null) {
                if (exc != null) {
                    onAuthenticationListener.onFailure(exc);
                }
            } else {
                Log.v(Constants.APP_NAME, "Access Token: " + authToken.toString());

                context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putString(Constants.OAUTH_TOKEN_KEY, authToken.getToken())
                        .putString(Constants.OAUTH_TOKEN_SECRET_KEY, authToken.getSecret())
                        .apply();

                onAuthenticationListener.onAuthenticationGranted(authToken);
            }
        }
    }

    private OAuthService oAuthService;
    private Context context;
    private OnAuthenticationListener onAuthenticationListener;

    public TumblrAuthenticate(String appId, Context context) {
        super();

        this.oAuthService = new ServiceBuilder()
                .apiKey(context.getString(R.string.consumer_key))
                .apiSecret(context.getString(R.string.consumer_secret))
                .provider(OAuthTumblrApi.class)
                .callback(Constants.CALLBACK_URL)
                .debugStream(new LogOutputStream(appId))
                .build();
        this.context = context;
        this.onAuthenticationListener = null;
    }

    public void request() {
        if (onAuthenticationListener == null)
            return;

        new RequestTokenTask(this, oAuthService, onAuthenticationListener).execute();
    }

    public void verify(Token requestToken, String authVerifier) {
        Log.v(Constants.APP_NAME, "In verify: requestToken = " + requestToken.toString());
        Log.v(Constants.APP_NAME, "In verify: authVerifier = " + authVerifier);

        new AccessTokenTask(
                authVerifier,
                requestToken,
                oAuthService,
                context,
                onAuthenticationListener).execute();
    }

    void setOnAuthenticationListener(OnAuthenticationListener onAuthenticationListener) {
        this.onAuthenticationListener = onAuthenticationListener;
    }
}

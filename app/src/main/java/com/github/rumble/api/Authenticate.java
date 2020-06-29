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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.github.rumble.Constants;
import com.github.rumble.LogOutputStream;
import com.github.rumble.R;

import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class Authenticate {

    public interface OnAuthenticationListener {
        void onAuthenticationRequest(
                Authenticate authenticator,
                Token requestToken,
                String authenticationUrl
        );
        void onAuthenticationGranted(Token accessToken);
        void onFailure(OAuthException exception);
    }

    private static class RequestTokenTask implements Runnable {
        private final Authenticate authenticator;
        private final OAuthService oAuthService;
        private final OnAuthenticationListener onAuthenticationListener;

        RequestTokenTask(
                Authenticate authenticator,
                OAuthService oAuthService,
                OnAuthenticationListener onAuthenticationListener) {
            super();

            this.authenticator = authenticator;
            this.oAuthService = oAuthService;
            this.onAuthenticationListener = onAuthenticationListener;
        }

        @Override
        public void run() {
            if (onAuthenticationListener == null)
                return;

            try {
                final Token requestToken = oAuthService.getRequestToken();
                Log.v(Constants.APP_NAME, "Request Token: " + requestToken.toString());

                final String authenticationUrl = oAuthService.getAuthorizationUrl(requestToken);
                Log.v(Constants.APP_NAME, "Authentication URL: " + authenticationUrl);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onAuthenticationListener.onAuthenticationRequest(
                                authenticator,
                                requestToken,
                                authenticationUrl
                        );
                    }
                });

            } catch (final OAuthException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onAuthenticationListener.onFailure(e);
                    }
                });
            }
        }
    }

    private static class AccessTokenTask implements Runnable {
        private final String authVerifier;
        private final Token requestToken;
        private final OAuthService oAuthService;
        private final Context context;
        private final OnAuthenticationListener onAuthenticationListener;

        AccessTokenTask(
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
        }

        @Override
        public void run() {
            if (onAuthenticationListener == null)
                return;

            try {
                final Token authToken = oAuthService.getAccessToken(requestToken, new Verifier(authVerifier));
                Log.v(Constants.APP_NAME, "Access Token: " + authToken.toString());

                context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putString(Constants.OAUTH_TOKEN_KEY, authToken.getToken())
                        .putString(Constants.OAUTH_TOKEN_SECRET_KEY, authToken.getSecret())
                        .apply();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onAuthenticationListener.onAuthenticationGranted(authToken);
                    }
                });

            } catch (final OAuthException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onAuthenticationListener.onFailure(e);
                    }
                });
            }
        }
    }

    private final OAuthService oAuthService;
    private final Context context;
    private OnAuthenticationListener onAuthenticationListener;

    public Authenticate(String appId, Context context) {
        super();

        this.oAuthService = new ServiceBuilder()
                .apiKey(context.getString(R.string.consumer_key))
                .apiSecret(context.getString(R.string.consumer_secret))
                .provider(OAuthApi.class)
                .callback(Constants.CALLBACK_URL)
                .debugStream(new LogOutputStream(appId))
                .build();
        this.context = context;
        this.onAuthenticationListener = null;
    }

    public void request() {
        if (onAuthenticationListener == null)
            return;

        new Thread(new RequestTokenTask(this, oAuthService, onAuthenticationListener)).start();
    }

    public void verify(Token requestToken, String authVerifier) {
        Log.v(Constants.APP_NAME, "Verify: requestToken = " + requestToken.toString());
        Log.v(Constants.APP_NAME, "Verify: authVerifier = " + authVerifier);

        new Thread (
            new AccessTokenTask(
                    authVerifier,
                    requestToken,
                    oAuthService,
                    context,
                    onAuthenticationListener)
        ).start();
    }

    public void setOnAuthenticationListener(OnAuthenticationListener onAuthenticationListener) {
        this.onAuthenticationListener = onAuthenticationListener;
    }
}

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
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final class TumblrClient {

    public interface OnCompletion<T> {
        void onSuccess(T result);
    }

    public interface OnArrayCompletion<T> {
        void onSuccess(List<T> result, int offset, int limit, int count);
    }

    public interface OnFailureListener {
        void onFailure(TumblrException e);
        void onNetworkFailure(OAuthException e);
    }

    public interface OnLoginListener {
        void onAccessGranted();
        void onAccessRequest(
                TumblrAuthenticate authenticator,
                Token requestToken,
                String authenticationUrl);
        void onAccessDenied();
    }

    private final Context context;
    private String appName;
    private String appVersion;

    private Token authToken;
    private final OAuthService oAuthService;
    private OnLoginListener onLoginListener;
    private OnFailureListener onFailureListener;

    private final ExecutorService executorService;

    private UserInfo.Data me;

    public TumblrClient(Context context) {
        super();

        this.context = context;

        authToken = null;
        oAuthService = new ServiceBuilder()
                .provider(OAuthTumblrApi.class)
                .apiKey(context.getString(R.string.consumer_key))
                .apiSecret(context.getString(R.string.consumer_secret))
                .build();
        onLoginListener = null;
        onFailureListener = null;

        this.me = null;

        this.executorService = new ThreadPoolExecutor(
                1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>()
        );

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appName = pInfo.applicationInfo.packageName;
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen, anyway it isn't a big deal
        }
    }

    private void doLogin() {
        final TumblrAuthenticate auth = new TumblrAuthenticate(appName, context);

        auth.setOnAuthenticationListener(new TumblrAuthenticate.OnAuthenticationListener() {
            @Override
            public void onAuthenticationRequest(
                    TumblrAuthenticate authenticator,
                    Token requestToken,
                    String authenticationUrl) {
                onLoginListener.onAccessRequest(authenticator, requestToken, authenticationUrl);
            }

            @Override
            public void onAuthenticationGranted(Token accessToken) {
                // redo user request, this time should work
                login(accessToken);
            }

            @Override
            public void onFailure(OAuthException exception) {
                onLoginListener.onAccessDenied();
            }
        });
        auth.request();
    }

    private void login(Token authToken) {
        this.authToken = authToken;

        executorService.submit(
            new UserInfo.Api(context, oAuthService, authToken, appName, appVersion, null)
                    .call(new TumblrApi.OnCompletion<UserInfo.Data>() {
                        @Override
                        public void onSuccess(UserInfo.Data result) {
                            me = result;

                            if (onLoginListener != null)
                                onLoginListener.onAccessGranted();
                        }

                        @Override
                        public void onFailure(TumblrException e) {
                            if (e instanceof TumblrNetworkException) {
                                // we cannot reach Tumblr, fail but do not remove our tokens
                                onFailureListener.onNetworkFailure(((TumblrNetworkException) e).getException());
                            } else {
                                // we can reach Tumblr, but we cannot access it. So, throw away our tokens, and
                                // let's ask a new authentication
                                Log.v(Constants.APP_NAME, "Auth token not valid");

                                context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE)
                                        .edit()
                                        .remove(Constants.OAUTH_TOKEN_KEY)
                                        .remove(Constants.OAUTH_TOKEN_SECRET_KEY)
                                        .apply();

                                doLogin();
                            }
                        }
                    })
        );
    }

    public void login() {

        SharedPreferences prefs = context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);

        if (prefs.contains(Constants.OAUTH_TOKEN_KEY) && prefs.contains(Constants.OAUTH_TOKEN_SECRET_KEY)) {

            // ok, we already have authentication tokens, let's try them first
            authToken = new Token(
                    prefs.getString(Constants.OAUTH_TOKEN_KEY, ""),
                    prefs.getString(Constants.OAUTH_TOKEN_SECRET_KEY, "")
            );
            Log.v(Constants.APP_NAME, "Stored Access Token: " + authToken);

            login(authToken);
        } else {
            // never logged in before, do that
            doLogin();
        }
    }

    private <T> void doCall(
            Class<? extends TumblrApi<T>> clazz,
            Map<String, ?> queryParams,
            final OnCompletion<T> onCompletion,
            String ... additionalArgs) {

        try {
            Class[] cArg = new Class[] {
                    Context.class,
                    OAuthService.class,
                    Token.class,
                    String.class,
                    String.class,
                    String[].class
            };

            executorService.submit(
                clazz.getDeclaredConstructor(cArg)
                        .newInstance(
                                context,
                                oAuthService,
                                authToken,
                                appName,
                                appVersion,
                                additionalArgs)
                        .call(queryParams, new TumblrApi.OnCompletion<T>() {
                            @Override
                            public void onSuccess(T result) {
                                if (onCompletion != null)
                                    onCompletion.onSuccess(result);
                            }

                            @Override
                            public void onFailure(TumblrException e) {
                                if (onFailureListener != null)
                                    onFailureListener.onFailure(e);
                            }
                        })
            );
        } catch (IllegalAccessException |
                NoSuchMethodException |
                InvocationTargetException |
                InstantiationException e) {
            e.printStackTrace();
        }
    }

    public <T> void call(
            Class<? extends TumblrApi<T>> clazz,
            Map<String, ?> queryParams,
            final OnCompletion<T> onCompletion,
            String ... additionalArgs) {
        doCall(clazz, queryParams, onCompletion, additionalArgs);
    }

    public <T> void call(Class<? extends TumblrApi<T>> clazz,
                         OnCompletion<T> onCompletion,
                         String ... additionalArgs) {
        doCall(clazz, null, onCompletion, additionalArgs);
    }

    private <T, W extends TumblrArrayItem<T>> void call(
            final List<T> resultList,
            final Class<? extends TumblrApi<W>> clazz,
            final String blogId,
            final int sourceOffset,
            final int currentOffset,
            final int sourceLimit,
            final int currentLimit,
            final Map<String, ?> queryParams,
            final OnArrayCompletion<T> onCompletion,
            final String ... additionalArgs) {
        String[] aArgs = new String[additionalArgs.length + 3];
        aArgs[0] = blogId;
        aArgs[1] = String.valueOf(currentOffset);
        aArgs[2] = String.valueOf((currentLimit == -1)? 20 : Math.min(currentLimit, 20));

        System.arraycopy(additionalArgs, 0, aArgs, 3, additionalArgs.length);

        doCall(
                clazz,
                queryParams,
                new TumblrClient.OnCompletion<W>() {
                    @Override
                    public void onSuccess(W result) {
                        resultList.addAll(result.getItems());

                        int newOffset = currentOffset + result.getItems().size();

                        int newLimit = Math.min(
                                20,
                                ((sourceLimit == -1)? result.getCount() : sourceLimit) - resultList.size()
                        );

                        if ((newLimit <= 0) || result.getItems().isEmpty()) {
                            if (onCompletion != null)
                                onCompletion.onSuccess(resultList, sourceOffset, sourceLimit, result.getCount());
                        } else {
                            call(
                                    resultList,
                                    clazz,
                                    blogId,
                                    sourceOffset,
                                    newOffset,
                                    sourceLimit,
                                    newLimit,
                                    queryParams,
                                    onCompletion,
                                    additionalArgs
                            );
                        }
                    }
                },
                aArgs
        );
    }

    public <T, W extends TumblrArrayItem<T>> void call(
            Class<? extends TumblrApi<W>> clazz,
            String blogId,
            final int offset,
            final int limit,
            final Map<String, ?> queryParams,
            final OnArrayCompletion<T> onCompletion,
            String ... additionalArgs) {
        call(
                new ArrayList<T>(),
                clazz,
                blogId,
                offset,
                offset,
                limit,
                limit,
                queryParams,
                onCompletion,
                additionalArgs
        );
    }

    public <T, W extends TumblrArrayItem<T>> void call(
            Class<? extends TumblrApi<W>> clazz,
            String blogId,
            int offset,
            int limit,
            OnArrayCompletion<T> onCompletion,
            String ... additionalArgs) {
        call(clazz, blogId, offset, limit, null, onCompletion, additionalArgs);
    }

    public <T, W extends TumblrArrayItem<T>> void call(
            Class<? extends TumblrApi<W>> clazz,
            String blogId,
            int offset,
            Map<String, ?> queryParams,
            final OnArrayCompletion<T> onCompletion,
            String ... additionalArgs) {
        call(clazz, blogId, offset, 20, queryParams, onCompletion, additionalArgs);
    }

    public <T, W extends TumblrArrayItem<T>> void call(
            Class<? extends TumblrApi<W>> clazz,
            String blogId,
            int offset,
            OnArrayCompletion<T> onCompletion,
            String ... additionalArgs) {
        call(clazz, blogId, offset, 20, null, onCompletion, additionalArgs);
    }

    public void setOnLoginListener(OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
    }

    public void setOnFailureListener(OnFailureListener onFailureListener) {
        this.onFailureListener = onFailureListener;
    }

    public UserInfo.Data getMe() {
        return me;
    }
}

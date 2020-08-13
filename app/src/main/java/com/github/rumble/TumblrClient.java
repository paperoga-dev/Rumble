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

import com.github.rumble.api.OAuthApi;
import com.github.rumble.api.array.ContentInterface;
import com.github.rumble.api.Authenticate;
import com.github.rumble.api.simple.CompletionInterface;
import com.github.rumble.exception.BaseException;
import com.github.rumble.exception.NetworkException;
import com.github.rumble.user.simple.Info;

import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class TumblrClient {

    public interface OnLoginListener {
        void onAccessGranted();
        void onAccessRequest(
                Authenticate authenticator,
                Token requestToken,
                String authenticationUrl);
        void onAccessDenied();
        void onLoginFailure(BaseException e);
    }

    private final Context context;
    private String appName;
    private String appVersion;

    private Token authToken;
    private final OAuthService oAuthService;
    private OnLoginListener onLoginListener;

    private final ExecutorService executorService;

    private Info.Data me;

    public TumblrClient(Context context) {
        super();

        this.context = context;

        authToken = null;
        oAuthService = new ServiceBuilder()
                .provider(OAuthApi.class)
                .apiKey(context.getString(R.string.consumer_key))
                .apiSecret(context.getString(R.string.consumer_secret))
                .build();
        onLoginListener = null;

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
        final Authenticate auth = new Authenticate(appName, context);

        auth.setOnAuthenticationListener(new Authenticate.OnAuthenticationListener() {
            @Override
            public void onAuthenticationRequest(
                    Authenticate authenticator,
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
            new Info.Api(
                    context,
                    oAuthService,
                    authToken,
                    appName,
                    appVersion)
                    .call(new HashMap<String, String>(), new CompletionInterface<Info.Data>() {
                        @Override
                        public void onSuccess(Info.Data result) {
                            me = result;

                            if (onLoginListener != null)
                                onLoginListener.onAccessGranted();
                        }

                        @Override
                        public void onFailure(BaseException e) {
                            if (!(e instanceof NetworkException)) {
                                // we can reach Tumblr, but we cannot access it. So, throw away our tokens, and
                                // let's ask a new authentication
                                Log.v(Constants.APP_NAME, "Auth token not valid");

                                logout();
                            }

                            // else we cannot reach Tumblr, fail but do not remove our tokens

                            if (onLoginListener != null)
                                onLoginListener.onLoginFailure(e);
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

    public void logout() {
        context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE).edit()
                .remove(Constants.OAUTH_TOKEN_KEY)
                .remove(Constants.OAUTH_TOKEN_SECRET_KEY)
                .apply();
        authToken = null;
    }

    /* **** SINGLE ITEM API CALL **** */
    private <T> void doCall(
            final com.github.rumble.api.simple.ApiInterface<T> obj,
            final Map<String, String> queryParams,
            final com.github.rumble.api.simple.CompletionInterface<T> onCompletion) {
        if (authToken == null) {
            if (onLoginListener != null) {
                onLoginListener.onLoginFailure(new com.github.rumble.exception.RuntimeException("Not logged"));
            }

            return;
        }

        executorService.submit(obj.call(queryParams, onCompletion));
    }
    /* **** SINGLE ITEM API CALL **** */

    /* **** ARRAY BASED API CALL **** */
    private <T, W extends ContentInterface<T>> void doCall(
            final List<T> resultList,
            final com.github.rumble.api.array.ApiInterface<T, W> obj,
            final Integer offset,
            final Integer limit,
            final Map<String, String> queryParams,
            final com.github.rumble.api.array.CompletionInterface<T, W> onCompletion) {

        if (authToken == null) {
            if (onLoginListener != null) {
                onLoginListener.onLoginFailure(new com.github.rumble.exception.RuntimeException("Not logged"));
            }

            return;
        }

        int newLimit = (limit == -1)? 20 : Math.min(20, limit);

        executorService.submit(
                obj.call(
                        resultList,
                        queryParams,
                        offset,
                        newLimit,
                        new com.github.rumble.api.array.CompletionInterface<T, W>() {

                            @Override
                            public void onSuccess(List<T> result, int offset, int limit, int count) {
                                resultList.addAll(result);
                                if (result.isEmpty()) {
                                    // This is a Tumblr bug, some array-based APIs return less items
                                    // than expected. In this case, we return earlier, with the real
                                    // number of items.

                                    if (onCompletion != null)
                                        onCompletion.onSuccess(resultList, obj.getOffset(), obj.getLimit(), resultList.size());
                                    return;
                                }

                                int newOffset = offset + resultList.size();
                                int newLimit;

                                if (count == -1) {
                                    // cannot get the end of the list

                                    if (obj.getLimit() == -1) {
                                        // the caller did not specify a limit, so the first content
                                        // is fine, we're done
                                        if (onCompletion != null)
                                            onCompletion.onSuccess(resultList, obj.getOffset(), obj.getLimit(), resultList.size());
                                        return;
                                    } else {
                                        if (resultList.size() >= obj.getLimit()) {
                                            // fetched enough content, we're done
                                            if (onCompletion != null)
                                                onCompletion.onSuccess(resultList, obj.getOffset(), obj.getLimit(), resultList.size());
                                            return;
                                        }

                                        newLimit = obj.getLimit() - resultList.size();
                                    }

                                } else {

                                    newLimit = ((obj.getLimit() == -1)? count : obj.getLimit()) - resultList.size();

                                    if (newLimit <= 0) {
                                        // fetched enough content, we're done
                                        if (onCompletion != null)
                                            onCompletion.onSuccess(resultList, obj.getOffset(), obj.getLimit(), resultList.size());
                                        return;
                                    }
                                }

                                doCall(resultList,
                                        obj,
                                        newOffset,
                                        newLimit,
                                        queryParams,
                                        onCompletion);
                            }

                            @Override
                            public void onFailure(BaseException e) {
                                onCompletion.onFailure(e);
                            }
                        }
                )
        );
    }
    /* **** ARRAY BASED API CALL **** */

    /* **** USER BASED API CALLS **** */
    public <T> void call(
            final Class<? extends com.github.rumble.api.simple.ApiInterface<T>> clazz,
            final Map<String, String> queryParams,
            final com.github.rumble.api.simple.CompletionInterface<T> onCompletion) {

        Class<?>[] cArg = new Class<?>[] {
                Context.class,
                OAuthService.class,
                Token.class,
                String.class,
                String.class
        };

        try {
            doCall(
                    clazz.getDeclaredConstructor(cArg)
                            .newInstance(
                                    context,
                                    oAuthService,
                                    authToken,
                                    appName,
                                    appVersion
                            ),
                    queryParams,
                    onCompletion
            );
        } catch (IllegalAccessException |
                InstantiationException |
                InvocationTargetException |
                NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public <T> void call(
            final Class<? extends com.github.rumble.api.simple.ApiInterface<T>> clazz,
            final CompletionInterface<T> onCompletion) {
        call(clazz, new HashMap<String, String>(), onCompletion);
    }

    public <T, W extends ContentInterface<T>> void call(
            final Class<? extends com.github.rumble.api.array.ApiInterface<T, W>> clazz,
            final int offset,
            final int limit,
            final Map<String, String> queryParams,
            final com.github.rumble.api.array.CompletionInterface<T, W> onCompletion) {

        Class<?>[] cArg = new Class<?>[] {
                Context.class,
                OAuthService.class,
                Token.class,
                String.class,
                String.class,
                Integer.class,
                Integer.class
        };

        try {
            doCall(
                    new ArrayList<T>(),
                    clazz.getDeclaredConstructor(cArg)
                            .newInstance(
                                    context,
                                    oAuthService,
                                    authToken,
                                    appName,
                                    appVersion,
                                    offset,
                                    limit
                            ),
                    offset,
                    limit,
                    queryParams,
                    onCompletion
            );
        } catch (IllegalAccessException |
                InstantiationException |
                InvocationTargetException |
                NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public <T, W extends ContentInterface<T>> void call(
            final Class<? extends com.github.rumble.api.array.ApiInterface<T, W>> clazz,
            final int offset,
            final Map<String, String> queryParams,
            final com.github.rumble.api.array.CompletionInterface<T, W> onCompletion) {
        call(clazz, offset, 20, queryParams, onCompletion);
    }

    public <T, W extends ContentInterface<T>> void call(
            final Class<? extends com.github.rumble.api.array.ApiInterface<T, W>> clazz,
            final int offset,
            final int limit,
            final com.github.rumble.api.array.CompletionInterface<T, W> onCompletion) {
        call(clazz, offset, limit, new HashMap<String, String>(), onCompletion);
    }

    public <T, W extends ContentInterface<T>> void call(
            final Class<? extends com.github.rumble.api.array.ApiInterface<T, W>> clazz,
            final int offset,
            final com.github.rumble.api.array.CompletionInterface<T, W> onCompletion) {
        call(clazz, offset, 20, new HashMap<String, String>(), onCompletion);
    }
    /* **** USER BASED API CALLS **** */

    /* **** BLOG BASED API CALLS **** */
    public <T> void call(
            final Class<? extends com.github.rumble.blog.simple.ApiInterface<T>> clazz,
            final String blogId,
            final Map<String, String> queryParams,
            final CompletionInterface<T> onCompletion) {

        Class<?>[] cArg = new Class<?>[] {
                Context.class,
                OAuthService.class,
                Token.class,
                String.class,
                String.class,
                String.class
        };

        try {
            doCall(
                    clazz.getDeclaredConstructor(cArg)
                            .newInstance(
                                    context,
                                    oAuthService,
                                    authToken,
                                    appName,
                                    appVersion,
                                    blogId
                            ),
                    queryParams,
                    onCompletion
            );
        } catch (IllegalAccessException |
                InstantiationException |
                InvocationTargetException |
                NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public <T> void call(
            final Class<? extends com.github.rumble.blog.simple.ApiInterface<T>> clazz,
            final String blogId,
            final CompletionInterface<T> onCompletion) {
        call(clazz, blogId, new HashMap<String, String>(), onCompletion);
    }

    public <T, W extends ContentInterface<T>> void call(
            final Class<? extends com.github.rumble.blog.array.ApiInterface<T, W>> clazz,
            final String blogId,
            final int offset,
            final int limit,
            final Map<String, String> queryParams,
            final com.github.rumble.api.array.CompletionInterface<T, W> onCompletion) {

        Class<?>[] cArg = new Class<?>[] {
                Context.class,
                OAuthService.class,
                Token.class,
                String.class,
                String.class,
                Integer.class,
                Integer.class,
                String.class
        };

        try {
            doCall(
                    new ArrayList<T>(),
                    clazz.getDeclaredConstructor(cArg)
                            .newInstance(
                                    context,
                                    oAuthService,
                                    authToken,
                                    appName,
                                    appVersion,
                                    offset,
                                    limit,
                                    blogId
                            ),
                    offset,
                    limit,
                    queryParams,
                    onCompletion
            );
        } catch (IllegalAccessException |
                InstantiationException |
                InvocationTargetException |
                NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public <T, W extends ContentInterface<T>> void call(
            final Class<? extends com.github.rumble.blog.array.ApiInterface<T, W>> clazz,
            final String blogId,
            final int offset,
            final Map<String, String> queryParams,
            final com.github.rumble.api.array.CompletionInterface<T, W> onCompletion) {
        call(clazz, blogId, offset, 20, queryParams, onCompletion);
    }

    public <T, W extends ContentInterface<T>> void call(
            final Class<? extends com.github.rumble.blog.array.ApiInterface<T, W>> clazz,
            final String blogId,
            final int offset,
            final int limit,
            final com.github.rumble.api.array.CompletionInterface<T, W> onCompletion) {
        call(clazz, blogId, offset, limit, new HashMap<String, String>(), onCompletion);
    }

    public <T, W extends ContentInterface<T>> void call(
            final Class<? extends com.github.rumble.blog.array.ApiInterface<T, W>> clazz,
            final String blogId,
            final int offset,
            final com.github.rumble.api.array.CompletionInterface<T, W> onCompletion) {
        call(clazz, blogId, offset, 20, new HashMap<String, String>(), onCompletion);
    }
    /* **** BLOG BASED API CALLS **** */

    /* **** POST BASED API CALLS **** */
    public <T> void call(
            final Class<? extends com.github.rumble.blog.simple.ApiInterface<T>> clazz,
            final String blogId,
            final String postId,
            final Map<String, String> queryParams,
            final CompletionInterface<T> onCompletion) {

        Class<?>[] cArg = new Class<?>[] {
                Context.class,
                OAuthService.class,
                Token.class,
                String.class,
                String.class,
                String.class,
                String.class
        };

        try {
            doCall(
                    clazz.getDeclaredConstructor(cArg)
                            .newInstance(
                                    context,
                                    oAuthService,
                                    authToken,
                                    appName,
                                    appVersion,
                                    blogId,
                                    postId
                            ),
                    queryParams,
                    onCompletion
            );
        } catch (IllegalAccessException |
                InstantiationException |
                InvocationTargetException |
                NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public <T> void call(
            final Class<? extends com.github.rumble.blog.simple.ApiInterface<T>> clazz,
            final String blogId,
            final String postId,
            final CompletionInterface<T> onCompletion) {
        call(clazz, blogId, postId, new HashMap<String, String>(), onCompletion);
    }
    /* **** POST BASED API CALLS **** */

    public void setOnLoginListener(OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
    }

    public Info.Data getMe() {
        return me;
    }

    public boolean isLogged() {
        return authToken != null;
    }
}

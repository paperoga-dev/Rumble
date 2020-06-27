package com.github.rumble.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.github.rumble.Constants;
import com.github.rumble.exception.JsonException;
import com.github.rumble.exception.NetworkException;
import com.github.rumble.exception.ResponseException;

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
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onCompletion.onFailure(new JsonException(e));
                }
            });
        } catch (final OAuthException e) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onCompletion.onFailure(new NetworkException(e));
                }
            });
        } catch (final java.lang.RuntimeException e) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onCompletion.onFailure(new com.github.rumble.exception.RuntimeException(e));
                }
            });
        }
    }
}

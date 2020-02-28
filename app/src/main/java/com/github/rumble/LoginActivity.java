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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.scribe.model.Token;

public class LoginActivity extends Activity {

    class CustomWebViewClient extends WebViewClient {
        private final Token token;

        CustomWebViewClient(Token token) {
            super();

            this.token = token;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.toLowerCase().contains(Constants.CALLBACK_URL.toLowerCase())) {
                Uri uri = Uri.parse(url);
                for (String strQuery : uri.getQueryParameterNames())
                    if (strQuery.contentEquals(Constants.OAUTH_VERIFIER)) {

                        Log.v(Constants.APP_NAME, "Auth Verifier: " + uri.getQueryParameter(strQuery));

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(Constants.REQUEST_TOKEN, token);
                        returnIntent.putExtra(Constants.OAUTH_VERIFIER, uri.getQueryParameter(strQuery));
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();

                        return true;
                    }
            }

            return false;
        }
    }

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        // We need this WebView only once, in order to perform a Tumblr login
        // so don't keep track about this visit
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setSaveFormData(false);
        webView.getSettings().setGeolocationEnabled(false);
        webView.getSettings().setDatabaseEnabled(false);
        webView.getSettings().setDomStorageEnabled(false);

        webView.setWebViewClient(new CustomWebViewClient(
                (Token) getIntent().getSerializableExtra(Constants.REQUEST_TOKEN))
        );

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(getIntent().getStringExtra(Constants.AUTH_URL));
            }
        });
    }
}

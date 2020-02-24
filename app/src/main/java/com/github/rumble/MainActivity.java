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

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;

public class MainActivity extends AppCompatActivity {

    private TumblrClient client;
    private TumblrAuthenticate authenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new TumblrClient(getApplicationContext());

        final TextView tv = findViewById(R.id.textView);
        tv.setText("");

        client.setOnLoginListener(new TumblrClient.OnLoginListener() {
            @Override
            public void onAccessGranted() {
                tv.append("Logged in!\n");

                tv.append("Me: " + client.getMe().getName() + "\n");
                tv.append("My blogs:\n");
                for (UserInfo.Blog blog : client.getMe().getBlogs()) {
                    tv.append("\t" + blog.getName() + "\n");
                    tv.append("\t\t" + blog.getTitle() + "\n");

                    client.call(BlogInfo.Api.class, new TumblrClient.OnCompletion<BlogInfo.Data>() {
                            @Override
                            public void onSuccess(BlogInfo.Data result) {
                                tv.append("\t\t\t" + result.getName() + "\n");
                                tv.append("\t\t\t" + result.getTitle() + "\n");
                                tv.append("\t\t\t" + result.getDescription() + "\n");
                                tv.append("\t\t\t" + result.getPosts() + "\n");
                                tv.append("\t\t\t" + result.getUpdated().toString() + "\n");
                                tv.append("\t\t\t" + result.isAsk() + "\n");
                                tv.append("\t\t\t" + result.isAskAnon() + "\n");
                            }
                        },
                        blog.getName()
                    );
                }
            }

            @Override
            public void onAccessRequest(
                    TumblrAuthenticate authenticator,
                    Token requestToken,
                    String authenticationUrl) {

                setAuthenticator(authenticator);

                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.putExtra(Constants.REQUEST_TOKEN, requestToken);
                i.putExtra(Constants.AUTH_URL, authenticationUrl);
                startActivityForResult(i, Constants.PERFORM_LOGIN);
            }

            @Override
            public void onAccessDenied() {
                tv.append("Login failed\n");
            }
        });

        client.setOnFailureListener(new TumblrClient.OnFailureListener() {
            @Override
            public void onFailure(TumblrException e) {
                tv.append("Command failure\n");
            }

            @Override
            public void onNetworkFailure(OAuthException e) {
                tv.append("Network error\n");
            }
        });

        findViewById(R.id.btnClearTokens).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE).edit()
                        .remove(Constants.OAUTH_TOKEN_KEY)
                        .remove(Constants.OAUTH_TOKEN_SECRET_KEY)
                        .apply();
            }
        });

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                client.login();
            }
        });
    }

    private void setAuthenticator(TumblrAuthenticate authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(Constants.APP_NAME, "ActivityResult");

        switch (requestCode) {
            case Constants.PERFORM_LOGIN:
                if (resultCode != RESULT_OK) {
                    ((TextView) findViewById(R.id.textView)).append("Login failed\n");
                    return;
                }

                authenticator.verify(
                    (Token) data.getSerializableExtra(Constants.REQUEST_TOKEN),
                            data.getStringExtra(Constants.OAUTH_VERIFIER)
                );
                break;

            default:
                break;
        }
    }
}
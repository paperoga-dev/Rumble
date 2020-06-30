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

package com.github.rumble.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.rumble.Constants;
import com.github.rumble.R;
import com.github.rumble.TumblrClient;
import com.github.rumble.api.Authenticate;
import com.github.rumble.exception.BaseException;

import org.scribe.model.Token;

public class Main extends AppCompatActivity {

    private Authenticate authenticator;
    static private TumblrClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new TumblrClient(getApplicationContext());

        client.setOnLoginListener(new TumblrClient.OnLoginListener() {
            @Override
            public void onAccessGranted() {
                setToLoggedState();
            }

            @Override
            public void onAccessRequest(
                    Authenticate authenticator,
                    Token requestToken,
                    String authenticationUrl) {

                setAuthenticator(authenticator);

                Intent i = new Intent(Main.this, Login.class);
                i.putExtra(Constants.REQUEST_TOKEN, requestToken);
                i.putExtra(Constants.AUTH_URL, authenticationUrl);
                startActivityForResult(i, Constants.PERFORM_LOGIN);
            }

            @Override
            public void onAccessDenied() {
                Toast.makeText(getApplicationContext(), "Access denied", Toast.LENGTH_SHORT).show();
                resetToLoginState();
            }

            @Override
            public void onLoginFailure(BaseException e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                resetToLoginState();
            }
        });

        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE).edit()
                        .remove(Constants.OAUTH_TOKEN_KEY)
                        .remove(Constants.OAUTH_TOKEN_SECRET_KEY)
                        .apply();

                resetToLoginState();
            }
        });

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.login();
            }
        });

        resetToLoginState();

        findViewById(R.id.btnDashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), com.github.rumble.activities.Dashboard.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnMyBlog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), com.github.rumble.activities.Blog.class);
                startActivity(intent);
            }
        });

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                client.login();
            }
        });
    }

    private void setAuthenticator(Authenticate authenticator) {
        this.authenticator = authenticator;
    }

    private void resetToLoginState() {
        findViewById(R.id.btnLogin).setVisibility(View.VISIBLE);
        findViewById(R.id.btnDashboard).setEnabled(false);
        findViewById(R.id.btnMyBlog).setEnabled(false);
        findViewById(R.id.btnLogout).setVisibility(View.INVISIBLE);
    }

    private void setToLoggedState() {
        findViewById(R.id.btnLogin).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnDashboard).setEnabled(true);
        findViewById(R.id.btnMyBlog).setEnabled(true);
        findViewById(R.id.btnLogout).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(Constants.APP_NAME, "ActivityResult");

        switch (requestCode) {
            case Constants.PERFORM_LOGIN:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Login failed: " + String.valueOf(resultCode), Toast.LENGTH_SHORT).show();
                    resetToLoginState();
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

    static public TumblrClient getClient() {
        return client;
    }
}

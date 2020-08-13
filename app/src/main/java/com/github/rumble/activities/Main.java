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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.github.rumble.Constants;
import com.github.rumble.R;

public class Main extends Base {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                getClient().login();
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

        findViewById(R.id.btnViewPost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), com.github.rumble.activities.Post.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnDifference).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), com.github.rumble.activities.Follow.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), com.github.rumble.activities.Test.class);
                startActivity(intent);
            }
        });

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getClient().login();
            }
        });
    }

    @Override
    protected void resetToLoginState() {
        findViewById(R.id.btnLogin).setVisibility(View.VISIBLE);
        findViewById(R.id.btnDashboard).setEnabled(false);
        findViewById(R.id.btnMyBlog).setEnabled(false);
        findViewById(R.id.btnLogout).setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setToLoggedState() {
        findViewById(R.id.btnLogin).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnDashboard).setEnabled(true);
        findViewById(R.id.btnMyBlog).setEnabled(true);
        findViewById(R.id.btnLogout).setVisibility(View.VISIBLE);
    }
}

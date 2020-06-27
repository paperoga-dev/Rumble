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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.rumble.api.Authenticate;
import com.github.rumble.api.array.CompletionInterface;
import com.github.rumble.exception.BaseException;
import com.github.rumble.blog.array.Posts;
import com.github.rumble.exception.NetworkException;
import com.github.rumble.posts.Post;

import org.scribe.model.Token;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TumblrClient client;
    private Authenticate authenticator;
    private TextView tv;
    private Post.Adapter postsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new TumblrClient(getApplicationContext());

        tv = findViewById(R.id.textView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
        );
        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), layoutManager.getOrientation()));
        layoutManager.scrollToPosition(0);
        rv.setLayoutManager(layoutManager);

        client.setOnLoginListener(new TumblrClient.OnLoginListener() {
            @Override
            public void onAccessGranted() {
                tv.append("Logged in!\n");

                tv.append("Me: " + client.getMe().getName() + "\n");
                tv.append("My posts:\n");

                client.call(
                        Posts.Api.class,
                        "papero-tombo",
                        0,
                        40,
                        new CompletionInterface<Post.Item, Post.Data>() {
                            @Override
                            public void onSuccess(List<Post.Item> result, int offset, int limit, int count) {
                                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                                Post.Adapter adapter = new Post.Adapter(result);
                                recyclerView.setAdapter(adapter);
                            }

                            @Override
                            public void onFailure(BaseException e) {
                                tv.append("Post retrieve failure!\n");
                            }
                        }
                );

                /*
                client.call(
                        OldPosts.Api.class,
                        "masoassai",
                        0,
                        -1,
                        new TumblrClient.OnArrayCompletion<OldPosts.Post>() {
                            @Override
                            public void onSuccess(List<OldPosts.Post> result, int offset, int limit, int count) {
                                for (OldPosts.Post post : result) {
                                    tv.append("Type: " + post.getClass().getName() + "\n");
                                    tv.append("Blog name: " + post.getBlogName() + "\n");
                                    tv.append("Id: " + post.getId() + "\n");
                                    tv.append("URL: " + post.getUrl() + "\n");
                                    tv.append("Slug: " + post.getSlug() + "\n");
                                    tv.append("Timestamp: " + post.getTimestamp().toString() + "\n");
                                    tv.append("Summary: " + post.getSummary() + "\n");
                                }
                            }
                        }
                );
                */

                /*
                for (BlogInfo.Data blog : client.getMe().getBlogs()) {
                    tv.append("\t" + blog.getName() + "\n");
                    tv.append("\t\t" + blog.getTitle() + "\n");

                    client.call(
                            BlogInfo.Api.class,
                            new TumblrClient.OnCompletion<BlogInfo.Data>() {
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

                client.call(
                        Following.Api.class,
                        client.getMe().getName(),
                        0,
                        -1,
                        new TumblrClient.OnArrayCompletion<BlogInfo.Base>() {
                            @Override
                            public void onSuccess(List<BlogInfo.Base> result, int offset, int limit, int count) {
                                tv.append("\toffset = " + String.valueOf(offset) + "\n");
                                tv.append("\tlimit = " + String.valueOf(limit) + "\n");
                                tv.append("\tcount = " + String.valueOf(count) + "\n");

                                for (BlogInfo.Base blog : result) {
                                    tv.append("\t\t" + blog.getName() + "\n");
                                    tv.append("\t\t" + blog.getTitle() + "\n");
                                    tv.append("\t\t" + blog.getDescription() + "\n");
                                    tv.append("\t\t" + blog.getUrl() + "\n");
                                    tv.append("\t\t" + blog.getUuid() + "\n");
                                    tv.append("\t\t" + blog.getUpdated().toString() + "\n");
                                }
                            }
                        }
                );
                */
            }

            @Override
            public void onAccessRequest(
                    Authenticate authenticator,
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

            @Override
            public void onLoginFailure(BaseException e) {
                if (e instanceof NetworkException)
                    tv.append("Network error\n");
                else
                    tv.append("Error\n");
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

    private void setAuthenticator(Authenticate authenticator) {
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

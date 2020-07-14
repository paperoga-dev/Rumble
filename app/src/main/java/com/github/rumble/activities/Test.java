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

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.github.rumble.R;
import com.github.rumble.api.array.CompletionInterface;
import com.github.rumble.blog.array.Posts;
import com.github.rumble.blog.simple.Info;
import com.github.rumble.exception.BaseException;
import com.github.rumble.posts.Post;

import java.util.List;

public class Test extends Base {
    private Post.Adapter postsAdapter;
    private int currentOffset;
    private boolean fetching;
    private String blogName;
    private int totalCount;
    private boolean stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        this.currentOffset = 16060;
        this.fetching = false;

        this.blogName = "masoassai";
        this.stop = false;

        ((TextView) findViewById(R.id.txtDebugMessage)).setMovementMethod(new ScrollingMovementMethod());

        postsAdapter = new Post.Adapter(this);

        getClient().call(
                com.github.rumble.blog.simple.Info.Api.class,
                blogName,
                new com.github.rumble.api.simple.CompletionInterface<Info.Data>() {
                    @Override
                    public void onFailure(BaseException e) {
                        viewException(e);
                    }

                    @Override
                    public void onSuccess(Info.Data result) {
                        totalCount = result.getTotalPosts() - currentOffset;

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                fetchNewItems();
                            }
                        });
                    }
                }
        );

        findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop = true;
            }
        });
    }

    private void viewException(BaseException e) {
        TextView tv = findViewById(R.id.txtDebugMessage);

        tv.append(e.getStackTrace() + "\n\n");


        if (e instanceof com.github.rumble.exception.JsonException) {
            com.github.rumble.exception.JsonException je = (com.github.rumble.exception.JsonException) e;
            tv.append(je.getJsonData() + "\n\n");
        }

        if (e instanceof com.github.rumble.exception.NetworkException) {
            com.github.rumble.exception.NetworkException ne = (com.github.rumble.exception.NetworkException) e;
            tv.append("OAuthException: " + ne.getException().getMessage() + "\n");
        }

        if (e instanceof com.github.rumble.exception.ResponseException) {
            com.github.rumble.exception.ResponseException re = (com.github.rumble.exception.ResponseException) e;
            tv.append("Response code: " + re.getResponseCode() + "\n");
            tv.append("Response message: " + re.getResponseMessage() + "\n");
        }

        tv.append(e.getMessage());
    }

    private void fetchNewItems() {
        if (fetching)
            return;

        fetching = true;

        getClient().call(
                Posts.Api.class,
                blogName,
                currentOffset,
                20,
                new CompletionInterface<Post.Item, Post.Data>() {
                    @Override
                    public void onSuccess(List<Post.Item> result, int offset, int limit, int count) {
                        postsAdapter.addItems(result);
                        currentOffset += result.size();
                        fetching = false;

                        TextView tv = findViewById(R.id.txtStatus);
                        tv.setText("Blog: " + blogName + " - Posts: " + postsAdapter.getCount() + " / " + totalCount);

                        if (stop) {
                            stop = false;
                            return;
                        }

                        if (postsAdapter.getCount() < totalCount) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    fetchNewItems();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(BaseException e) {
                        viewException(e);
                    }
                }
        );
    }
}
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
import android.util.Log;
import android.widget.Toast;

import com.github.rumble.Constants;
import com.github.rumble.R;
import com.github.rumble.api.array.CompletionInterface;
import com.github.rumble.exception.BaseException;
import com.github.rumble.posts.Post;
import com.github.rumble.ui.ListView;

import java.util.List;

public class Dashboard extends Base {

    private int currentOffset;
    private boolean fetching;
    private Post.Adapter postsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        this.currentOffset = 0;
        this.fetching = false;

        ListView lv = findViewById(R.id.lvDashboard);

        postsAdapter = new Post.Adapter(this);
        lv.setAdapter(postsAdapter);

        lv.setOnUpdateListener(new ListView.OnUpdateListener() {
            @Override
            public void onUpdate() {
                if (!fetching)
                    fetchNewItems();
            }
        });

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                fetchNewItems();
            }
        });
    }

    private void fetchNewItems() {
        if (fetching)
            return;

        fetching = true;

        getClient().call(
                com.github.rumble.user.array.Dashboard.Api.class,
                currentOffset,
                -1,
                new CompletionInterface<Post.Item, com.github.rumble.user.array.Dashboard.Data>() {

                    @Override
                    public void onFailure(BaseException e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        Log.v(Constants.APP_NAME, e.toString());
                        fetching = false;
                    }

                    @Override
                    public void onSuccess(List<Post.Item> result, int offset, int limit, int count) {
                        postsAdapter.addItems(result);
                        currentOffset += result.size();
                        fetching = false;
                    }
                }
        );
    }
}
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

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.rumble.Constants;
import com.github.rumble.R;
import com.github.rumble.api.simple.CompletionInterface;
import com.github.rumble.exception.BaseException;

public class Post extends Base {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        findViewById(R.id.btnPostSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getClient().call(
                        com.github.rumble.blog.simple.Post.Api.class,
                        ((EditText) findViewById(R.id.edtBlogId)).getText().toString(),
                        ((EditText) findViewById(R.id.edtPostId)).getText().toString(),
                        new CompletionInterface<com.github.rumble.posts.Post.Item>() {
                            @Override
                            public void onSuccess(com.github.rumble.posts.Post.Item result) {
                                result.render(
                                        (LinearLayout) findViewById(R.id.postLayout),
                                        Resources.getSystem().getDisplayMetrics().widthPixels
                                );
                            }

                            @Override
                            public void onFailure(BaseException e) {
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                Log.v(Constants.APP_NAME, e.toString());
                            }
                        }
                );
            }
        });
    }
}
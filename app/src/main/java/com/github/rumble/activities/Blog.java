package com.github.rumble.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.rumble.Constants;
import com.github.rumble.R;
import com.github.rumble.api.array.CompletionInterface;
import com.github.rumble.blog.array.Posts;
import com.github.rumble.exception.BaseException;
import com.github.rumble.posts.Post;

import java.util.List;

public class Blog extends AppCompatActivity {
    private Post.Adapter postsAdapter;
    private int currentOffset;
    private boolean fetching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        this.currentOffset = 0;
        this.fetching = false;

        ListView lv = findViewById(R.id.lvBlogContent);
        postsAdapter = new Post.Adapter(this);
        lv.setAdapter(postsAdapter);

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                ListView lv = (ListView) view;

                if ((scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) &&
                        ((lv.getLastVisiblePosition() - lv.getHeaderViewsCount() - lv.getFooterViewsCount()) >= (lv.getAdapter().getCount() - 1)) &&
                        !fetching) {
                    fetchNewItems();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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

        postsAdapter.addNullItem();
        fetching = true;

        Main.getClient().call(
                Posts.Api.class,
                Main.getClient().getMe().getBlogs().get(0).getName(),
                currentOffset,
                20,
                new CompletionInterface<Post.Item, Post.Data>() {
                    @Override
                    public void onSuccess(List<Post.Item> result, int offset, int limit, int count) {
                        postsAdapter.removeNullItem();
                        postsAdapter.addItems(result);
                        currentOffset += result.size();
                        fetching = false;
                    }

                    @Override
                    public void onFailure(BaseException e) {
                        postsAdapter.removeNullItem();
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        Log.v(Constants.APP_NAME, e.toString());
                        fetching = false;
                    }
                }
        );
    }
}
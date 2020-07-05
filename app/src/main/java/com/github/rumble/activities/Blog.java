package com.github.rumble.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

        RecyclerView rv = findViewById(R.id.rvBlog);
        rv.setLayoutManager(new LinearLayoutManager(this));

        postsAdapter = new Post.Adapter();
        rv.setAdapter(postsAdapter);

        rv.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!fetching) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == postsAdapter.getItemCount() - 1) {
                        fetchNewItems();
                    }
                }
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
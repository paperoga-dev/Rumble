package com.github.rumble.activities;

import androidx.appcompat.app.AppCompatActivity;

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

public class Dashboard extends AppCompatActivity {

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

        Main.getClient().call(
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
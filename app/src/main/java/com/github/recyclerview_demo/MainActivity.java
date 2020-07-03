package com.github.recyclerview_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

// Reference: https://www.journaldev.com/24041/android-recyclerview-load-more-endless-scrolling

public class MainActivity extends AppCompatActivity {

    private boolean isLoading;
    private Adapter adapter;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.isLoading = false;
        this.adapter = new Adapter();
        this.counter = 0;

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1) {
                        //bottom of list!
                        adapter.addNullItem();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapter.removeNullItem();
                                addNewItems();
                                isLoading = false;
                            }
                        }, 2000);

                        isLoading = true;
                    }
                }
            }
        });

        addNewItems();
    }

    private void addNewItems() {
        List<String> values = new ArrayList<>();
        for (int i = 0; i < 20; ++i)
            values.add(String.valueOf(++counter));

        adapter.addItems(values);
    }
}
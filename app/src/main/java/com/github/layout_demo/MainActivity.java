package com.github.layout_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static class Item {
        public int textSize;
        public int color;
        public String text;

        public Item(int textSize, int color, String text) {
            this.textSize = textSize;
            this.color = color;
            this.text = text;
        }
    }

    private LinearLayout currentRow;
    private Random random;

    private static final List<Item> items = new ArrayList<Item>() {{
        add(new Item(24, Color.YELLOW, "Hello!"));
        add(new Item(12, Color.GREEN, "The quick brown fox jumps over the lazy dog"));
        add(new Item(16, Color.CYAN, "We came, we saw, we kicked its ass!"));
        add(new Item(48, Color.RED, "NO!"));
        add(new Item(32, Color.MAGENTA, "Scotty, we need more power!"));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.currentRow = null;
        this.random = new Random();

        findViewById(R.id.btnAddRow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout mainLayout = findViewById(R.id.layoutMain);
                currentRow = (LinearLayout) inflater.inflate(R.layout.layout_row, mainLayout, false);
                mainLayout.addView(currentRow);
            }
        });

        findViewById(R.id.btnAddColumn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentRow == null)
                    return;

                LayoutInflater inflater = getLayoutInflater();
                TextView textView = (TextView) inflater.inflate(R.layout.textview_item, currentRow, false);

                Item item = items.get(Math.abs(random.nextInt()) % items.size());

                textView.setText(item.text);
                textView.setTextSize(item.textSize);
                // ViewGroup.LayoutParams params = textView.getLayoutParams();
                // params.height = (random.nextInt() % 50) + 200;
                textView.setBackground(new ColorDrawable(item.color));
                currentRow.addView(textView);
            }
        });
    }
}
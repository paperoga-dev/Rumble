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

package com.github.rumble.posts;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.rumble.R;
import com.github.rumble.api.array.ContentInterface;
import com.github.rumble.blog.simple.Info;
import com.github.rumble.posts.layout.Rows;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public interface Post {

    class Base {
        private long id;

        public Base(JSONObject idObject) throws JSONException {
            super();

            this.id = idObject.getLong("id");
        }

        public long getId() {
            return id;
        }
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View view) {
                super(view);
            }
        }

        private final List<Item> posts;

        public Adapter(List<Item> posts) {
            super();

            this.posts = posts;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FlexboxLayout fbl = (FlexboxLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
            posts.get(viewType).render(parent.getContext(), fbl, parent.getWidth());
            return new ViewHolder(fbl);
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.ViewHolder viewHolder, int position) {
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }
    }

    class Trail extends Base {
        private Info.Base blog;
        private List<ContentItem> content;
        private List<LayoutItem> layout;
        private List<Trail> trail;

        public Trail(JSONObject postObject, JSONObject idObject) throws JSONException {
            super(idObject);

            this.blog = new Info.Base(postObject.getJSONObject("blog"));

            this.content = new ArrayList<>();
            JSONArray content = postObject.getJSONArray("content");
            for (int i = 0; i < content.length(); ++i) {
                this.content.add(ContentItem.create(content.getJSONObject(i)));
            }

            this.layout = new ArrayList<>();
            JSONArray layout = postObject.optJSONArray("layout");
            if (layout != null) {
                for (int i = 0; i < layout.length(); ++i) {
                    this.layout.add(LayoutItem.create(layout.getJSONObject(i)));
                }
            }

            this.trail = new ArrayList<>();
            JSONArray trail = postObject.optJSONArray("trail");
            if (trail != null) {
                for (int i = 0; i < trail.length(); ++i) {
                    this.trail.add(
                            new Trail(
                                    trail.getJSONObject(i),
                                    trail.getJSONObject(i).getJSONObject("post")
                            )
                    );
                }
            }
        }

        public Info.Base getBlog() {
            return blog;
        }

        public List<ContentItem> getContent() {
            return content;
        }

        public List<LayoutItem> getLayout() {
            return layout;
        }

        public List<Trail> getTrail() {
            return trail;
        }

        public List<List<Integer>> getBlocksLayout() {
            SortedSet<Integer> indexes = new TreeSet<>();

            for (int i = 0; i < getContent().size(); ++i)
                indexes.add(i);

            ArrayList<List<Integer>> list = new ArrayList<>();

            for (int i = 0; i < getLayout().size(); ++i) {
                if (getLayout().get(i) instanceof Rows) {
                    Rows rows = (Rows) getLayout().get(i);

                    for (Rows.Blocks blocks : rows.getBlocksList()) {
                        ArrayList<Integer> innerBlock = new ArrayList<>();

                        for (Integer index : blocks.getIndexes()) {
                            innerBlock.add(index);
                            indexes.remove(index);
                        }

                        list.add(innerBlock);
                    }
                }
            }

            for (Integer index : indexes) {
                ArrayList<Integer> innerBlock = new ArrayList<>();
                innerBlock.add(index);
                list.add(innerBlock);
            }

            return list;
        }

        public void render(Context context, FlexboxLayout flexLayout, int viewWidth) {
            for (Trail trail : getTrail()) {
                trail.render(context, flexLayout, viewWidth);
            }

            FlexboxLayout.LayoutParams lpItem = new FlexboxLayout.LayoutParams(flexLayout.getLayoutParams());
            lpItem.setWidth(FlexboxLayout.LayoutParams.WRAP_CONTENT);
            lpItem.setHeight(FlexboxLayout.LayoutParams.WRAP_CONTENT);

            FlexboxLayout.LayoutParams lpFirstItem = new FlexboxLayout.LayoutParams(lpItem);
            lpFirstItem.setWrapBefore(true);

            TextView tvTitle = new TextView(context);
            SpannableStringBuilder ssbTitle = new SpannableStringBuilder(getBlog().getName());
            ssbTitle.setSpan(
                    new android.text.style.StyleSpan(Typeface.BOLD),
                    0,
                    getBlog().getName().length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            tvTitle.setText(ssbTitle);
            flexLayout.addView(tvTitle, lpFirstItem);

            List<List<Integer>> rows = getBlocksLayout();

            for (List<Integer> row : rows) {
                boolean isFirst = true;

                for (Integer item : row) {
                    View itemView = getContent().get(item).render(context, viewWidth / row.size());
                    if (isFirst)
                        flexLayout.addView(itemView, lpFirstItem);
                    else
                        flexLayout.addView(itemView, lpItem);
                    isFirst = false;
                }
            }
        }
    }

    class Item extends Trail {
        private Date timestamp;
        private List<String> tags;
        private String url;
        private String shortUrl;

        public Item(JSONObject postObject) throws JSONException {
            super(postObject, postObject);

            this.timestamp = new Date(postObject.getLong("timestamp") * 1000L);
            this.url = postObject.getString("post_url");
            this.shortUrl = postObject.getString("short_url");

            this.tags = new ArrayList<>();
            JSONArray tags = postObject.optJSONArray("tags");
            if (tags != null) {
                for (int i = 0; i < tags.length(); ++i) {
                    this.tags.add(tags.getString(i));
                }
            }
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public List<String> getTags() {
            return tags;
        }

        public String getUrl() {
            return url;
        }

        public String getShortUrl() {
            return shortUrl;
        }

        public void render(Context context, FlexboxLayout flexLayout, int viewWidth) {
            super.render(context, flexLayout, viewWidth);

            FlexboxLayout.LayoutParams lpItem = new FlexboxLayout.LayoutParams(flexLayout.getLayoutParams());
            lpItem.setWidth(FlexboxLayout.LayoutParams.WRAP_CONTENT);
            lpItem.setHeight(FlexboxLayout.LayoutParams.WRAP_CONTENT);

            FlexboxLayout.LayoutParams lpFirstItem = new FlexboxLayout.LayoutParams(lpItem);
            lpFirstItem.setWrapBefore(true);

            TextView tvTimestamp = new TextView(context);
            tvTimestamp.setText(getTimestamp().toString());
            flexLayout.addView(tvTimestamp, lpFirstItem);

            boolean isFirst = true;
            for (String tag : getTags()) {
                TextView tvTag = new TextView(context);
                tvTag.setText("#" + tag);

                if (isFirst)
                    flexLayout.addView(tvTag, lpFirstItem);
                else
                    flexLayout.addView(tvTag, lpItem);

                isFirst = false;
            }
        }
    }

    class Data implements ContentInterface<Item> {
        private int totalPosts;
        private List<Item> posts;

        public Data(JSONObject postsObject) throws JSONException {
            super();

            this.totalPosts = postsObject.getInt("total_posts");
            this.posts = new ArrayList<>();

            JSONArray posts = postsObject.getJSONArray("posts");
            for (int i = 0; i < posts.length(); ++i) {
                this.posts.add(new Item(posts.getJSONObject(i)));
            }
        }

        @Override
        public int getCount() {
            return totalPosts;
        }

        @Override
        public List<Item> getItems() {
            return posts;
        }
    }
}

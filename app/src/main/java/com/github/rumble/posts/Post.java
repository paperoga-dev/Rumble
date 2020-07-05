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

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.rumble.R;
import com.github.rumble.api.array.ContentInterface;
import com.github.rumble.blog.simple.Info;
import com.github.rumble.posts.layout.Rows;

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

        public Base() {
            super();

            this.id = 0;
        }

        public Base(JSONObject idObject) throws JSONException {
            super();

            this.id = idObject.getLong("id");
        }

        public long getId() {
            return id;
        }
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static class LoadingViewHolder extends RecyclerView.ViewHolder {
            public LoadingViewHolder(View itemView) {
                super(itemView);
            }
        }

        public static class ItemViewHolder extends RecyclerView.ViewHolder {

            public LinearLayout layoutPostContent;
            public TextView tvTimeStamp;
            public TextView tvTags;

            public ItemViewHolder(View itemView) {
                super(itemView);

                this.layoutPostContent = itemView.findViewById(R.id.layoutPostContent);
                this.tvTimeStamp = itemView.findViewById(R.id.tvTimestamp);
                this.tvTags = itemView.findViewById(R.id.tvTags);
            }
        }

        private final List<Item> posts;
        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        public Adapter() {
            super();

            this.posts = new ArrayList<>();
        }

        public void addNullItem() {
            posts.add(null);
            notifyItemInserted(posts.size() - 1);
        }

        public void removeNullItem() {
            posts.remove(posts.size() - 1);
            notifyItemRemoved(posts.size());
        }

        public void addItems(List<Item> items) {
            posts.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return (posts.get(position) == null)? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case VIEW_TYPE_ITEM:
                    return new ItemViewHolder(
                            LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false)
                    );

                case VIEW_TYPE_LOADING:
                    return new LoadingViewHolder(
                            LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.items_loading, parent, false)
                    );

                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof ItemViewHolder) {
                ItemViewHolder ivh = (ItemViewHolder) viewHolder;

                int itemWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
                posts.get(position).render((ItemViewHolder) viewHolder, itemWidth);
            }
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

        public Trail(JSONObject postObject, String brokenBlogName) throws JSONException {
            super();

            this.blog = new Info.Base(brokenBlogName);

            loadContent(postObject);
        }

        public Trail(JSONObject postObject, JSONObject idObject) throws JSONException {
            super(idObject);

            this.blog = new Info.Base(postObject.getJSONObject("blog"));

            loadContent(postObject);
        }

        private void loadContent(JSONObject postObject) throws JSONException {
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
                    String brokenBlogName = trail.getJSONObject(i).optString("broken_blog_name");
                    if (brokenBlogName != null) {
                        this.trail.add(
                                new Trail(
                                        trail.getJSONObject(i),
                                        brokenBlogName
                                )
                        );
                    } else {
                        this.trail.add(
                                new Trail(
                                        trail.getJSONObject(i),
                                        trail.getJSONObject(i).getJSONObject("post")
                                )
                        );
                    }
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

        public void render(Adapter.ItemViewHolder viewHolder, LinearLayout postLayout, int viewWidth) {
            for (Trail trail : getTrail()) {
                trail.render(viewHolder, postLayout, viewWidth);
            }

            View postContentView = LayoutInflater.from(viewHolder.itemView.getContext()).inflate(R.layout.post_content, postLayout);
            TextView tvBlogName = postContentView.findViewById(R.id.blogTitle);
            tvBlogName.setText(getBlog().getName());

            /*
            int dp5 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, viewHolder.itemView.getContext().getResources().getDisplayMetrics());

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f);
            lp.setMargins(dp5, dp5, dp5, dp5);

            LinearLayout contentLayout = postContentView.findViewById(R.id.blogContent);
            List<List<Integer>> rows = getBlocksLayout();

            for (List<Integer> row : rows) {
                LinearLayout rowLayout = (LinearLayout) LayoutInflater.from(viewHolder.itemView.getContext()).inflate(R.layout.post_item_row, contentLayout);

                for (Integer item : row) {
                    View itemView = getContent().get(item).render(viewHolder.itemView.getContext(), viewWidth / row.size() - dp5 * 2);
                    if (itemView instanceof TextView)
                        Linkify.addLinks((TextView) itemView, Linkify.ALL);

                    rowLayout.addView(itemView, lp);
                }
            }
            */
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

        public void render(Adapter.ItemViewHolder viewHolder, int viewWidth) {
            viewHolder.layoutPostContent.removeAllViews();
            super.render(viewHolder, viewHolder.layoutPostContent, viewWidth);
            viewHolder.layoutPostContent.requestLayout();

            viewHolder.tvTimeStamp.setText(getTimestamp().toString());

            viewHolder.tvTags.setMaxLines(getTags().size());

            for (String tag : getTags()) {
                viewHolder.tvTags.append("#" + tag + "\n");
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

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
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.rumble.R;
import com.github.rumble.api.array.ContentInterface;
import com.github.rumble.blog.simple.Info;
import com.github.rumble.exception.RuntimeException;
import com.github.rumble.posts.layout.Ask;
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

    class Adapter extends ArrayAdapter<Item> {
        public Adapter(Context context) {
            super(context, 0, new ArrayList<Item>());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.post_item, parent, false);

            getItem(position).render((LinearLayout) convertView.findViewById(R.id.llItemContent), Resources.getSystem().getDisplayMetrics().widthPixels);

            ((TextView) convertView.findViewById(R.id.tvContentTimestamp)).setText(getItem(position).getTimestamp().toString());

            TextView tvContentTags = convertView.findViewById(R.id.tvContentTags);
            tvContentTags.setMaxLines(getItem(position).getTags().size());

            for (String tag : getItem(position).getTags()) {
                tvContentTags.append("#" + tag + "\n");
            }

            return convertView;
        }

        public void addItems(List<Item> items) {
            addAll(items);
            notifyDataSetChanged();
        }
    }

    class Trail extends Base {
        private Info.Base blog;
        private List<ContentItem> content;
        private List<LayoutItem> layout;
        private List<Trail> trail;
        private String askingName;
        private String askingUrl;

        public Trail(JSONObject postObject, String brokenBlogName) throws JSONException, com.github.rumble.exception.RuntimeException {
            super();

            this.blog = new Info.Base(brokenBlogName);

            loadContent(postObject);
        }

        public Trail(JSONObject postObject, JSONObject idObject) throws JSONException, com.github.rumble.exception.RuntimeException {
            super(idObject);

            this.blog = new Info.Base(postObject.getJSONObject("blog"));

            loadContent(postObject);
        }

        private void loadContent(JSONObject postObject) throws JSONException, com.github.rumble.exception.RuntimeException {
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
                    String brokenBlogName = trail.getJSONObject(i).optString("broken_blog_name", null);
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

            this.askingName = postObject.optString("asking_name");
            this.askingUrl = postObject.optString("asking_url");
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

        public String getAskingName() {
            return askingName;
        }

        public String getAskingUrl() {
            return askingUrl;
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
                } else if (getLayout().get(i) instanceof Ask) {
                    ArrayList<Integer> askBlock = new ArrayList<>();
                    askBlock.add(-1);
                    list.add(askBlock);
                    indexes.remove(i);
                }
            }

            for (Integer index : indexes) {
                ArrayList<Integer> innerBlock = new ArrayList<>();
                innerBlock.add(index);
                list.add(innerBlock);
            }

            return list;
        }

        private void render(LinearLayout linearLayout, int viewWidth, boolean isAReblog) {
            for (Trail trail : getTrail()) {
                LinearLayout llTrailPostContent = (LinearLayout) LayoutInflater.from(linearLayout.getContext()).inflate(
                        R.layout.post_content,
                        linearLayout,
                        false
                );

                trail.render(llTrailPostContent, viewWidth, true);
                llTrailPostContent.setBackground(new ColorDrawable(0xfff6dba4));
                linearLayout.addView(llTrailPostContent);
            }

            LinearLayout llPostContent = (LinearLayout) LayoutInflater.from(linearLayout.getContext()).inflate(
                    R.layout.post_content,
                    linearLayout,
                    false
            );

            String blogName = getBlog().getName();
            if (isAReblog)
                blogName += " \u21BB";
            ((TextView) llPostContent.findViewById(R.id.tvContentBlogName)).setText(blogName);

            List<List<Integer>> rows = getBlocksLayout();

            for (List<Integer> row : rows) {
                LinearLayout rowLayout = (LinearLayout) LayoutInflater.from(llPostContent.getContext()).inflate(R.layout.post_item_row, llPostContent, false);

                int dp5 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, llPostContent.getContext().getResources().getDisplayMetrics());

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f);
                lp.setMargins(dp5, dp5, dp5, dp5);

                for (Integer item : row) {
                    if (item == -1) {
                        TextView itemView = (TextView) getContent().get(0).render(llPostContent.getContext(), viewWidth / row.size() - dp5 * 2);

                        itemView.setText(Html.fromHtml("<b>" + getAskingName() + "</b><br>" + itemView.getText()));

                        GradientDrawable gd = new GradientDrawable();
                        gd.setShape(GradientDrawable.RECTANGLE);
                        gd.setColor(Color.LTGRAY);
                        gd.setStroke(2, Color.GRAY);
                        gd.setCornerRadius(15.0f);
                        itemView.setBackground(gd);

                        rowLayout.addView(itemView, lp);
                    } else {
                        View itemView = getContent().get(item).render(llPostContent.getContext(), viewWidth / row.size() - dp5 * 2);
                        if (itemView instanceof VideoView) {
                            VideoView vv = (VideoView) itemView;

                            rowLayout.addView(vv, new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    vv.getMinimumHeight(),
                                    1.0f)
                            );
                        } else {
                            rowLayout.addView(itemView, lp);
                        }
                    }
                }

                llPostContent.addView(rowLayout);
            }

            linearLayout.addView(llPostContent);
        }

        public void render(LinearLayout linearLayout, int viewWidth) {
            render(linearLayout, viewWidth, false);
        }
    }

    class Item extends Trail {
        private Date timestamp;
        private List<String> tags;
        private String url;
        private String shortUrl;

        public Item(JSONObject postObject) throws JSONException, com.github.rumble.exception.RuntimeException {
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
    }

    class Data implements ContentInterface<Item> {
        private int totalPosts;
        private List<Item> posts;

        public Data(JSONObject postsObject) throws JSONException, com.github.rumble.exception.RuntimeException {
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

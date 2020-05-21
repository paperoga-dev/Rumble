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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.rumble.BlogInfo;
import com.github.rumble.R;
import com.github.rumble.TumblrArray;
import com.github.rumble.TumblrArrayItem;
import com.github.rumble.posts.layout.Rows;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public interface Posts {

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

        private final List<Posts.Post> posts;

        public Adapter(List<Posts.Post> posts) {
            super();

            this.posts = posts;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            WebView wv = (WebView) LayoutInflater.from(parent.getContext()).inflate(R.layout.webview_item, null);
            wv.getSettings().setJavaScriptEnabled(true);
            wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
            wv.getSettings().setLoadWithOverviewMode(true);
            wv.getSettings().setUseWideViewPort(true);
            wv.getSettings().setGeolocationEnabled(false);
            wv.getSettings().setNeedInitialFocus(false);
            wv.getSettings().setSaveFormData(false);
            wv.getSettings().setDefaultFontSize(40);

            Post post = posts.get(viewType);

            wv.loadData("<html><head><style type=\"text/css\">" + parent.getContext().getResources().getString(R.string.post_css) + "</style></head><body>" +
                            post.render(parent.getWidth()) +
                            "</body></html>",
                    "text/html",
                    "UTF-8"
            );
            return new ViewHolder(wv);
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder viewHolder, int position) {
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }
    }

    class Trail extends Base {
        private BlogInfo.Base blog;
        private List<ContentItem> content;
        private List<LayoutItem> layout;
        private List<Trail> trail;

        public Trail(JSONObject postObject, JSONObject idObject) throws JSONException {
            super(idObject);

            this.blog = new BlogInfo.Base(postObject.getJSONObject("blog"));

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

        public BlogInfo.Base getBlog() {
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

        public String render(int viewWidth) {
            String content = "";

            for (Trail trail : getTrail()) {
                content += trail.render(viewWidth);
            }

            content += "<section id=\"blog_title\"><div>" + getBlog().getName() + "</div></section>";

            List<List<Integer>> rows = getBlocksLayout();

            for (List<Integer> row : rows) {
                content += "<section id=\"row\">";

                for (Integer item : row) {
                    content += "<div>" + getContent().get(item).render(viewWidth / row.size()) + "</div><div>&nbsp;</div>";
                }

                content += "</section><section id=\"row\"><div>&nbsp;</div></section>";
            }

            return content;
        }
    }

    class Post extends Trail {
        private Date timestamp;
        private List<String> tags;
        private String url;
        private String shortUrl;


        public Post(JSONObject postObject) throws JSONException {
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

        public String render(int viewWidth) {
            String content = super.render(viewWidth);

            content += "<section id=\"timestamp\"><div>" + getTimestamp().toString() + "</div></section>";

            if (getTags() != null) {
                content += "<section id=\"tags\">";
                for (String tag : getTags()) {
                    content += "<div>#" + tag + "</div><div>&nbsp;</div>";
                }
                content += "</section>";
            }

            return content;
        }
    }

    class Data implements TumblrArrayItem<Post> {
        private int totalPosts;
        private List<Post> posts;

        Data(JSONObject postsObject) throws JSONException {
            super();

            this.totalPosts = postsObject.getInt("total_posts");
            this.posts = new ArrayList<>();

            JSONArray posts = postsObject.getJSONArray("posts");
            for (int i = 0; i < posts.length(); ++i) {
                this.posts.add(new Post(posts.getJSONObject(i)));
            }
        }

        @Override
        public int getCount() {
            return totalPosts;
        }

        @Override
        public List<Post> getItems() {
            return posts;
        }
    }

    class Api extends TumblrArray<Data> {

        public Api(
                Context context,
                OAuthService service,
                Token authToken,
                String appId,
                String appVersion,
                String[] additionalArgs) {
            super(context, service, authToken, appId, appVersion, additionalArgs);
        }

        @Override
        protected String getPath() {
            return super.getPath() + "/posts";
        }

        @Override
        public Map<String, String> defaultParams() {
            Map<String, String> m = super.defaultParams();

            m.put("npf", "true");

            return m;
        }

        @Override
        protected Data readData(JSONObject jsonObject) throws JSONException {
            return new Data(jsonObject);
        }
    }
}

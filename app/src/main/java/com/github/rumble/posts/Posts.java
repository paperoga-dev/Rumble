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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.rumble.BlogInfo;
import com.github.rumble.TumblrArray;
import com.github.rumble.TumblrArrayItem;
import com.github.rumble.posts.layout.Rows;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public interface Posts {

    class Base {
        private long id;

        public Base(JSONObject postObject) throws JSONException {
            super();

            this.id = postObject.getLong("id");
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
            return new ViewHolder(posts.get(viewType).render(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder viewHolder, int position) {
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }
    }

    class Post extends Base {
        static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
            public class ViewHolder extends RecyclerView.ViewHolder {
                public ViewHolder(View view) {
                    super(view);
                }
            }

            private final List<ContentItem> content;
            private final List<List<Integer>> blocksLayout;

            public Adapter(List<ContentItem> content, List<List<Integer>> blocksLayout) {
                super();

                this.content = content;
                this.blocksLayout = blocksLayout;
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @Override
            public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                List<Integer> item = blocksLayout.get(viewType);

                if (item.size() > 1) {
                    LinearLayout blockLayout = new LinearLayout(parent.getContext());
                    blockLayout.setOrientation(LinearLayout.HORIZONTAL);
                    blockLayout.setPadding(15, 15, 15, 15);

                    LinearLayout.LayoutParams blockLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    blockLayoutParams.setMargins(15, 15, 15, 15);
                    blockLayout.setLayoutParams(blockLayoutParams);

                    blockLayout.setGravity(Gravity.CENTER);

                    for (Integer index : item) {
                        View v = content.get(index).render(parent.getContext());
                        v.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                        );
                        blockLayout.addView(v, blockLayoutParams);
                    }

                    return new ViewHolder(blockLayout);
                } else {
                    View v = content.get(item.get(0)).render(parent.getContext());
                    v.setLayoutParams(
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                    );
                    return new ViewHolder(v);
                }
            }

            @Override
            public void onBindViewHolder(Adapter.ViewHolder viewHolder, int position) {
            }

            @Override
            public int getItemCount() {
                return blocksLayout.size();
            }
        }

        private BlogInfo.Base blog;
        private List<ContentItem> content;
        private List<LayoutItem> layout;

        public Post(JSONObject postObject) throws JSONException {
            super(postObject);

            this.blog = new BlogInfo.Base(postObject.getJSONObject("blog"));

            this.content = new ArrayList<>();
            JSONArray content = postObject.getJSONArray("content");
            for (int i = 0; i < content.length(); ++i) {
                this.content.add(ContentItem.create(content.getJSONObject(i)));
            }

            this.layout = new ArrayList<>();
            JSONArray layout = postObject.optJSONArray("layout");
            if (layout == null)
                return;

            for (int i = 0; i < layout.length(); ++i) {
                this.layout.add(LayoutItem.create(layout.getJSONObject(i)));
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

        public List<List<Integer>> getBlocksLayout() {
            SortedSet<Integer> indexes = new TreeSet<>();

            for (int i = 0; i < getContent().size(); ++i)
                indexes.add(i);

            ArrayList<List<Integer>> list = new ArrayList<>();

            for (int i = 0; i < getLayout().size(); ++i) {
                if (getLayout().get(i) instanceof Rows) {
                    ArrayList<Integer> innerBlock = new ArrayList<>();

                    Rows rows = (Rows) getLayout().get(i);

                    for (Rows.Blocks blocks : rows.getBlocksList()) {
                        for (Integer index : blocks.getIndexes()) {
                            innerBlock.add(index);
                            indexes.remove(index);
                        }
                    }

                    list.add(innerBlock);
                }
            }

            for (Integer index : indexes) {
                ArrayList<Integer> innerBlock = new ArrayList<>();
                innerBlock.add(index);
                list.add(innerBlock);
            }

            return list;
        }

        public View render(Context context) {
            RecyclerView lv = new RecyclerView(context);
            lv.setLayoutManager(new LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false
            ));
            lv.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            lv.setAdapter(new Adapter(getContent(), getBlocksLayout()));
            return lv;
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

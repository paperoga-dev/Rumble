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

package com.github.rumble;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.List;

public interface UserInfo {
    enum PostFormat {
        Html,
        MarkDown,
        Raw
    };

    enum Type {
        Public,
        Private
    };

    enum Tweet {
        Auto,
        Yes,
        No
    };

    class Blog {
        private String name;       // String - the short name of the blog
        private String url;        // String - the URL of the blog
        private String title;      // String - the title of the blog
        private boolean primary;   // Boolean - indicates if this is the user's primary blog
        private int followers;     // Number - total count of followers for this blog
        private Tweet tweet;     // String - indicate if posts are tweeted auto, Y, N
        private boolean facebook;  // String - indicate if posts are sent to facebook Y, N
        private Type type;         // String - indicates whether a blog is public or private

        public Blog(
                String name,
                String url,
                String title,
                boolean primary,
                int followers,
                Tweet tweet,
                boolean facebook,
                Type type) {
            this.name = name;
            this.url = url;
            this.title = title;
            this.primary = primary;
            this.followers = followers;
            this.tweet = tweet;
            this.facebook = facebook;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public String getTitle() {
            return title;
        }

        public boolean isPrimary() {
            return primary;
        }

        public int getFollowers() {
            return followers;
        }

        public Tweet getTweet() {
            return tweet;
        }

        public boolean isFacebook() {
            return facebook;
        }

        public Type getType() {
            return type;
        }
    };

    class Data {
        private int following;                // Number - The number of blogs the user is following
        private PostFormat defaultPostFormat; // String - The default posting format - html, markdown, or raw
        private String name;                  // String - The user's tumblr short name
        private int likes;                    // Number - The total count of the user's likes
        private List<Blog> blogs;             // Array - Each item is a blog the user has permissions to post to

        public Data(int following, PostFormat defaultPostFormat, String name, int likes, List<Blog> blogs) {
            this.following = following;
            this.defaultPostFormat = defaultPostFormat;
            this.name = name;
            this.likes = likes;
            this.blogs = blogs;
        }

        public int getFollowing() {
            return following;
        }

        public PostFormat getDefaultPostFormat() {
            return defaultPostFormat;
        }

        public String getName() {
            return name;
        }

        public int getLikes() {
            return likes;
        }

        public List<Blog> getBlogs() {
            return blogs;
        }
    };

    class Api extends TumblrApi<Data> {

        /*
        "response": {
          "user": {
            "name": "paperogacoibentato",
            "likes": 8300,
            "following": 175,
            "default_post_format": "html",
            "blogs": [
              {
                "admin":true,
                "ask":true,
                "ask_anon":true,
                "ask_page_title":"Vediamo se la so",
                "avatar": [
                  {
                    "width": 512,
                    "height": 512,
                    "url": "https://66.media.tumblr.com/avatar_ed354109bd89_512.png"
                  },
                  {
                    "width": 128,
                    "height": 128,
                    "url": "https://66.media.tumblr.com/avatar_ed354109bd89_128.png"
                  },
                  {
                    "width": 96,
                    "height": 96,
                    "url": "https://66.media.tumblr.com/avatar_ed354109bd89_96.png"
                  },
                  {
                    "width": 64,
                    "height": 64,
                    "url": "https://66.media.tumblr.com/avatar_ed354109bd89_64.png"
                  }
                ],
                "can_chat": true,
                "can_send_fan_mail": true,
                "can_submit": true,
                "can_subscribe": false,
                "description": "Anche un papero sa arrampicarsi su un albero se viene adulato",
                "drafts": 0,
                "facebook": "N",
                "facebook_opengraph_enabled": "N",
                "followed": false,
                "followers": 261,
                "is_blocked_from_primary": false,
                "is_nsfw": false,
                "messages": 0,
                "name": "paperogacoibentato",
                "posts": 790,
                "primary": true,
                "queue": 0,
                "share_likes": false,
                "submission_page_title": "Ecco, bravo, damme na' mano",
                "submission_terms": {
                  "accepted_types":
                    [
                      "text",
                      "photo",
                      "quote",
                      "link",
                      "video"
                    ],
                    "tags": [
                    ],
                    "title": "Ecco, bravo, damme na' mano",
                    "guidelines": ""
                  },
                  "subscribed": false,
                  "theme":
                    {
                      "header_full_width": 396,
                      "header_full_height": 396,
                      "header_focus_width": 396,
                      "header_focus_height": 222,
                      "avatar_shape": "square",
                      "background_color": "#fffaf4",
                      "body_font": "Helvetica Neue",
                      "header_bounds": "20,396,242,0",
                      "header_image": "https://static.tumblr.com/9299a64d63c89e1caaf3eded41472a7e/1nhjmvr/irMpr5hpj/tumblr_static_-516496371-content.png",
                      "header_image_focused": "https://static.tumblr.com/e2f13b6f1d3f5b5bca60fa5db7c6ca8b/1nhjmvr/11Wpr5hpl/tumblr_static_tumblr_static_-516496371-content_focused_v3.png",
                      "header_image_scaled": "https://static.tumblr.com/9299a64d63c89e1caaf3eded41472a7e/1nhjmvr/irMpr5hpj/tumblr_static_-516496371-content_2048_v2.png",
                      "header_stretch": true,
                      "link_color": "#529ECC",
                      "show_avatar": true,
                      "show_description": true,
                      "show_header_image": true,
                      "show_title": true,
                      "title_color": "#444444",
                      "title_font": "Calluna",
                      "title_font_weight": "bold"
                    },
                  "title": "Paperoga Coibentato",
                  "total_posts": 790,
                  "tweet": "N",
                  "twitter_enabled": false,
                  "twitter_send": false,
                  "type": "public",
                  "updated": 1582472844,
                  "url": "https://paperogacoibentato.tumblr.com/",
                  "uuid": "t:4ZHKojAk25vVcuhziYcWLw"
                }
              ]
            }
          }
          */

        public Api(
                Context context,
                OAuthService service,
                Token authToken,
                String appId,
                String appVersion,
                String[] additionalArgs) {
            super(context, service, authToken, appId, appVersion);
        }

        @Override
        protected String getPath() {
            return "/user/info";
        }

        @Override
        protected Data readData(JSONObject jsonObject) throws JSONException {
            JSONObject userObj = jsonObject.getJSONObject("user");

            List<Blog> blogs = new ArrayList<Blog>();

            JSONArray blogsArray = userObj.getJSONArray("blogs");
            for (int idx = 0; idx < blogsArray.length(); ++idx) {
                JSONObject blog = blogsArray.getJSONObject(idx);

                String tweet = blog.getString("tweet");
                Tweet vTweet;

                String type = blog.getString("type");
                Type vType;

                if (type.equalsIgnoreCase("public")) {
                    vType = Type.Public;
                } else {
                    vType = Type.Private;
                }

                if (tweet.equalsIgnoreCase("auto")) {
                    vTweet = Tweet.Auto;
                } else if (tweet.equalsIgnoreCase("y")) {
                    vTweet = Tweet.Yes;
                } else {
                    vTweet = Tweet.No;
                }

                blogs.add(new Blog(
                        blog.getString("name"),
                        blog.getString("url"),
                        blog.getString("title"),
                        blog.getBoolean("primary"),
                        blog.getInt("followers"),
                        vTweet,
                        blog.getString("facebook").equalsIgnoreCase("y"),
                        vType
                ));
            }

            String postFormat = userObj.getString("default_post_format");
            PostFormat vPostFormat;

            if (postFormat.equalsIgnoreCase("html")) {
                vPostFormat = PostFormat.Html;
            } else if (postFormat.equalsIgnoreCase("markdown")) {
                vPostFormat = PostFormat.MarkDown;
            } else {
                vPostFormat = PostFormat.Raw;
            }

            return new Data(
                    userObj.getInt("following"),
                    vPostFormat,
                    userObj.getString("name"),
                    userObj.getInt("likes"),
                    blogs
            );
        }
    }
}

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

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BlogInfo {
    class Avatar {
    };

    class Data {
        private String title;                 // String - The display title of the blog
        private int posts;                    // Number - The total number of posts to this blog
        private String name;                  // String - The short blog name that appears before tumblr.com in a standard blog hostname
        private Date updated;                 // Number - The time of the most recent post, in seconds since the epoch
        private String description;           // String - You guessed it! The blog's description
        private boolean ask;                  // Boolean - Indicates whether the blog allows questions
        private boolean askAnon;              // Boolean - Indicates whether the blog allows anonymous questions; returned only if ask is true
        private List<Avatar> avatars;	      // Array - An array of avatar objects, each a different size, which should each have a width, height, and URL.

        public Data(
                String title,
                int posts,
                String name,
                Date updated,
                String description,
                boolean ask,
                boolean askAnon,
                List<Avatar> avatars) {
            this.title = title;
            this.posts = posts;
            this.name = name;
            this.updated = updated;
            this.description = description;
            this.ask = ask;
            this.askAnon = askAnon;
            this.avatars = avatars;
        }

        public String getTitle() {
            return title;
        }

        public int getPosts() {
            return posts;
        }

        public String getName() {
            return name;
        }

        public Date getUpdated() {
            return updated;
        }

        public String getDescription() {
            return description;
        }

        public boolean isAsk() {
            return ask;
        }

        public boolean isAskAnon() {
            return askAnon;
        }

        public List<Avatar> getAvatars() {
            return avatars;
        }
    };

    class Api extends TumblrApi<Data> {

        /*
        "response": {
          "blog":
            {
              "admin": true,
              "ask": true,
              "ask_anon": true,
              "ask_page_title": "Vediamo se la so",
              "avatar": [
                {
                  "width": 512,
                  "height": 512,
                  "url": "https:\/\/66.media.tumblr.com\/avatar_ed354109bd89_512.png"
                },
                {
                  "width": 128,
                  "height": 128,
                  "url": "https:\/\/66.media.tumblr.com\/avatar_ed354109bd89_128.png"
                },
                {
                  "width": 96,
                  "height": 96,
                  "url": "https:\/\/66.media.tumblr.com\/avatar_ed354109bd89_96.png"
                },
                {
                  "width": 64,
                  "height": 64,
                  "url": "https:\/\/66.media.tumblr.com\/avatar_ed354109bd89_64.png"
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
              "posts": 791,
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
                  "tags":[
                  ],
                  "title": "Ecco, bravo, damme na' mano",
                  "guidelines": ""
                },
              "subscribed": false,
              "theme": {
                "header_full_width": 396,
                "header_full_height": 396,
                "header_focus_width": 396,
                "header_focus_height": 222,
                "avatar_shape": "square",
                "background_color": "#fffaf4",
                "body_font": "Helvetica Neue",
                "header_bounds": "20,396,242,0",
                "header_image": "https:\/\/static.tumblr.com\/9299a64d63c89e1caaf3eded41472a7e\/1nhjmvr\/irMpr5hpj\/tumblr_static_-516496371-content.png",
                "header_image_focused": "https:\/\/static.tumblr.com\/e2f13b6f1d3f5b5bca60fa5db7c6ca8b\/1nhjmvr\/11Wpr5hpl\/tumblr_static_tumblr_static_-516496371-content_focused_v3.png",
                "header_image_scaled": "https:\/\/static.tumblr.com\/9299a64d63c89e1caaf3eded41472a7e\/1nhjmvr\/irMpr5hpj\/tumblr_static_-516496371-content_2048_v2.png",
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
              "total_posts": 791,
              "tweet": "N",
              "twitter_enabled": false,
              "twitter_send": false,
              "type": "public",
              "updated": 1582480880,
              "url": "https:\/\/paperogacoibentato.tumblr.com\/",
              "uuid": "t:4ZHKojAk25vVcuhziYcWLw",
              "is_optout_ads": true
            }
          }
        }
        */

        private String blogId;

        public Api(
                Context context,
                OAuthService service,
                Token authToken,
                String appId,
                String appVersion,
                String[] additionalArgs) {
            super(context, service, authToken, appId, appVersion);

            this.blogId = additionalArgs[0];
        }

        @Override
        protected String getPath() {
            return "/blog/" + blogId + ".tumblr.com/info";
        }

        @Override
        protected Map<String, String> defaultParams() {
            return new HashMap<String, String>(){{
                put("api_key", getContext().getString(R.string.consumer_key));
            }};
        }

        @Override
        protected Data readData(JSONObject jsonObject) throws JSONException {
            JSONObject blogObj = jsonObject.getJSONObject("blog");

            return new Data(
                blogObj.getString("title"),
                blogObj.getInt("posts"),
                blogObj.getString("name"),
                new Date(blogObj.getInt("updated") * 1000),
                blogObj.getString("description"),
                blogObj.getBoolean("ask"),
                blogObj.getBoolean("ask_anon"),
                new ArrayList<Avatar>()
            );
        }
    }
}

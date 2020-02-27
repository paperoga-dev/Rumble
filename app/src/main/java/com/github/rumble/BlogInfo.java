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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BlogInfo {

    class SubmissionTerms {
        public enum AcceptedTypes {
            Text,
            Photo,
            Quote,
            Link,
            Video
        }

        private Set<AcceptedTypes> acceptedTypes;
        private Set<String> tags;
        private String title;
        private String guidelines;

        /*
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
        */

        public SubmissionTerms(JSONObject jsonSubmissionTermsObject) throws JSONException {
            JSONArray acceptedTypes = jsonSubmissionTermsObject.getJSONArray("accepted_types");

            this.acceptedTypes = new HashSet<>();
            for (int i = 0; i < acceptedTypes.length(); ++i) {
                if (acceptedTypes.getString(i).equalsIgnoreCase("text"))
                    this.acceptedTypes.add(AcceptedTypes.Text);
                else if (acceptedTypes.getString(i).equalsIgnoreCase("photo"))
                    this.acceptedTypes.add(AcceptedTypes.Photo);
                else if (acceptedTypes.getString(i).equalsIgnoreCase("quote"))
                    this.acceptedTypes.add(AcceptedTypes.Quote);
                else if (acceptedTypes.getString(i).equalsIgnoreCase("link"))
                    this.acceptedTypes.add(AcceptedTypes.Link);
                else if (acceptedTypes.getString(i).equalsIgnoreCase("video"))
                    this.acceptedTypes.add(AcceptedTypes.Video);
            }

            JSONArray tags = jsonSubmissionTermsObject.getJSONArray("tags");
            this.tags = new HashSet<>();
            for (int i = 0; i < tags.length(); ++i)
                this.tags.add(tags.getString(i));

            this.title = jsonSubmissionTermsObject.getString("title");
            this.guidelines = jsonSubmissionTermsObject.getString("guidelines");
        }

        public Set<AcceptedTypes> getAcceptedTypes() {
            return acceptedTypes;
        }

        public Set<String> getTags() {
            return tags;
        }

        public String getTitle() {
            return title;
        }

        public String getGuidelines() {
            return guidelines;
        }
    }

    class Data {
        enum Type {
            Public,
            Private
        };

        enum Tweet {
            Auto,
            Yes,
            No
        };

        private boolean admin;                    // Boolean - is admin
        private boolean ask;                      // Boolean - Indicates whether the blog allows questions
        private boolean askAnon;                  // Boolean - Indicates whether the blog allows anonymous questions; returned only if ask is true
        private String askPageTitle;              // String - Ask page title
        private List<Avatar.Data> avatars;        // Array - List of available avatars
        private boolean canChat;                  // Boolean - Allows chat
        private boolean canSendFanMail;           // Boolean - ????
        private boolean canSubmit;                // Boolean - Allows submissions
        private boolean canSubscribe;             // Boolean - ????
        private String description;               // String - Blog description
        private int drafts;                       // Number - Drafts count
        private boolean facebook;                 // Boolean - Is to Facebook linked
        private boolean facebookOpengraphEnabled; // Boolean String - ????? (Y/N)
        private boolean followed;                 // Boolean - ?????
        private int followers;                    // Number - Followers count
        private boolean isBlockedFromPrimary;     // Boolean - ????
        private boolean isNFSW;                   // Boolean - NFSW blog
        private int messages;                     // Number - Messages count;
        private String name;                      // String - Blog name
        private int posts;                        // Number - Posts count
        private boolean primary;                  // Boolean - Is a primary blog
        private int queue;                        // Number - Queued posts count
        private boolean shareLikes;               // Boolean - ?????
        private String submissionPageTitle;       // String - Submission page title
        private SubmissionTerms submissionTerms;  // Submission Object => see above
        private boolean subscribed;               // Boolean - ?????
        // There is a theme object here, we skip it, it's useless for our purposes
        private String title;                     // String - Blog title
        private int totalPosts;                   // Number - Posts count
        private Tweet tweet;                      // String - indicate if posts are tweeted auto, Y, N
        private boolean twitterEnabled;           // Boolean - ?????
        private boolean twitterSend;              // Boolean - ????
        private Type type;                        // String - indicates whether a blog is public or private
        private Date updated;                     // Number - Last updated time (epoch)
        private String url;                       // String - Blog URL
        private String uuid;                      // Stirng - Blog UUID

        public Data(JSONObject blogObject) throws JSONException {
            this.admin = blogObject.getBoolean("admin");
            this.ask = blogObject.getBoolean("ask");
            this.askAnon = blogObject.getBoolean("ask_anon");
            this.askPageTitle = blogObject.getString("ask_page_title");

            JSONArray avatars = blogObject.getJSONArray("avatars");

            this.avatars = new ArrayList<>();
            for (int i = 0; i < avatars.length(); ++i) {
                this.avatars.add(new Avatar.Data(avatars.getJSONObject(i)));
            }

            this.canChat = blogObject.getBoolean("can_chat");
            this.canSendFanMail = blogObject.getBoolean("can_send_fan_mail");
            this.canSubmit = blogObject.getBoolean("can_submit");
            this.canSubscribe = blogObject.getBoolean("can_subscribe");
            this.description = blogObject.getString("description");
            this.drafts = blogObject.getInt("drafts");
            this.facebook = blogObject.getString("facebook").equalsIgnoreCase("Y");
            this.facebookOpengraphEnabled = blogObject.getString("facebook_opengraph_enabled").equalsIgnoreCase("Y");
            this.followed = blogObject.getBoolean("followed");
            this.followers = blogObject.getInt("followers");
            this.isBlockedFromPrimary = blogObject.getBoolean("is_blocked_from_primary");
            this.isNFSW = blogObject.getBoolean("is_nfsw");
            this.messages = blogObject.getInt("messages");
            this.name = blogObject.getString("name");
            this.posts = blogObject.getInt("posts");
            this.primary = blogObject.getBoolean("primary");
            this.queue = blogObject.getInt("queue");
            this.shareLikes = blogObject.getBoolean("share_likes");
            this.submissionPageTitle = blogObject.getString("submission_page_title");
            this.submissionTerms = new SubmissionTerms(blogObject.getJSONObject("submission_terms"));
            this.subscribed = blogObject.getBoolean("subscribed");
            this.title = blogObject.getString("title");
            this.totalPosts = blogObject.getInt("total_posts");

            String tweet = blogObject.getString("tweet");
            if (tweet.equalsIgnoreCase("Auto"))
                this.tweet = Tweet.Auto;
            else if (tweet.equalsIgnoreCase("Y"))
                this.tweet = Tweet.Yes;
            else
                this.tweet = Tweet.No;

            this.twitterEnabled = blogObject.getBoolean("twitter_enabled");
            this.twitterSend = blogObject.getBoolean("twitter_send");

            if (blogObject.getString("type").equalsIgnoreCase("public"))
                this.type = Type.Public;
            else
                this.type = Type.Private;

            this.updated = new Date(blogObject.getInt("updated") * 1000);
            this.url = blogObject.getString("url");
            this.uuid = blogObject.getString("uuid");
        }

        public boolean isAdmin() {
            return admin;
        }

        public boolean isAsk() {
            return ask;
        }

        public boolean isAskAnon() {
            return askAnon;
        }

        public String getAskPageTitle() {
            return askPageTitle;
        }

        public List<Avatar.Data> getAvatars() {
            return avatars;
        }

        public boolean isCanChat() {
            return canChat;
        }

        public boolean isCanSendFanMail() {
            return canSendFanMail;
        }

        public boolean isCanSubmit() {
            return canSubmit;
        }

        public boolean isCanSubscribe() {
            return canSubscribe;
        }

        public String getDescription() {
            return description;
        }

        public int getDrafts() {
            return drafts;
        }

        public boolean isFacebook() {
            return facebook;
        }

        public boolean isFacebookOpengraphEnabled() {
            return facebookOpengraphEnabled;
        }

        public boolean isFollowed() {
            return followed;
        }

        public int getFollowers() {
            return followers;
        }

        public boolean isBlockedFromPrimary() {
            return isBlockedFromPrimary;
        }

        public boolean isNFSW() {
            return isNFSW;
        }

        public int getMessages() {
            return messages;
        }

        public String getName() {
            return name;
        }

        public int getPosts() {
            return posts;
        }

        public boolean isPrimary() {
            return primary;
        }

        public int getQueue() {
            return queue;
        }

        public boolean isShareLikes() {
            return shareLikes;
        }

        public String getSubmissionPageTitle() {
            return submissionPageTitle;
        }

        public SubmissionTerms getSubmissionTerms() {
            return submissionTerms;
        }

        public boolean isSubscribed() {
            return subscribed;
        }

        public String getTitle() {
            return title;
        }

        public int getTotalPosts() {
            return totalPosts;
        }

        public Tweet getTweet() {
            return tweet;
        }

        public boolean isTwitterEnabled() {
            return twitterEnabled;
        }

        public boolean isTwitterSend() {
            return twitterSend;
        }

        public Type getType() {
            return type;
        }

        public Date getUpdated() {
            return updated;
        }

        public String getUrl() {
            return url;
        }

        public String getUuid() {
            return uuid;
        }
    };

    class Api extends TumblrBlogId<Data> {

        /*
        "response": {
          "blog":
            {
              "admin": true,
              "ask": true,
              "ask_anon": true,
              "ask_page_title": "Vediamo se la so",
              "avatar": [
                => Array of Avatars Object, see Avatar.Data
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
                => SubmissionTerms Object, see above
                },
              "subscribed": false,
              "theme": {
                => Theme data, we don't need it
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
            }
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
            super(context, service, authToken, appId, appVersion, additionalArgs);
        }

        @Override
        protected Data readData(JSONObject jsonObject) throws JSONException {
            return new Data(jsonObject.getJSONObject("blog"));
        }
    }
}

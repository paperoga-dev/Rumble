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
import java.util.List;
import java.util.Map;

public interface OldPosts {
    enum State {
        Published,
        Queued,
        Draft,
        Private
    }

    enum Format {
        Html,
        Markdown
    }

    abstract class Post {
        private String blogName;
        private BlogInfo.Base blog;
        private long id;
        private String url;
        private String slug;
        private Date timestamp;
        private State state;
        private Format format;
        private String reblogKey;
        private List<String> tags;
        private String shortUrl;
        private String summary;
        private boolean shouldOpenInLegacy;
        private boolean followed;
        private boolean liked;
        private int noteCount;
        private boolean canLike;
        private boolean canReblog;
        private boolean canSendInMessage;
        private boolean canReply;
        private boolean displayAvatar;

        public Post(JSONObject postObject) throws JSONException {
            super();

            this.blogName = postObject.getString("blog_name");
            this.blog = new BlogInfo.Base(postObject.getJSONObject("blog"));
            this.id = postObject.getLong("id");
            this.url = postObject.getString("post_url");
            this.slug = postObject.getString("slug");
            this.timestamp = new Date(postObject.getInt("timestamp") * 1000L);

            String state = postObject.getString("state");
            if (state.equalsIgnoreCase("queued"))
                this.state = State.Queued;
            else if (state.equalsIgnoreCase("draft"))
                this.state = State.Draft;
            else if (state.equalsIgnoreCase("private"))
                this.state = State.Private;
            else
                this.state = State.Published;

            String format = postObject.getString("format");
            if (format.equalsIgnoreCase("markdown"))
                this.format = Format.Markdown;
            else
                this.format = Format.Html;

            this.reblogKey = postObject.getString("reblog_key");

            this.tags = new ArrayList<>();
            JSONArray tags = postObject.getJSONArray("tags");
            for (int i = 0; i < tags.length(); ++i) {
                this.tags.add(tags.getString(i));
            }

            this.shortUrl = postObject.getString("short_url");
            this.summary = postObject.getString("summary");
            this.shouldOpenInLegacy = postObject.getBoolean("should_open_in_legacy");
            this.followed = postObject.getBoolean("followed");
            this.liked = postObject.getBoolean("liked");
            this.noteCount = postObject.getInt("note_count");
            this.canLike = postObject.getBoolean("can_like");
            this.canReblog = postObject.getBoolean("can_reblog");
            this.canSendInMessage = postObject.getBoolean("can_send_in_message");
            this.canReply = postObject.getBoolean("can_reply");
            this.displayAvatar = postObject.getBoolean("display_avatar");
        }

        public String getBlogName() {
            return blogName;
        }

        public BlogInfo.Base getBlog() {
            return blog;
        }

        public long getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }

        public String getSlug() {
            return slug;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public State getState() {
            return state;
        }

        public Format getFormat() {
            return format;
        }

        public String getReblogKey() {
            return reblogKey;
        }

        public List<String> getTags() {
            return tags;
        }

        public String getShortUrl() {
            return shortUrl;
        }

        public String getSummary() {
            return summary;
        }

        public boolean isShouldOpenInLegacy() {
            return shouldOpenInLegacy;
        }

        public boolean isFollowed() {
            return followed;
        }

        public boolean isLiked() {
            return liked;
        }

        public int getNoteCount() {
            return noteCount;
        }

        public boolean canLike() {
            return canLike;
        }

        public boolean canReblog() {
            return canReblog;
        }

        public boolean canSendInMessage() {
            return canSendInMessage;
        }

        public boolean canReply() {
            return canReply;
        }

        public boolean isDisplayAvatar() {
            return displayAvatar;
        }
    }

    class Text extends Post {

        // text only
        // title -> string, can be null
        // body -> string

        private String title;
        private String body;

        public Text(JSONObject postObject) throws JSONException {
            super(postObject);

            this.title = (postObject.has("title")? postObject.getString("title") : "");
            this.title = postObject.getString("body");
        }

        public String getTitle() {
            return title;
        }

        public String getBody() {
            return body;
        }
    }

    class Photo extends Post {

        // photo only
        // image_permalink -> string
        // photos -> array of objects
        //   -> caption -> string
        //   -> original_size -> object
        //   -> -> url -> string
        //   -> -> width -> number
        //   -> -> height -> number
        //   -> alt_sizes -> array of objects
        //   -> -> url -> string
        //   -> -> width -> number
        //   -> -> height -> number

        public Photo(JSONObject postObject) throws JSONException {
            super(postObject);
        }
    }

    class Answer extends Post {

        // answer only
        // asking_name -> string
        // asking_url -> object, can be null (anonymous?)
        // question -> string
        // answer -> string

        public Answer(JSONObject postObject) throws JSONException {
            super(postObject);
        }
    }

    class Link extends Post {

        // link only
        // title -> string
        // url -> string
        // link_image -> url
        // link_image_dimensions -> object
        // -> width -> number
        // -> height -> number
        // link_author -> object
        // excerpt -> string
        // publisher -> string
        // photos -> array of objects
        //  -> image object (see above)
        // description -> string

        public Link(JSONObject postObject) throws JSONException {
            super(postObject);
        }
    }

    class Video extends Post {

        // video only
        // source_url -> string
        // source_title -> string
        // caption -> string
        // video_url -> string
        // html5_capable -> boolean
        // thumbnail_url -> string
        // thumbnail_width -> number
        // thumbnail_height -> number
        // duration -> number
        // player -> object
        // -> width -> number
        // -> embed_code -> string
        // video_type -> string (unknown values, "tumblr")

        public Video(JSONObject postObject) throws JSONException {
            super(postObject);
        }
    }

    class Audio extends Post {

        // audio only
        // source_url -> stirng
        // source_title -> string
        // track_name -> string
        // album_art -> string
        // caption -> string
        // player -> string
        // embed -> string
        // plays -> number
        // audio_url -> string
        // audio_source_url -> string
        // is_external -> boolean
        // audio_type -> string ("soundcloud", unknown values)

        public Audio(JSONObject postObject) throws JSONException {
            super(postObject);
        }
    }

    class Quote extends Post {

        // quote only
        // text -> string
        // source -> string

        public Quote(JSONObject postObject) throws JSONException {
            super(postObject);
        }
    }

    class Chat extends Post {

        // chat only
        // title -> string (can be null)
        // body -> string
        // dialogue -> array of objects
        // -> name -> string
        // -> label -> string
        // -> phrase -> string

        public Chat(JSONObject postObject) throws JSONException {
            super(postObject);
        }
    }

    class Data implements TumblrArrayItem<Post> {
        private List<Post> posts;
        private int totalPosts;

        public Data(JSONObject postsObject) throws JSONException {
            super();

            this.totalPosts = postsObject.getInt("total_posts");
            this.posts = new ArrayList<>();

            JSONArray posts = postsObject.getJSONArray("posts");
            for (int i = 0; i < posts.length(); ++i) {
                JSONObject post = posts.getJSONObject(i);

                String type = post.getString("type");

                if (type.equalsIgnoreCase("quote"))
                    this.posts.add(new Quote(post));
                else if (type.equalsIgnoreCase("link"))
                    this.posts.add(new Link(post));
                else if (type.equalsIgnoreCase("answer"))
                    this.posts.add(new Answer(post));
                else if (type.equalsIgnoreCase("video"))
                    this.posts.add(new Video(post));
                else if (type.equalsIgnoreCase("audio"))
                    this.posts.add(new Audio(post));
                else if (type.equalsIgnoreCase("photo"))
                    this.posts.add(new Photo(post));
                else if (type.equalsIgnoreCase("chat"))
                    this.posts.add(new Chat(post));
                else
                    this.posts.add(new Text(post));
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

        /*
        {
          "type": "text",
          "blog_name": "paperogacoibentato",
          "blog": {
            "name": "paperogacoibentato",
            "title": "Paperoga Coibentato",
            "description": "Anche un papero sa arrampicarsi su un albero se viene adulato",
            "url": "https://paperogacoibentato.tumblr.com/",
            "uuid": "t:4ZHKojAk25vVcuhziYcWLw",
            "updated": 1583512064
          },
          "id": 611577585127784448,
          "post_url": "https://paperogacoibentato.tumblr.com/post/611577585127784448/dopo-settimane-di-tentativi-andati-a-vuoto",
          "slug": "dopo-settimane-di-tentativi-andati-a-vuoto",
          "date": "2020-03-03 14:30:35 GMT",
          "timestamp": 1583245835,
          "state": "published",
          "format": "html",
          "reblog_key": "CPz5wM9v",
          "tags": [
            "io",
            "diario",
            "non adoro vantarmi",
            "ma lo sto facendo",
            "e pure bene"
          ],
          "short_url": "https://tmblr.co/Zz2sqWXymfBA8W00",
          "summary": "Dopo settimane di tentativi andati a vuoto, fallimenti, ripensamenti, idee cadute nel nulla, esperimenti finiti in esplosioni...",
          "should_open_in_legacy": false,
          "followed": false,
          "liked": false,
          "note_count": 27,
          "title": null,
          "body": "<p>Dopo settimane di tentativi andati a vuoto, ...",
          "reblog": {
            "comment": "<p><p>Dopo settimane di tentativi andati a vuoto...",
            "tree_html": ""
          },
          "trail": [
            {
              "blog": {
                "name": "paperogacoibentato",
                "active": true,
                "theme": {
                  ...
                },
                "share_likes": false,
                "share_following": false,
                "can_be_followed": false
              },
              "post": {
                "id": "611577585127784448"
              },
              "content_raw": "<p><p>Dopo settimane di tentativi ...",
              "content": "<p><p>Dopo settimane di tentativi ...",
              "is_current_item": true,
              "is_root_item": true
            }
          ],
          "can_like": true,
          "can_reblog": true,
          "can_send_in_message": true,
          "can_reply": true,
          "display_avatar": true
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
        protected String getPath() {
            return super.getPath() + "/posts";
        }

        @Override
        protected Map<String, String> defaultParams() {
            Map<String, String> m = super.defaultParams();

            /*
            blog-identifier String Any blog identifier
             */
            m.put("blog_identifier", getBlogId());
            m.put("npf", "true");

            return m;
        }


        @Override
        protected Data readData(JSONObject jsonObject) throws JSONException {
            return new Data(jsonObject);
        }
    }
}

package com.github.rumble;

import android.app.Application;
import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Posts {

    abstract class Media {
        private String url;
        private String mimeType;
        private boolean originalDimensionsMissing;
        private boolean cropped;
        private boolean hasOriginalDimensions;

        public Media(JSONObject mediaObject) throws JSONException {
            super();

            this.url = mediaObject.getString("url");
            this.mimeType = mediaObject.optString("type", "");
            this.originalDimensionsMissing = mediaObject.optBoolean(
                    "original_dimensions_missing",
                    false
            );
            this.cropped = mediaObject.optBoolean("cropped", false);
            this.hasOriginalDimensions = mediaObject.optBoolean(
                    "has_original_dimensions",
                    false
            );
        }

        public String getUrl() {
            return url;
        }

        public String getMimeType() {
            return mimeType;
        }

        public boolean areOriginalDimensionsMissing() {
            return originalDimensionsMissing;
        }

        public boolean isCropped() {
            return cropped;
        }

        public boolean hasOriginalDimensions() {
            return hasOriginalDimensions;
        }
    }

    class Image extends Media {
        private int width;
        private int height;

        public Image(JSONObject mediaObject) throws JSONException {
            super(mediaObject);

            this.width = mediaObject.optInt("width", 0);
            this.height = mediaObject.optInt("height", 0);
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    abstract class ContentItem {

    }

    abstract class FormattingItem {
        private int start;
        private int end;

        public FormattingItem(JSONObject formattingObject) throws JSONException {
            super();

            this.start = formattingObject.getInt("start");
            this.end = formattingObject.getInt("end");
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }

    class Bold extends FormattingItem {
        public Bold(JSONObject formattingObject) throws JSONException {
            super(formattingObject);
        }
    }

    class Italic extends FormattingItem {
        public Italic(JSONObject formattingObject) throws JSONException {
            super(formattingObject);
        }
    }

    class Strikethrough extends FormattingItem {
        public Strikethrough(JSONObject formattingObject) throws JSONException {
            super(formattingObject);
        }
    }

    class Link extends FormattingItem {
        private String url;

        public Link(JSONObject formattingObject) throws JSONException {
            super(formattingObject);

            this.url = formattingObject.getString("url");
        }

        public String getUrl() {
            return url;
        }
    }

    class Mention extends FormattingItem {
        private BlogInfo.Reference blog;

        public Mention(JSONObject formattingObject) throws JSONException {
            super(formattingObject);

            this.blog = new BlogInfo.Reference(formattingObject.getJSONObject("blog"));
        }

        public BlogInfo.Reference getBlog() {
            return blog;
        }
    }

    class Color extends FormattingItem {
        private int color;

        public Color(JSONObject formattingObject) throws JSONException {
            super(formattingObject);

            this.color = android.graphics.Color.parseColor(formattingObject.getString("color"));
        }

        public int getColor() {
            return color;
        }
    }

    class Text extends ContentItem {
        private String text;
        private List<FormattingItem> formattingItems;

        private static final Map<String, Class<? extends FormattingItem>> typesMap =
                new HashMap<String, Class<? extends FormattingItem>>(){{
                    put("bold", Bold.class);
                    put("italic", Italic.class);
                    put("strikethrough", Strikethrough.class);
                    put("link", Link.class);
                    put("mention", Mention.class);
                    put("color", Color.class);
                }};

        public Text(JSONObject textObject) throws JSONException, RuntimeException {
            super();

            this.text = textObject.getString("text");

            this.formattingItems = new ArrayList<>();
            JSONArray formattingItems = textObject.optJSONArray("formatting");
            for (int i = 0; i < formattingItems.length(); ++i) {
                JSONObject formattingItem = formattingItems.getJSONObject(i);
                String type = formattingItem.getString("type");
                try {
                    this.formattingItems.add(
                            typesMap.get(type)
                                    .getDeclaredConstructor(JSONObject.class)
                                    .newInstance(formattingItem)
                    );
                } catch (InstantiationException |
                        InvocationTargetException |
                        NoSuchMethodException |
                        IllegalAccessException e) {
                    throw new RuntimeException("Add missing formatting type: " + type);
                }
            }
        }

        public String getText() {
            return text;
        }
    }

    class Heading1 extends Text {
        public Heading1(JSONObject textObject) throws JSONException {
            super(textObject);
        }
    }

    class Heading2 extends Text {
        public Heading2(JSONObject textObject) throws JSONException {
            super(textObject);
        }
    }

    class Quirky extends Text {
        public Quirky(JSONObject textObject) throws JSONException {
            super(textObject);
        }
    }

    class Quote extends Text {
        public Quote(JSONObject textObject) throws JSONException {
            super(textObject);
        }
    }

    class Indented extends Text {
        public Indented(JSONObject textObject) throws JSONException {
            super(textObject);
        }
    }

    class Chat extends Text {
        public Chat(JSONObject textObject) throws JSONException {
            super(textObject);
        }
    }

    class OrderedListItem extends Text {
        public OrderedListItem(JSONObject textObject) throws JSONException {
            super(textObject);
        }
    }

    class UnorderedListItem extends Text {
        public UnorderedListItem(JSONObject textObject) throws JSONException {
            super(textObject);
        }
    }

    abstract class Post {
        private long id;
        private BlogInfo.Base blog;
        private List<ContentItem> content;

        public Post(JSONObject postObject) throws JSONException {
            super();

            this.id = postObject.getLong("id");
            this.blog = new BlogInfo.Data(postObject);

            this.content = new ArrayList<>();
            JSONArray content = postObject.getJSONArray("content");
            for (int i = 0; i < content.length(); ++i) {
                JSONObject contentItem = content.getJSONObject(i);

                String contentType = contentItem.getString("type");
                if (contentType.equalsIgnoreCase("text")) {
                    this.content.add(new Text(contentItem));
                }
            }
        }
    }
}

![Android CI](https://github.com/paperoga-dev/Rumble/workflows/Android%20CI/badge.svg)

# Rumble
Tumblr App for Android devices

## Start developing
Just clone the project and open it with Android Studio.

In order to build and run Rumble, you need developer keys. Please contact the maintainers in order to get them (preferred way), or create your own [here](https://www.tumblr.com/oauth/apps).

Then create a *tokens.xml* file in the *app/src/main/res/values* folder, with the following content:

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="consumer_key">consumer_key_here</string>
    <string name="consumer_secret">consumer_secret_here</string>
</resources>
```

If classes are not found, but the project builds fine anyway, please trigger a ```File => Invalidate Caches / Restart ...``` action, in order to purge your current cache and reload the Java package.

## Maintainers

Please contact on Tumblr, via message, one of the following developers:

- paperogacoibentato

or

- join the *Rumble The App* Tumblr Chat

or

- visit the [*rumble-the-app*](https://rumble-the-app.tumblr.com) Tumblr Blog

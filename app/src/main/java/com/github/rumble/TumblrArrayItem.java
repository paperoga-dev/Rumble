package com.github.rumble;

import java.util.List;

public interface TumblrArrayItem<T> {
    int getCount();
    List<T> getItems();
}

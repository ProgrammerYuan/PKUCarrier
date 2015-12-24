package com.handmark.pulltorefresh.library.internal;

import android.widget.ScrollView;

/**
 * Created by Michael on 2014/10/14.
 */
public interface OnScrollChangedListener {
    void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt);
}

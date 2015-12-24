/**
 *
 */
package com.programmeryuan.PKUCarrier.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Michael
 */
public class AngelFragment extends Fragment {
    protected View cache = null;

    /**
     * 初始化方法，可以使用缓存，使得此Fragment在ViewPager中不会因为划出屏幕而重新加载
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @param layout
     * @return
     */
    public boolean onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int layout) {
        boolean use_cache = (cache != null);
        if (!use_cache) {
            cache = inflater.inflate(layout, null);
        }

        ViewGroup parent = (ViewGroup) cache.getParent();
        if (parent != null) {
            parent.removeView(cache);
        }
        return use_cache;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}

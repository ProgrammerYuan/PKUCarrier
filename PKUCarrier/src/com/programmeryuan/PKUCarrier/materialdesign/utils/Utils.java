package com.programmeryuan.PKUCarrier.materialdesign.utils;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;

public class Utils {


    /**
     * Convert Dp to Pixel
     */
    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    public static int getRelativeTop(View myView) {
//	    if (myView.getParent() == myView.getRootView())
        if (myView.getId() == android.R.id.content)
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }

    public static int getRelativeLeft(View myView) {
//	    if (myView.getParent() == myView.getRootView())
        if (myView.getId() == android.R.id.content)
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    public static double getRippleSize(int f, int fn, double max) {
        if (f > fn / 2) {
            return max;
        }
//        二次模型
//        double a = -max / (fn * fn);
//        double b = max / fn;
//        double result = a * f * f + b * f;
//        对数模型(没用...)
//        double a = Math.pow(fn / 2.0, max);
//        double result = Math.log(max) / Math.log(a);
//三角模型
        double a = Math.PI / fn;
        double result = max * Math.sin(a * f);
//        Logger.out("getRippleSize  frame:" + f + " size:" + result);
        return result;
    }

    public static int getBgColor(int f, int fn, int c0, int cn) {
        int a0 = Color.alpha(c0);
        int r0 = Color.red(c0);
        int g0 = Color.green(c0);
        int b0 = Color.blue(c0);
        int an = Color.alpha(cn);
        int rn = Color.red(cn);
        int gn = Color.green(cn);
        int bn = Color.blue(cn);
        int result = Color.argb(getColor(f, fn, a0, an), getColor(f, fn, r0, rn), getColor(f, fn, g0, gn), getColor(f, fn, b0, bn));
//        Logger.out("getBgColor  frame:" + f + " color:" + Integer.toHexString(result));
        return result;
    }

    static int getColor(int f, int fn, int c0, int cn) {
        //二次模型
//        double a = (c0 - cn) * 4.0 / (fn * fn);
//        double b = (cn - c0) * 4.0 / fn;
//        int result = (int) (a * (f * f) + b * f + c0);
        double a = cn - c0;
        double b = Math.PI / fn;
        double c = c0;
        double result = a * Math.sin(b * f) + c;
//        Logger.out("getColor  frame:" + f + " result:" + result);
        return (int) result;
    }
}

package com.programmeryuan.PKUCarrier.materialdesign.widgets;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import com.programmeryuan.PKUCarrier.materialdesign.utils.Utils;
import com.programmeryuan.PKUCarrier.utils.Logger;
import com.programmeryuan.PKUCarrier.utils.Util;

public class CustomLinearLayout extends android.widget.LinearLayout {


    final static String MATERIALDESIGNXML = "http://schemas.android.com/apk/res-auto";
    final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";
    boolean fade_started = false;
    boolean draw_effect = false;
    int rippleSize = 3;
    final int disabledBackgroundColor = Color.parseColor("#E2E2E2");
    int beforeBackground;
    int backgroundColor = Color.parseColor("#FFFFFF");
    int fade_alpha = 255;
    int duration_ripple;
    int duration_alpha;
    float frames_ripple;
    float frames_alpha;
    int frame_max;
    int frame;
    float ripple_speed = 20f;
    int fade_speed = 15;
    float max_distance = 0;
    float x = -1, y = -1;
    float radius = -1;
    int duration = 350;
    int pressed_color = 0;
    protected OnClickListener listener;
    Rect src, dst;
    GradientDrawable shape;
    Bitmap canvas_bitmap;
    float threshold = 0.8f;
    // Indicate if user touched this view the last time
    public boolean isLastTouch = false;
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    void setMaxdistance() {
        Point p0 = new Point((int) x, (int) y);
        Point p1 = new Point(0, 0);
        Point p2 = new Point(getMeasuredWidth(), 0);
        Point p3 = new Point(getMeasuredWidth(), getMeasuredHeight());
        Point p4 = new Point(0, getMeasuredHeight());
        max_distance = Util.getDistance(p0, p1);
        max_distance = Math.max(max_distance, Util.getDistance(p0, p2));
        max_distance = Math.max(max_distance, Util.getDistance(p0, p3));
        max_distance = Math.max(max_distance, Util.getDistance(p0, p4));
    }

    public CustomLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);


    }

    //    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return super.onInterceptTouchEvent(ev);
////        return true;
//    }
//    @Override
//    public void setEnabled(boolean enabled) {
//        super.setEnabled(enabled);
//        if (enabled)
//            setBackgroundColor(beforeBackground);
//        else
//            setBackgroundColor(disabledBackgroundColor);
//
//    }

    @Override
    public boolean performClick() {
//        Logger.out("CustomLinearLayout performClick");
        draw_effect = true;
        radius++;
        invalidate();
//        return super.performClick();
        return false;
    }

    public Bitmap makeBitmap() {
        Bitmap output = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);
        radius = (float) Utils.getRippleSize(frame, frame_max, Util.getDistance(new Point(0, 0), new Point(getWidth(), getHeight())) / 3);
        int color = Utils.getBgColor(frame, frame_max, this.backgroundColor, pressed_color);
        if((frame * 1.0f) / frame_max > threshold){
            threshold = 1.01f;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    System.gc();
                    Logger.out("CustomLinearLayout#" + getTag() + ": listener triggered. listener " + (listener == null ? "is null" : "is not null"));
//                    Logger.out("listener " + (listener == null ? "is null" : "is not null"));
                    if (listener != null)
                        listener.onClick(CustomLinearLayout.this);
                }
            });
        }
        if (frame == frame_max) {
            threshold = 0.8f;
            draw_effect = false;
            frame = 0;

//            System.gc();
////            Logger.out("makeBitmap gc！！！");
//            if (onClickListener != null)
//                onClickListener.onClick(this);
        } else {
            frame++;
            canvas.drawColor(color);

            if (frame < frame_max / 2) {
                canvas.drawCircle(x, y, radius, paint);
            }
            if (canvas_bitmap == null) {
                setCanvas(shape);
            }
            if (canvas_bitmap != null) {
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                canvas.drawBitmap(canvas_bitmap, 0, 0, paint);
                paint.setXfermode(null);
            }
        }

//
//        if (fade_started) {
//            canvas.drawARGB(fade_alpha, Color.red(pressed_color), Color.green(pressed_color), Color.blue(pressed_color));
//            fade_alpha -= fade_speed;
//            if (fade_alpha <= 0) {
//                fade_started = false;
//                draw_effect = false;
//                if (onClickListener != null)
//                    onClickListener.onClick(this);
//            }
//        } else {
//            if (x != -1) {
//                canvas.drawCircle(x, y, radius, paint);
//                if (radius > getHeight() / rippleSize)
//                    radius += ripple_speed;
//                if (radius >= max_distance) {
//                    x = -1;
//                    y = -1;
//                    radius = getHeight() / rippleSize;
//                    fade_started = true;
//                    fade_alpha = Color.alpha(pressed_color);
//                }
//            }
//
//        }
        return output;
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        this.listener = onClickListener;
//        Logger.out("CustomLinearLayout#" + this.getTag() + " setOnClickListener");
    }

    protected void setCanvas(Drawable canvas_drawable) {
        if (canvas_drawable != null) {
            Rect rect = canvas_drawable.getBounds();
            canvas_bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            canvas_drawable.setBounds(rect.left, rect.top, rect.right, rect.bottom);
            Canvas canvas_mask = new Canvas(canvas_bitmap);
            canvas_drawable.draw(canvas_mask);
        }
    }

    //    public Bitmap makeBitmap() {
//        Bitmap output = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(output);
//        canvas.drawARGB(0, 0, 0, 0);
//        if (fade_started) {
//            canvas.drawARGB(fade_alpha, Color.red(pressed_color), Color.green(pressed_color), Color.blue(pressed_color));
//            fade_alpha -= fade_speed;
//            if (fade_alpha <= 0) {
//                fade_started = false;
//                draw_effect = false;
//                if (onClickListener != null)
//                    onClickListener.onClick(this);
//            }
//        } else {
//            if (x != -1) {
//                canvas.drawCircle(x, y, radius, paint);
//                if (radius > getHeight() / rippleSize)
//                    radius += ripple_speed;
//                if (radius >= max_distance) {
//                    x = -1;
//                    y = -1;
//                    radius = getHeight() / rippleSize;
//                    fade_started = true;
//                    fade_alpha = Color.alpha(pressed_color);
//                }
//            }
//
//        }
//        return output;
//    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMaxdistance();
        src = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
        dst = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());

        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        float fps = display.getRefreshRate();

        duration_ripple = (int) (duration * 2.0 / 3);
        duration_alpha = (int) (duration * 1.0 / 3);
        frames_ripple = (float) (fps * duration_ripple / 1000.0);
        frames_alpha = (float) (fps * duration_alpha / 1000.0);
        frame_max = (int) (fps * duration / 1000.0);
        ripple_speed = max_distance / frames_ripple;
        if (pressed_color == 0) {
            pressed_color = makePressColor();
        }
        fade_alpha = Color.alpha(pressed_color);
        fade_speed = (int) (fade_alpha / frames_alpha);
        paint.setAntiAlias(true);
        paint.setColor(pressed_color);
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        pressed_color = makePressColor();
        paint.setColor(pressed_color);
    }

    /**
     * Make a dark color to ripple effect
     *
     * @return
     */
    public int makePressColor() {
        return makePressColor(backgroundColor);
//        int r = Color.red(backgroundColor);
//        int g = Color.green(backgroundColor);
//        int b = Color.blue(backgroundColor);
//        r = (int) (r * 7 / 8.0);
//        g = (int) (g * 7 / 8.0);
//        b = (int) (b * 7 / 8.0);
//        return Color.rgb(r, g, b);
    }

    public int makePressColor(int color) {
        pressed_color = Util.createDarkerColor(color);
//        int a = Color.alpha(color);
//        int r = Color.red(color);
//        int g = Color.green(color);
//        int b = Color.blue(color);
//        if (r + g + b == 0) {
//            pressed_color = Color.argb(Math.min(a + 15, 255), 0, 0, 0);
//            return pressed_color;
//        }
////        a = (int) (a * 7 / 8.0);
//        r = (int) (r * 7 / 8.0);
//        g = (int) (g * 7 / 8.0);
//        b = (int) (b * 7 / 8.0);
//        pressed_color = Color.rgb(r, g, b);
        paint.setColor(pressed_color);
        return pressed_color;
    }
//    protected int makePressColor() {
//        int r = (this.backgroundColor >> 16) & 0xFF;
//        int g = (this.backgroundColor >> 8) & 0xFF;
//        int b = (this.backgroundColor >> 0) & 0xFF;
//        r = (r - 30 < 0) ? 0 : r - 30;
//        g = (g - 30 < 0) ? 0 : g - 30;
//        b = (b - 30 < 0) ? 0 : b - 30;
//        return Color.rgb(r, g, b);
//    }
}

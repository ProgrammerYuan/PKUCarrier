package com.programmeryuan.PKUCarrier.materialdesign.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class RelativeLayout extends CustomRelativeLayout {

    int background;
//    int rippleSize = 3;

//    OnClickListener onClickListener;
    //    int backgroundColor = Color.parseColor("#FFFFFF");
//    boolean fade_started = false;
//    boolean draw_effect = false;
    Integer rippleColor;
    Float xRippleOrigin;
    Float yRippleOrigin;
//    float max_distance = 0;

    public RelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
    }

    // Set atributtes of XML to View
    protected void setAttributes(AttributeSet attrs) {

        // Set background Color
        // Color by resource
        int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML,
                "background", -1);
        if (bacgroundColor != -1) {
            setBackgroundColor(getResources().getColor(bacgroundColor));
        } else {
            // Color by hexadecimal
            background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
            if (background != -1)
                setBackgroundColor(background);
            else
                setBackgroundColor(this.backgroundColor);
        }
        // Set Ripple Color
        // Color by resource
        int rippleColor = attrs.getAttributeResourceValue(MATERIALDESIGNXML,
                "rippleColor", -1);
        if (rippleColor != -1) {
            setRippleColor(getResources().getColor(rippleColor));
        } else {
            // Color by hexadecimal
            int background = attrs.getAttributeIntValue(MATERIALDESIGNXML, "rippleColor", -1);
            if (background != -1)
                setRippleColor(background);
            else
                setRippleColor(makePressColor());
        }

//        rippleSpeed = attrs.getAttributeFloatValue(MATERIALDESIGNXML,
//                "rippleSpeed", 20f);

    }


    // Set color of background
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        if (isEnabled())
            beforeBackground = backgroundColor;
        super.setBackgroundColor(color);
    }

    public void setRippleSpeed(int rippleSpeed) {
        this.ripple_speed = rippleSpeed;
    }

    // ### RIPPLE EFFECT ###

//    float x = -1, y = -1;
//    float radius = -1;

//    @Override
//    public boolean performClick() {
//        draw_effect = true;
//        radius++;
//        invalidate();
//        return super.performClick();
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isEnabled()) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if ((event.getX() <= getWidth() && event.getX() >= 0)
                        && (event.getY() <= getHeight() && event.getY() >= 0)) {
                    radius = getHeight() / rippleSize;
                    x = event.getX();
                    y = event.getY();
                    setMaxdistance();
                    ripple_speed = max_distance / frames_ripple;
                    return performClick();
                } else {
                    isLastTouch = false;
                    x = -1;
                    y = -1;
                }

            }
        }
        return true;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus,direction,previouslyFocusedRect);
        if (!gainFocus) {
            x = -1;
            y = -1;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
//        return true;
    }

//    public Bitmap makeBitmap() {
//        Bitmap output = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
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

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (draw_effect) {

//            Rect src = new Rect(0, 0, getWidth(), getHeight());
//            Rect dst = new Rect(0, 0, getWidth(), getHeight());
            Bitmap bitmap = makeBitmap();
            canvas.drawBitmap(bitmap, src, dst, null);
//            bitmap.recycle();
//            bitmap = null;

            invalidate();
        }
    }


    @Override
    public void setOnClickListener(OnClickListener l) {
        listener = l;
    }

    public void setRippleColor(int rippleColor) {
        this.rippleColor = rippleColor;
    }

//    public void setxRippleOrigin(Float xRippleOrigin) {
//        this.xRippleOrigin = xRippleOrigin;
//    }
//
//    public void setyRippleOrigin(Float yRippleOrigin) {
//        this.yRippleOrigin = yRippleOrigin;
//    }
}

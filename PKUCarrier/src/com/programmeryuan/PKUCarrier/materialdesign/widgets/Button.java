package com.programmeryuan.PKUCarrier.materialdesign.widgets;

import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.materialdesign.utils.Utils;
import com.programmeryuan.PKUCarrier.utils.Util;

public abstract class Button extends CustomRelativeLayout {

    final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";

    int minWidth;
    int minHeight;
    int background;

    Integer rippleColor;

//    int backgroundColor = Color.parseColor("#1E88E5");

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaultProperties();
        setAttributes(attrs);
        beforeBackground = backgroundColor;
        if (rippleColor == null)
            rippleColor = makePressColor();
    }

    protected void setDefaultProperties() {
        backgroundColor = Color.parseColor("#1E88E5");
        // Min size
//        setMinimumHeight(Utils.dpToPx(minHeight, getResources()));
//        setMinimumWidth(Utils.dpToPx(minWidth, getResources()));
        // Background shape
        setBackgroundResource(background);
        setBackgroundColor(backgroundColor);
    }

    // Set atributtes of XML to View
    protected void setAttributes(AttributeSet attrs) {
        setMinSize(attrs);
    }

    // ### RIPPLE EFFECT ###
    protected void setPaddingFor(View v, AttributeSet attrs) {
//        String padding_text = attrs.getAttributeValue(ANDROIDXML, "padding");
        float padding = fixValue(attrs.getAttributeValue(ANDROIDXML, "padding"));
//        int padding = attrs.getAttributeIntValue(ANDROIDXML, "padding", -1);
        float paddingLeft;
        float paddingTop;
        float paddingRight;
        float paddingBottom;
        if (padding == -1) {

            paddingLeft = fixValue(attrs.getAttributeValue(ANDROIDXML, "paddingLeft"));
            paddingTop = fixValue(attrs.getAttributeValue(ANDROIDXML, "paddingTop"));
            paddingRight = fixValue(attrs.getAttributeValue(ANDROIDXML, "paddingRight"));
            paddingBottom = fixValue(attrs.getAttributeValue(ANDROIDXML, "paddingBottom"));
            paddingLeft = (paddingLeft == -1) ? 16 : paddingLeft;
            paddingTop = (paddingTop == -1) ? 2 : paddingTop;
            paddingRight = (paddingRight == -1) ? 16 : paddingRight;
            paddingBottom = (paddingBottom == -1) ? 2 : paddingBottom;
        } else {
            paddingLeft = paddingTop = paddingRight = paddingBottom = padding;
        }
        v.setPadding(Util.getPX(paddingLeft), Util.getPX(paddingTop), Util.getPX(paddingRight), Util.getPX(paddingBottom));

    }

    protected void setMinSize(AttributeSet attrs) {
        int width = (int) fixValue(attrs.getAttributeValue(ANDROIDXML, "minWidth"));
        int height = (int) fixValue(attrs.getAttributeValue(ANDROIDXML, "minHeight"));

        setMinimumWidth(Util.getPX(width == -1 ? minWidth : width));
        setMinimumHeight(Util.getPX(height == -1 ? minHeight : height));
    }

    float fixValue(String value_with_unit) {
        if (value_with_unit == null) {
            return -1;
        }
        float value = -1;
        value_with_unit = value_with_unit.replace("dp", "");
        value_with_unit = value_with_unit.replace("dip", "");
        value_with_unit = value_with_unit.replace("sp", "");
        try {
            value = Float.parseFloat(value_with_unit);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return value;
    }
//    float x = -1, y = -1;
//    float radius = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.i("onTouchEvent",event.getAction()+"");
        if (isEnabled()) {
            isLastTouch = true;
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
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (!gainFocus) {
            x = -1;
            y = -1;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
//        onTouchEvent(ev);
//        return false;
    }

    public Bitmap makeCircle() {
        Bitmap output = Bitmap.createBitmap(
                getWidth() - Utils.dpToPx(6, getResources()), getHeight()
                        - Utils.dpToPx(7, getResources()), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(rippleColor);
        canvas.drawCircle(x, y, radius, paint);
        if (radius > getHeight() / rippleSize)
            radius += ripple_speed;
        if (radius >= getWidth()) {
            x = -1;
            y = -1;
            radius = getHeight() / rippleSize;
            if (listener != null)
                listener.onClick(this);
        }


        return output;
    }


    @Override
    public void setOnClickListener(View.OnClickListener l) {
        listener = l;
    }

    // Set color of background
    public void setBackgroundColor(int color) {

        this.backgroundColor = color;
        pressed_color = makePressColor();
        paint.setColor(pressed_color);
        if (isEnabled())
            beforeBackground = backgroundColor;
        try {
            LayerDrawable layer = (LayerDrawable) getBackground();
            shape = (GradientDrawable) layer
                    .findDrawableByLayerId(R.id.shape_bacground);
            shape.setColor(backgroundColor);

//            setCanvas(shape);
            rippleColor = makePressColor();
        } catch (Exception ex) {
            // Without bacground
        }
    }

    abstract public TextView getTextView();

    public void setRippleSpeed(float rippleSpeed) {
        this.ripple_speed = rippleSpeed;
    }

    public float getRippleSpeed() {
        return this.ripple_speed;
    }
}

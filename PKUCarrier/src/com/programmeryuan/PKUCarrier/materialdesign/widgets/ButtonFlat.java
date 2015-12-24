package com.programmeryuan.PKUCarrier.materialdesign.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.programmeryuan.PKUCarrier.R;

public class ButtonFlat extends Button {

    TextView textButton;

    public ButtonFlat(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    protected void setDefaultProperties() {
        minHeight = 36;
        minWidth = 64;
        rippleSize = 3;

        // Min size
//        setMinimumHeight(Utils.dpToPx(minHeight, getResources()));
//        setMinimumWidth(Utils.dpToPx(minWidth, getResources()));
        setBackgroundResource(R.drawable.background_transparent);
    }

    @Override
    protected void setAttributes(AttributeSet attrs) {
        super.setAttributes(attrs);
        // Set text button
        String text = null;
        int textResource = attrs.getAttributeResourceValue(ANDROIDXML, "text", -1);
        int textSize = attrs.getAttributeResourceValue(ANDROIDXML, "textSize", 16);
//        int textColor = attrs.getAttributeResourceValue(ANDROIDXML, "textColor", R.color.black);
        if (textResource != -1) {
            text = getResources().getString(textResource);
        } else {
            text = attrs.getAttributeValue(ANDROIDXML, "text");
        }

        textButton = new TextView(getContext(), attrs);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        textButton.setLayoutParams(params);
        setPaddingFor(textButton, attrs);
        addView(textButton);
        if (text != null) {
            textButton.setText(text);
        }
//        if(textColor!=-1){
//            textButton.setTextColor(textColor);
//        }
        int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML, "background", -1);
        if (bacgroundColor != -1) {
            setBackgroundColor(getResources().getColor(bacgroundColor));
        } else {
            background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
            if (background != -1)
                setBackgroundColor(background);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (draw_effect) {

            Rect src = new Rect(0, 0, getWidth(), getHeight());
            Rect dst = new Rect(0, 0, getWidth(), getHeight());
            Bitmap bitmap = makeBitmap();
            canvas.drawBitmap(bitmap, src, dst, null);
//            bitmap.recycle();
//            bitmap = null;
            invalidate();
        }
    }

    public void setText(String text) {
        if(textButton != null)
            textButton.setText(text.toUpperCase());
    }

    // Set color of background
    public void setBackgroundColor(int color) {
        backgroundColor = color;
        if (isEnabled())
            beforeBackground = backgroundColor;
        textButton.setTextColor(color);
    }

    @Override
    public TextView getTextView() {
        return textButton;
    }

    public String getText() {
        return textButton.getText().toString();
    }

}

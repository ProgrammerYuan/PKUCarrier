package com.programmeryuan.PKUCarrier.materialdesign.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.utils.Util;

public class ProgressBarDetermininate extends CustomRelativeLayout {


    View progressView;
    int max = 100;
    int min = 0;
    int pendingProgress = -1;
    int progress = 0;
    int delta = 1;
    int minHeight = 10;
    int backgroundColor = Color.parseColor("#1E88E5");
    boolean is_animating = false;

    public ProgressBarDetermininate(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
    }

    // Set atributtes of XML to View
    protected void setAttributes(AttributeSet attrs) {

        progressView = new View(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1, 1);
        progressView.setLayoutParams(params);
        progressView.setBackgroundResource(R.drawable.background_progress);
        addView(progressView);
        setBackgroundResource(R.color.trans);
        //Set background Color
        // Color by resource
        int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML, "background", -1);
        if (bacgroundColor != -1) {
            setBackgroundColor(getResources().getColor(bacgroundColor));
        } else {
            // Color by hexadecimal
            int background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
            if (background != -1)
                setBackgroundColor(background);
            else
                setBackgroundColor(Color.parseColor("#1E88E5"));
        }

        min = attrs.getAttributeIntValue(MATERIALDESIGNXML, "min", 0);
        max = attrs.getAttributeIntValue(MATERIALDESIGNXML, "max", 100);
        progress = attrs.getAttributeIntValue(MATERIALDESIGNXML, "progress", min);
        pendingProgress = progress;
        minHeight = Util.getPX(4);
        setMinimumHeight(minHeight);

        post(new Runnable() {

            @Override
            public void run() {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) progressView.getLayoutParams();
                params.height = getHeight();
                progressView.setLayoutParams(params);
            }
        });

    }

    public int getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (pendingProgress != progress) {
            if (pendingProgress > progress) {
                updateBarProgress(true);
            } else if (pendingProgress < progress) {
                updateBarProgress(false);
            }
            invalidate();
        }
        is_animating = pendingProgress != progress;
    }

    public boolean isAnimating() {
        return is_animating;
    }

    void updateBarProgress(boolean increase) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) progressView.getLayoutParams();

        params.width = increase ? (params.width + delta) : (params.width - delta);
        params.height = getHeight();

        if (params.width > getWidth()) {
            params.width = getWidth();
        } else if (params.width < 0) {
            params.width = 0;
        }
        progressView.setLayoutParams(params);
        progress = (max - min) * params.width / getWidth();
        if ((increase && progress > pendingProgress) || (!increase && progress < pendingProgress)) {
            progress = pendingProgress;
        }
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setMin(int min) {
        this.min = min;
    }


    public void setProgress(int progress) {
        if (getWidth() == 0) {
            pendingProgress = progress;
        } else {
            pendingProgress = progress;
            if (pendingProgress > max)
                pendingProgress = max;
            if (pendingProgress < min)
                pendingProgress = min;
            delta = (int) (Math.abs(pendingProgress - this.progress) * 1.0 / max * getWidth() * fps / duration);
            invalidate();
        }
    }

    public int getProgress() {
        return progress;
    }

    // Set color of background
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        if (isEnabled())
            beforeBackground = backgroundColor;
        LayerDrawable layer = (LayerDrawable) progressView.getBackground();
        GradientDrawable shape = (GradientDrawable) layer.findDrawableByLayerId(R.id.shape_bacground);

        shape.setColor(color);
    }

}

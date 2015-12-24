package com.programmeryuan.PKUCarrier.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.R;

public abstract class ZDialog extends Dialog {
    public static final int direction_top = 0;
    public static final int direction_left = 1;
    public static final int direction_right = 2;
    public static final int direction_bottom = 3;
    Context c;
    int layout_id;
    View root;

    public ZDialog(Context context, int layout) {
        super(context, R.style.dialog);
        c = context;
        layout_id = layout;
        init();
    }

    public ZDialog(Context context) {
        super(context, R.style.dialog);
        c = context;
        init();
    }

    protected void init() {
        if (layout_id == 0) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = inflater.inflate(layout_id, null);
        setContentView(root);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        a = getWindow().getAttributes();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        a.gravity = Gravity.TOP | Gravity.LEFT;
        a.dimAmount = 0.5f; // 添加背景遮盖

    }

    WindowManager.LayoutParams a;
    public void clearDimAmount(){
        a.dimAmount = 0.0f;
    }
    public void showAtView(final View v, final int direction) {
        if (layout_id == 0) {
            return;
        }
        int[] loc = new int[2];

        v.getLocationOnScreen(loc);
        int y = loc[1] - PKUCarrierApplication.status_bar_height;
        WindowManager.LayoutParams a = getWindow().getAttributes();
        switch (direction) {
            case direction_top:
                a.x = loc[0];
                a.y = y - v.getHeight() - a.height;
                if (a.y < 0) {
                    a.y = 0;
                    a.height = y;
                }
                break;
            case direction_left:
                a.x = loc[0] - a.width;
                a.y = y;
                break;
            case direction_right:
                a.x = loc[0] + v.getWidth();
                a.y = y;
                break;
            case direction_bottom:
            default:
                a.windowAnimations = R.style.dialog_anim;
                a.x = loc[0];
                a.y = y + v.getHeight();
                if (y + v.getHeight() + a.height > PKUCarrierApplication.screen_height) {
                    a.height = PKUCarrierApplication.screen_height - PKUCarrierApplication.status_bar_height - (y + v.getHeight());
                }
                break;
        }
        getWindow().setAttributes(a);
        show();

    }

    public abstract void resize();

}

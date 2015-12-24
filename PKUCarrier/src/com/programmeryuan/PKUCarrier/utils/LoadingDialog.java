package com.programmeryuan.PKUCarrier.utils;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.materialdesign.widgets.ProgressBarCircularIndetermininate;
import com.programmeryuan.PKUCarrier.materialdesign.widgets.ProgressBarDetermininate;

public class LoadingDialog extends Dialog {
    ProgressBarCircularIndetermininate pb_eternal;
    ProgressBarDetermininate pb;
    LinearLayout ll;
    TextView tv_content;
    boolean is_enternal;
    Activity act;
    FragmentActivity act_v4;

    public LoadingDialog(Activity a, int res_color) {
        this(a, res_color, true);

    }

    public LoadingDialog(Activity a, int res_color, boolean e) {
        super(a, e ? R.style.AngelProgressDialogEternal : R.style.AngelProgressDialogEternal);
//        super(a, R.style.AngelProgressDialog);
        act = a;
        init(res_color, e);
    }

    public LoadingDialog(FragmentActivity a, int res_color) {
        this(a, res_color, true);
    }

    public LoadingDialog(FragmentActivity a, int res_color, boolean e) {
        super(a, e ? R.style.AngelProgressDialogEternal : R.style.AngelProgressDialogEternal);
//        super(a,R.style.AngelProgressDialog);
        act_v4 = a;
        init(res_color, e);

    }

    void init(int res_color, boolean e) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        is_enternal = e;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        if (!is_enternal) {
            lp.dimAmount = 0.25f;
        }
        getWindow().setAttributes(lp);
        setTitle(null);
//        setCancelable(is_enternal ? true : false);
        setCancelable(true);
//        setCanceledOnTouchOutside(true);
        setOnCancelListener(null);
        setContentView(R.layout.dialog_progress);
        pb = (ProgressBarDetermininate) findViewById(R.id.dialog_progress_pb);

        ll = (LinearLayout) findViewById(R.id.dialog_progress_pb_layout);
        ll.setVisibility(e ? View.GONE : View.VISIBLE);
        pb_eternal = (ProgressBarCircularIndetermininate) findViewById(R.id.dialog_progress_pb_eternal);
        pb_eternal.setVisibility(e ? View.VISIBLE : View.GONE);
        tv_content = (TextView) findViewById(R.id.dialog_progress_content);
        Context context = (act != null) ? act.getBaseContext() : act_v4.getBaseContext();
        pb.setBackgroundColor(context.getResources().getColor(res_color));
        pb_eternal.setBackgroundColor(context.getResources().getColor(res_color));
        try {
            show();
        } catch (WindowManager.BadTokenException exception){
            exception.printStackTrace();
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (!is_enternal) {
//            return false;
//        }
//        event.setLocation(event.getRawX(), event.getRawY());
//        if (act != null) {
//            act.dispatchTouchEvent(event);
//        } else if (act_v4 != null) {
//            act_v4.dispatchTouchEvent(event);
//        }
//        return false;
//    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
////        return super.dispatchTouchEvent(ev);
//        return false;
//    }

    public void setMax(int m) {
        if (is_enternal) {
            Logger.out("类型错误，无法设置最大值");
            return;
        }
        pb.setMax(m);
    }

    public void setProgress(int p) {
        if (is_enternal) {
            Logger.out("类型错误，无法设置进度");
            return;
        }
        pb.setProgress(p);
    }

    public void forceDismiss() {
        if (LoadingDialog.this != null && LoadingDialog.this.isShowing()) {
            super.dismiss();
        }
    }

    @Override
    public void dismiss() {
        int height = 0;

//        AnimationSet set2=new AnimationSet(true);
//        Animation anim3 = AnimationUtils.loadAnimation(this.getContext(), R.anim.slide_in_from_bottom);
//        Animation anim4 = AnimationUtils.loadAnimation(this.getContext(), R.anim.dialog_fade_out);
        View target = is_enternal ? pb_eternal : ll;
        height = target.getHeight();

        height = (int) (is_enternal ? (height * 1.2f) : (height * 0.4f));
//        if (is_enternal) {
//            height = target.getHeight();
//            height *= 1.2f;
//        } else {
//            WindowManager.LayoutParams lp = getWindow().getAttributes();
//            height = lp.height;
//            height /= 2.0f;
//        }
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(target, "translationY", 0, -height);
        anim1.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(target, "alpha", 1.0f, 0.0f);
        anim2.setInterpolator(new AccelerateInterpolator());
        set.playTogether(anim1, anim2);


        set.setDuration(is_enternal ? 600 : 400);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if ((act != null || act_v4 != null) && LoadingDialog.this != null && LoadingDialog.this.isShowing()) {
                    try {
                        LoadingDialog.super.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();

    }

}

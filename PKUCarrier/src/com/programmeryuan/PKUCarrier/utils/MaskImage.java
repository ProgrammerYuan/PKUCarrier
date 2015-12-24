package com.programmeryuan.PKUCarrier.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.programmeryuan.PKUCarrier.R;

public class MaskImage extends ImageView {
    int image = 0;
    int mask = 0;
    int back = 0;
//    RuntimeException mException;

    public MaskImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaskImage, 0, 0);
        image = a.getResourceId(R.styleable.MaskImage_image, 0);
        mask = a.getResourceId(R.styleable.MaskImage_mask, 0);
        back = a.getResourceId(R.styleable.MaskImage_frame, 0);

//        if (mImageSource == 0 || mMaskSource == 0 /*|| mBackgroundSource == 0*/) {
//            mException = new IllegalArgumentException(a.getPositionDescription() +
//                    ": The content attribute is required and must refer to a valid image.");
//        }
//        if (mException != null)
//            throw mException;
//        updateImage();
        a.recycle();
    }

//    @Override
//    public void setImageResource(int resId) {
//        super.setImageResource(resId);
//        Bitmap image = ((BitmapDrawable) getDrawable()).getBitmap();
//        updateImage(image);
//    }

//    @Override
//    public void setImageBitmap(Bitmap bm) {
//        super.setImageBitmap(bm);
//        updateImage(bm);
//    }

//    @Override
//    public void setImageDrawable(Drawable drawable) {
//        super.setImageDrawable(drawable);
//        Bitmap image = ((BitmapDrawable) getDrawable()).getBitmap();
//        updateImage(image);
////        updateImage();
//    }

    public void updateImage() {
        Bitmap original = BitmapFactory.decodeResource(getResources(), image);
//        Bitmap mask = BitmapFactory.decodeResource(getResources(), this.mask);
        updateImage(original);
    }
//
//    public void updateImage(Bitmap original) {
//        Bitmap mask = BitmapFactory.decodeResource(getResources(), this.mask);
//        updateImage(original, mask);
//    }

    public Bitmap updateImage(Bitmap original) {
        if (original == null ) {
            return null;
        }
        try {
            int width = original.getWidth();
            int height = original.getHeight();
            Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawBitmap(original, 0, 0, null);
            Bitmap mask_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            NinePatchDrawable mask =  (NinePatchDrawable) getResources().getDrawable(this.mask);
            if (mask != null) {
                mask.setBounds(0, 0, width, height);
                Canvas canvas_mask = new Canvas(mask_bitmap);
                mask.draw(canvas_mask);
            }
            canvas.drawBitmap(mask_bitmap, 0, 0, paint);
            paint.setXfermode(null);
            setImageBitmap(result);

//            setScaleType(ScaleType.CENTER);
            if (back != 0) {
                setBackgroundResource(back);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

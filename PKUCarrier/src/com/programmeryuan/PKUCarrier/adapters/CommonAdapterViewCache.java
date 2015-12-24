/**
 *
 */
package com.programmeryuan.PKUCarrier.adapters;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.programmeryuan.PKUCarrier.utils.image.ImageProvider;

import java.util.HashMap;


/**
 * 通用适配器的View缓存
 *
 * @author Michael
 */
public class CommonAdapterViewCache {
	private View v;
	HashMap<String, Bitmap> cache;

	public CommonAdapterViewCache() {
	}

	public CommonAdapterViewCache(View base) {
		this();
		this.v = base;
	}

	public CommonAdapterViewCache(View base, HashMap<String, Bitmap> c) {
		this();
		this.v = base;
		cache = c;
	}

	/**
	 * 为布局中的View赋值
	 *
	 * @param view_class 要赋值的View的class
	 * @param value      值
	 * @param view_id    要赋值的View的id
	 */
	public void setViewValue(Class<? extends View> view_class, final Object value, int view_id) {
		if (view_class.getName().equalsIgnoreCase("android.widget.TextView")) {
			TextView tv = (TextView) v.findViewById(view_id);
			tv.setText("" + value);
		} else if (view_class.getName().equalsIgnoreCase("android.widget.ImageView")) {
			final ImageView iv = (ImageView) v.findViewById(view_id);
			SimpleImageLoadingListener l = null;
			if (cache != null) {
				Bitmap b = null;
				try {
					b = cache.get(value.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (b != null) {
					iv.setImageBitmap(b);
				} else {
					l = new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							super.onLoadingComplete(imageUri, view, loadedImage);
							cache.put(imageUri, loadedImage);
						}
					};
					ImageProvider.display(iv, value, l);
				}

			} else {
				ImageProvider.display(iv, value);
			}
		} else if (view_class.getName().equalsIgnoreCase("android.widget.RatingBar")) {
			RatingBar rb = (RatingBar) v.findViewById(view_id);
			rb.setRating(Float.valueOf("" + value));
		}
	}

	/**
	 * 获取布局中的目标View
	 *
	 * @param view_id 要获取的View的id
	 * @return 目标View
	 */
	public View getView(int view_id) {
		return v.findViewById(view_id);
	}
}
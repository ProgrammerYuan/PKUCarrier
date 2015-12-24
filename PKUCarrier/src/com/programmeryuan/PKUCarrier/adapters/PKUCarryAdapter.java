package com.programmeryuan.PKUCarrier.adapters;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.listeners.OnCacheGeneratedListener;
import com.programmeryuan.PKUCarrier.models.PKUCarryItem;

import java.util.List;

/**
 * Created by ProgrammerYuan on 15/11/3.
 */
public class PKUCarryAdapter extends CommonAdapter<PKUCarryItem>{

	public PKUCarryAdapter(Context context, List<PKUCarryItem> list) {
		this(context,list, R.layout.item_like);
	}

	public PKUCarryAdapter(Context context, List<PKUCarryItem> list, int item_layout) {
		super(context, list, item_layout);
		init();
	}

	private void init(){
		l = new OnCacheGeneratedListener<PKUCarryItem>() {
			@Override
			public void onCacheGenerated(CommonAdapterViewCache c, PKUCarryItem pkuCarryItem) {
				c.setViewValue(ImageView.class, pkuCarryItem.uploader.avatar_temp,R.id.item_like_avatar);
				c.setViewValue(TextView.class, pkuCarryItem.title,R.id.item_like_name);
				c.setViewValue(TextView.class, pkuCarryItem.content,R.id.item_like_date);
			}
		};
	}
}

package com.programmeryuan.PKUCarrier.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.client.HttpRequest;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.activities.ChatV2Activity;
import com.programmeryuan.PKUCarrier.listeners.OnCacheGeneratedListener;
import com.programmeryuan.PKUCarrier.models.PKUDeal;
import com.programmeryuan.PKUCarrier.utils.Net;
import com.programmeryuan.PKUCarrier.utils.NetCallBack;
import com.programmeryuan.PKUCarrier.utils.Notifier;
import org.jivesoftware.smack.Chat;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ProgrammerYuan on 15/12/21.
 */
public class PKUUserTaskAdapter extends CommonAdapter<PKUDeal>{

	View complete_view,chat_view;
	Context context;
	
	public PKUUserTaskAdapter(Context context, List<PKUDeal> list) {
		this(context,list, R.layout.item_user_task);
	}

	public PKUUserTaskAdapter(Context context, List<PKUDeal> list, int item_layout) {
		super(context, list, item_layout);
		this.context = context;
		init();
	}

	private void init(){
		l = new OnCacheGeneratedListener<PKUDeal>() {
			@Override
			public void onCacheGenerated(CommonAdapterViewCache c, final PKUDeal PKUDeal) {
				c.setViewValue(ImageView.class, PKUDeal.uploader.avatar_temp,R.id.item_like_avatar);
				c.setViewValue(TextView.class, PKUDeal.title,R.id.item_like_name);
				c.setViewValue(TextView.class, PKUDeal.content,R.id.item_like_date);
				complete_view = c.getView(R.id.item_like_delete);
				chat_view = c.getView(R.id.item_like_complete);
				complete_view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//TODO
					}
				});
				
				chat_view.setOnClickListener(new View.OnClickListener(){
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, ChatV2Activity.class);
						intent.putExtra("item",PKUDeal);
						context.startActivity(intent);
					}
				});

				complete_view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						complete(PKUDeal);
					}
				});

				if(!PKUDeal.uploader.id.equals(PKUCarrierApplication.getUserId())) {
					complete_view.setVisibility(View.GONE);
				}
			}
		};
	}

	private void complete(PKUDeal pkuDeal) {
		final HttpUtils http = Net.getClient(context);
		LinkedHashMap<String, String> para = new LinkedHashMap<>();
		para.put("email",PKUCarrierApplication.user.sid);
		para.put("password",PKUCarrierApplication.user.pwd);
		para.put("deal_id",String.valueOf(pkuDeal.id));
		http.send(HttpRequest.HttpMethod.POST, Net.url_deal_complete, Net.getParam(HttpRequest.HttpMethod.POST, para), new NetCallBack() {

			@Override
			public void onSuccess(int status, String json, JSONObject request_param) {
				if(status == 0) {
					Notifier.showNormalMsg(context,"订单确认成功");
				}
			}

			@Override
			public void onFailure(HttpException error, String msg, JSONObject request_param) {
				Net.handleErrorCode(context,error.getExceptionCode(),msg,request_param);
			}
		});
	}
}

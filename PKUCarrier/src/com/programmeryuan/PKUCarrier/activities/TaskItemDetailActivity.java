package com.programmeryuan.PKUCarrier.activities;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.client.HttpRequest;
import com.makeramen.RoundedImageView;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.materialdesign.widgets.Dialog;
import com.programmeryuan.PKUCarrier.models.PKUCarryItem;
import com.programmeryuan.PKUCarrier.models.PrivateMessage;
import com.programmeryuan.PKUCarrier.models.User;
import com.programmeryuan.PKUCarrier.utils.*;
import com.programmeryuan.PKUCarrier.utils.image.ImageProvider;
import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Created by ProgrammerYuan on 15/12/2.
 */
public class TaskItemDetailActivity extends BActivity implements View.OnClickListener {

	PKUCarryItem item;
	ImageView iv_accept, iv_deny;
	TextView tv_info,tv_name,tv_time;
	RoundedImageView iv_avatar;
	LinearLayout ll_btn;
	LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_task_detail);
		Bundle data = getIntent().getExtras();
		BUtil.setupActionBar(this,"订单详情");
		if (data != null) {
			item = (PKUCarryItem) data.getSerializable("item");
			if (item == null) {
				Notifier.showNormalMsg(this, "信息有误");
				finish();
				return;
			}
		} else {
			Notifier.showNormalMsg(this, "信息有误");
			finish();
			return;
		}
		iv_accept = (ImageView) findViewById(R.id.item_get);
		iv_deny = (ImageView) findViewById(R.id.item_deny);
		iv_avatar = (RoundedImageView) findViewById(R.id.item_post2_owner_portrait);
		tv_info = (TextView) findViewById(R.id.item_post2_content);
		tv_time = (TextView) findViewById(R.id.item_post2_time);
		tv_name = (TextView) findViewById(R.id.item_post2_owner_name);
		ll_btn = (LinearLayout) findViewById(R.id.btn_ll);

		tv_info.setText(item.briefInfo());
		tv_name.setText(item.uploader.name);
		ImageProvider.display(iv_avatar,item.uploader.avatar_temp);

		iv_accept.setOnClickListener(this);
		iv_deny.setOnClickListener(this);

		if(item.uploader.id.equals(PKUCarrierApplication.getUserId())) {
			ll_btn.setVisibility(View.INVISIBLE);
			getAngelActionBar().setRightText("删除");
			getAngelActionBar().setRightListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					delete();
				}
			});
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.item_get:
				if(item.type == 0) respond();
				else respondWill();
				break;
			case R.id.item_deny:
				setResult(PKUCarrierApplication.result_ok);
				finish();
				break;
		}
	}

	private void respond(){
		final HttpUtils http = Net.getClient(this);
		LinkedHashMap<String,String> para = new LinkedHashMap<>();
		para.put("task_id",item.id);
		para.put("user_id",PKUCarrierApplication.getUserId());
		http.send(HttpRequest.HttpMethod.POST, Net.url_task_respond, Net.getParam(HttpRequest.HttpMethod.POST, para), new NetCallBack() {

			@Override
			public void onStart() {
				dialog = new LoadingDialog(TaskItemDetailActivity.this,R.color.pku_red,true);
			}

			@Override
			public void onSuccess(int status, String json, JSONObject request_param) {
				dialog.dismiss();
				if(status == 0) {
					setResult(PKUCarrierApplication.result_ok);
					iv_accept.setVisibility(View.INVISIBLE);
					iv_deny.setVisibility(View.INVISIBLE);
					final Dialog dialogs = new Dialog(TaskItemDetailActivity.this, "", null, R.color.pku_red);
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View custom = inflater.inflate(R.layout.dialog_task_result, null);
					dialogs.setCustomView(custom);
					dialogs.show();
					dialogs.setButtonAcceptText("准备出发~");
					dialogs.setButtonCancelText("关闭");
					dialogs.setOnAcceptButtonClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialogs.dismiss();
							tv_info.setText(item.detailedInfo());
						}
					});
					dialogs.setOnCancelButtonClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dialogs.dismiss();
							finish();
						}
					});
				}
			}

			@Override
			public void onFailure(HttpException error, String msg, JSONObject request_param) {
				dialog.dismiss();
				Net.handleErrorCode(TaskItemDetailActivity.this,error.getExceptionCode(),msg,request_param);
			}
		});
	}

	private void respondWill() {
		final HttpUtils http = Net.getClient(this);
		LinkedHashMap<String,String> para = new LinkedHashMap<>();
		para.put("user",item.uploader.IM_Id);
		para.put("needer_id",PKUCarrierApplication.getUserId());
		http.send(HttpRequest.HttpMethod.GET, Net.url_will_respond, Net.getParam(HttpRequest.HttpMethod.GET, para), new NetCallBack() {


			@Override
			public void onStart() {
				dialog = new LoadingDialog(TaskItemDetailActivity.this,R.color.pku_red,true);
			}

			@Override
			public void onSuccess(int status, String json, JSONObject request_param) {
				if(status == 0) {
					Notifier.showNormalMsg(TaskItemDetailActivity.this,"已发送消息提醒对应用户");
					User admin = new User(R.drawable.avatar1,"pku-courier小秘书");
					PrivateMessage privateMessage = new PrivateMessage(admin,item.uploader,new Date().getTime(), PKUCarrierApplication.getAdminMessage(PKUCarrierApplication.user),0);
					IMDB.savePrivateMessage(privateMessage);
					setResult(PKUCarrierApplication.result_ok);
					finish();
				}
 			}

			@Override
			public void onFailure(HttpException error, String msg, JSONObject request_param) {
				dialog.dismiss();
				Net.handleErrorCode(TaskItemDetailActivity.this,error.getExceptionCode(),msg,request_param);
			}
		});
	}

	private void delete() {
		final boolean isTaskType = item.type == 0;
		final HttpUtils http = Net.getClient(this);
		LinkedHashMap<String,String> para = new LinkedHashMap<>();
		para.put("uid",PKUCarrierApplication.getUserId());
		para.put(isTaskType ? "tid" : "wid",item.id);
		para.put("password",PKUCarrierApplication.getUserPwd());
		http.send(HttpRequest.HttpMethod.POST,isTaskType ? Net.url_task_delete : Net.url_will_delete, Net.getParam(HttpRequest.HttpMethod.POST, para), new NetCallBack() {


			@Override
			public void onStart() {
				dialog = new LoadingDialog(TaskItemDetailActivity.this,R.color.pku_red,true);
			}

			@Override
			public void onSuccess(int status, String json, JSONObject request_param) {
				if(status == 0) {
					Notifier.showNormalMsg(TaskItemDetailActivity.this,isTaskType ? "任务已删除" : "意愿已删除");
					setResult(PKUCarrierApplication.result_ok);
					finish();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg, JSONObject request_param) {
				dialog.dismiss();
				Net.handleErrorCode(TaskItemDetailActivity.this,error.getExceptionCode(),msg,request_param);
			}
		});
	}
}

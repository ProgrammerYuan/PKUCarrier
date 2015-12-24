package com.programmeryuan.PKUCarrier.activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.*;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.client.HttpRequest;
import com.makeramen.RoundedImageView;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.adapters.PKUCarryAdapter;
import com.programmeryuan.PKUCarrier.materialdesign.utils.Utils;
import com.programmeryuan.PKUCarrier.materialdesign.widgets.Dialog;
import com.programmeryuan.PKUCarrier.models.PKUCarryItem;
import com.programmeryuan.PKUCarrier.models.PKUDeal;
import com.programmeryuan.PKUCarrier.models.User;
import com.programmeryuan.PKUCarrier.utils.*;
import com.programmeryuan.PKUCarrier.utils.image.ImageProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
	/**
	 * Called when the activity is first created.
	 */
	PullToRefreshListView list;
	int width = Util.getPX(120);
	ArrayList<PKUCarryItem> items_task = new ArrayList<>(), items_will = new ArrayList<>();
	ArrayList<PKUCarryItem> items_task_to_add = new ArrayList<>(), items_will_to_add = new ArrayList<>();
	RelativeLayout rl_main,rl_actionbar;
	LinearLayout ll_home,ll_chat;
	RoundedImageView iv_avatar;
	TextView tab_left, tab_right, tv_logout, tv_name;
	TextView tv_add_task,tv_add_will;
	TextView tv_deal,tv_info;
	ImageView iv_me, iv_add;
	PKUCarryAdapter adapter_task, adapter_will;
	RelativeLayout popView;
	private PopupWindow popWind;
	User user;

	int selected_position = -1;
	int add_index_task = 0;
	int add_index_will = 0;
	boolean sidebar_showed = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		Bundle data = getIntent().getExtras();
		user = data != null ? (User)data.getSerializable("user") : new User(R.drawable.avatar2,"王钰翔");
		tab_left = (TextView) findViewById(R.id.actionbar_home_left_tab);
		tab_right = (TextView) findViewById(R.id.actionbar_home_right_tab);
		iv_me = (ImageView) findViewById(R.id.actionbar_home_me);
		iv_add = (ImageView) findViewById(R.id.act_main_add);
		iv_avatar = (RoundedImageView) findViewById(R.id.act_main_user_avatar);
		rl_main = (RelativeLayout) findViewById(R.id.act_main_main);
		rl_actionbar = (RelativeLayout) findViewById(R.id.action_bar);
		tv_info = (TextView) findViewById(R.id.act_main_user_info);
		tv_deal = (TextView) findViewById(R.id.act_main_user_deals);
		tv_name = (TextView) findViewById(R.id.act_main_user_name);
		tv_logout = (TextView) findViewById(R.id.act_main_logout);
		ll_chat = (LinearLayout) findViewById(R.id.chat_btn);

		if (user != null) {
			PKUCarrierApplication.user = user;
			tv_name.setText(user.name);
			ImageProvider.display(iv_avatar, user.avatar_temp);
		}
		tab_left.setSelected(true);
		tab_left.setOnClickListener(this);
		tab_right.setOnClickListener(this);
		iv_me.setOnClickListener(this);
		iv_add.setOnClickListener(this);
		tv_info.setOnClickListener(this);
		tv_deal.setOnClickListener(this);
		tv_logout.setOnClickListener(this);
		ll_chat.setOnClickListener(this);

		list = (PullToRefreshListView) findViewById(R.id.act_main_task_list);
		adapter_task = new PKUCarryAdapter(this, items_task);
		list.setAdapter(adapter_task);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selected_position = position - 1;
				Intent intent = new Intent(MainActivity.this, TaskItemDetailActivity.class);
				intent.putExtra("item", tab_left.isSelected() ? items_task.get(position - 1) : items_will.get(position - 1));
				startActivityForResult(intent, PKUCarrierApplication.request_detail);
			}
		});
		adapter_will = new PKUCarryAdapter(this, items_will);
		popView = (RelativeLayout) getLayoutInflater().inflate(R.layout.group_pop_view, null);
		tv_add_task = (TextView) popView.findViewById(R.id.group_pop_add_task);
		tv_add_task.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (popWind != null) popWind.dismiss();
				Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
				intent.putExtra("type",AddTaskActivity.TYPE_TASK);
				startActivityForResult(intent, PKUCarrierApplication.request_code_create_group_event);
			}
		});
		tv_add_will = (TextView) popView.findViewById(R.id.group_pop_add_will);
		tv_add_will.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (popWind != null) popWind.dismiss();
				Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
				intent.putExtra("type",AddTaskActivity.TYPE_WILL);
				startActivityForResult(intent, PKUCarrierApplication.request_code_create_group_chat);
			}
		});
//		final Dialog dialog = new Dialog(this, "", null, R.color.pku_red);
//		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View custom = inflater.inflate(R.layout.dialog_task_result, null);
//		dialog.setCustomView(custom);
//		dialog.show();
//		dialog.setButtonAcceptVisibility(View.GONE);
//		dialog.setButtonCancelVisibility(View.GONE);
//		dialog.setButtonAcceptText("出发咯~");
//		dialog.setButtonCancelText("查看顺路订单");
		getTask(true);
		getTask(false);
		list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getTask(tab_left.isSelected());
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

			}
		});
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		Intent intent = null;
		switch (id) {
			case R.id.actionbar_home_left_tab:
				if (!tab_left.isSelected())
					list.setAdapter(adapter_task);
				tab_left.setSelected(true);
				tab_right.setSelected(false);
				break;
			case R.id.actionbar_home_right_tab:
				if (!tab_right.isSelected())
					list.setAdapter(adapter_will);
				tab_left.setSelected(false);
				tab_right.setSelected(true);
				break;
			case R.id.actionbar_home_me:
				if (sidebar_showed) {
					hideSideBar();
				} else {
					showSideBar();
				}
				break;
			case R.id.act_main_add:
				popupOverflow();
//				if (tab_left.isSelected()) {
//					items_task.add(0, items_task_to_add.get(add_index_task));
//					add_index_task++;
//					add_index_task %= items_task_to_add.size();
//					adapter_task.notifyDataSetChanged();
//				} else {
//					items_will.add(0, items_will_to_add.get(add_index_will));
//					add_index_will++;
//					add_index_will %= items_will_to_add.size();
//					adapter_will.notifyDataSetChanged();
//				}
				break;
			case R.id.act_main_logout:
				IMDB.logout(PKUCarrierApplication.getUserId());
				intent = new Intent(MainActivity.this, EntranceActivity.class);
				startActivity(intent);
				finish();
				break;
			case R.id.chat_btn:
				intent = new Intent(MainActivity.this,ChatV2Activity.class);
				User admin = new User(R.drawable.avatar1,"pku-courier小秘书");
				PKUDeal deal = new PKUDeal(admin,PKUCarrierApplication.user);
				intent.putExtra("item",deal);
				startActivity(intent);
				break;
			case R.id.act_main_user_info:
				intent = new Intent(MainActivity.this,TaskSearchActivity.class);
				startActivity(intent);
				break;
			case R.id.act_main_user_deals:
//				Notifier.showNormalMsg(this,"历史订单信息当前无法获取");
				intent = new Intent(MainActivity.this,UserTaskListActivity.class);
				intent.putExtra("user_id",user != null ? user.id : "-1");
				startActivity(intent);
				break;
		}
	}

	private ObjectAnimator animator_show = ObjectAnimator.ofFloat(null, "test", 0.0f, 1.0f);
	private ObjectAnimator animator_hide = ObjectAnimator.ofFloat(null, "test", 1.0f, 0.0f);
	private ValueAnimator.AnimatorUpdateListener animListener = new ValueAnimator.AnimatorUpdateListener() {
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			float value = (Float) animation.getAnimatedValue();
			rl_main.setLeft((int) (value * width));
		}
	};

	private void showSideBar() {
		animator_show.addUpdateListener(animListener);
		animator_show.setDuration(500);
		animator_show.setInterpolator(new AccelerateDecelerateInterpolator());
		animator_show.start();
		sidebar_showed = true;
	}

	private void hideSideBar() {
		animator_hide.addUpdateListener(animListener);
		animator_hide.setDuration(500);
		animator_hide.setInterpolator(new AccelerateDecelerateInterpolator());
		animator_hide.start();
		sidebar_showed = false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PKUCarrierApplication.request_detail
				&& resultCode == PKUCarrierApplication.result_ok) {
			if (selected_position != -1) {
				if (tab_left.isSelected()) {
					items_task.remove(selected_position);
					adapter_task.notifyDataSetChanged();
				} else {
					items_will.remove(selected_position);
					adapter_will.notifyDataSetChanged();
				}
				selected_position = -1;
			}
		}

		if(requestCode == PKUCarrierApplication.request_code_create_group_chat
				&& resultCode == PKUCarrierApplication.result_ok) {
			PKUCarryItem item = (PKUCarryItem) data.getSerializableExtra("item");
			item.uploader = user;
			items_will.add(0,item);
			adapter_will.notifyDataSetChanged();
		}

		if(requestCode == PKUCarrierApplication.request_code_create_group_event
				&& resultCode == PKUCarrierApplication.result_ok) {
			PKUCarryItem item = (PKUCarryItem) data.getSerializableExtra("item");
			item.uploader = user;
			items_task.add(0,item);
			adapter_task.notifyDataSetChanged();
		}
	}

	void popupOverflow() {
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//           状态栏高度：frame.top
		int offset_y = frame.top + rl_actionbar.getHeight();//减去阴影宽度，适配UI.-25?
		int offset_x = Util.getPX(0f); //设置x方向offset为5dp
		View parentView = getLayoutInflater().inflate(R.layout.act_main, null);
		popWind = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);//popView即popupWindow的布局，ture设置focusAble.

		//必须设置BackgroundDrawable后setOutsideTouchable(true)才会有效。这里在XML中定义背景，所以这里设置为null;
		popWind.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
		popWind.setOutsideTouchable(true); //点击外部关闭。
//        popWind.setAnimationStyle(android.R.style.Animation_Dialog);    //设置一个动画。
		popWind.setAnimationStyle(R.style.AngelActivityAnim);    //设置一个动画。
		//设置Gravity，让它显示在右上角。
		popWind.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);// 。

		popWind.showAtLocation(parentView, Gravity.RIGHT | Gravity.TOP, offset_x, offset_y);

	}

	private void getTask(final boolean isTypeTask){
		final HttpUtils http = Net.getClient(this);
		http.send(HttpRequest.HttpMethod.GET, isTypeTask ? Net.url_task_list : Net.url_will_list, Net.getParam(), new NetCallBack() {
			@Override
			public void onSuccess(int status, String json, JSONObject request_param) {
				list.onRefreshComplete();
				if(status == 0) {
					Logger.out(json);
					try {
						JSONArray ja = new JSONArray(json);
						PKUCarryItem item;
						if(isTypeTask) adapter_task.clear();
						else adapter_will.clear();
						for(int i = 0; i < ja.length();i++) {
							item = new PKUCarryItem(ja.optJSONObject(i),isTypeTask ? 0 : 1);
							if(isTypeTask) {
								if(item.status == 0) adapter_task.add(item);
							} else adapter_will.add(item);
						}
						if(isTypeTask) adapter_task.notifyDataSetChanged();
						else adapter_will.notifyDataSetChanged();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg, JSONObject request_param) {
				Logger.out(msg);
				list.onRefreshComplete();
			}
		});
	}
}

package com.programmeryuan.PKUCarrier.activities;

import android.os.Bundle;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.client.HttpRequest;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.adapters.PKUCarryAdapter;
import com.programmeryuan.PKUCarrier.adapters.PKUUserTaskAdapter;
import com.programmeryuan.PKUCarrier.models.PKUCarryItem;
import com.programmeryuan.PKUCarrier.models.PKUDeal;
import com.programmeryuan.PKUCarrier.utils.BUtil;
import com.programmeryuan.PKUCarrier.utils.Net;
import com.programmeryuan.PKUCarrier.utils.NetCallBack;
import com.programmeryuan.PKUCarrier.utils.Notifier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by ProgrammerYuan on 15/12/17.
 */
public class UserTaskListActivity extends BActivity {

	PullToRefreshListView lv_task;
	PKUUserTaskAdapter adapter;
	ArrayList<PKUDeal> items = new ArrayList<>();
	String user_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_user_task);
		Bundle data = getIntent().getExtras();
		if(data != null) {
			user_id = data.getString("user_id","-1");
		}
		if(user_id.equals("-1")) {
			Notifier.showNormalMsg(this,"用户信息有误");
			finish();
			return;
		}
		BUtil.setupActionBar(this,"历史订单");
		lv_task = (PullToRefreshListView) findViewById(R.id.act_user_task_list);
		adapter = new PKUUserTaskAdapter(this,items);
		lv_task.setAdapter(adapter);
		lv_task.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getTasks();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				//DO NOTHING
			}
		});
		getTasks();
	}

	private void getTasks() {
		final HttpUtils http = Net.getClient(this);
		LinkedHashMap<String,String> para = new LinkedHashMap<>();
		para.put("user_id",String.valueOf(user_id));
		http.send(HttpRequest.HttpMethod.GET, Net.url_deal_user_list.replace("{user_id}", user_id), Net.getParam(HttpRequest.HttpMethod.GET, para), new NetCallBack() {
			@Override
			public void onSuccess(int status, String json, JSONObject request_param) {
				if(status == 0) {
					try {
						JSONObject jo = new JSONObject(json);
						items.clear();
						JSONArray ja = jo.optJSONArray("needer_deal");
						for(int i = 0;i<ja.length();i++) {
							items.add(new PKUDeal(ja.optJSONObject(i)));
						}
						ja = jo.optJSONArray("helper_deal");
						for(int i = 0;i<ja.length();i++) {
							items.add(new PKUDeal(ja.optJSONObject(i)));
						}
						adapter.notifyDataSetChanged();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg, JSONObject request_param) {

			}
		});
	}
}

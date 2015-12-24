package com.programmeryuan.PKUCarrier.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.client.HttpRequest;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.adapters.PKUCarryAdapter;
import com.programmeryuan.PKUCarrier.materialdesign.widgets.ButtonFlat;
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
 * Created by ProgrammerYuan on 15/12/23.
 */
public class TaskSearchActivity extends BActivity implements View.OnClickListener {

	ListView listView;
	EditText et_search;
	ButtonFlat btn_search;
	PKUCarryAdapter adapter;
	ArrayList<PKUCarryItem> items;
	private int selected_position = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_task_search);
		BUtil.setupActionBar(this, "任务搜索");
		listView = (ListView) findViewById(R.id.act_task_search_list);
		et_search = (EditText) findViewById(R.id.act_chat_text);
		btn_search = (ButtonFlat) findViewById(R.id.act_chat_send);
		items = new ArrayList<>();
		adapter = new PKUCarryAdapter(this, items);
		listView.setAdapter(adapter);
		btn_search.setOnClickListener(this);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selected_position = position;
				Intent intent = new Intent(TaskSearchActivity.this, TaskItemDetailActivity.class);
				intent.putExtra("item", items.get(position));
				startActivityForResult(intent, PKUCarrierApplication.request_detail);
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		String s;
		switch (id) {
			case R.id.act_chat_send:
				s = et_search.getText().toString();
				try {
					int uid = Integer.parseInt(s);
					search(uid);
				} catch (NumberFormatException e) {
					Notifier.showNormalMsg(this, "请输入合法用户ID");
				}
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PKUCarrierApplication.request_detail
				&& resultCode == PKUCarrierApplication.result_ok) {
			if (selected_position != -1) {
				items.remove(selected_position);
				adapter.notifyDataSetChanged();
				selected_position = -1;
			}
		}
	}

	private void search(int uid) {
		final HttpUtils http = Net.getClient(this);
		LinkedHashMap<String, String> para = new LinkedHashMap<>();
		para.put("user_id", String.valueOf(uid));
		http.send(HttpRequest.HttpMethod.GET, Net.url_task_user_list.replace("{user_id}", String.valueOf(uid)), Net.getParam(HttpRequest.HttpMethod.GET, para), new NetCallBack() {
			@Override
			public void onSuccess(int status, String json, JSONObject request_param) {
				if (status == 0) {
					try {
						JSONArray ja = new JSONArray(json);
						for (int i = 0; i < ja.length(); i++) {
							items.add(new PKUCarryItem(ja.optJSONObject(i), 0));
						}
						adapter.notifyDataSetChanged();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg, JSONObject request_param) {
				Net.handleErrorCode(TaskSearchActivity.this, error.getExceptionCode(), msg, request_param);
			}
		});
	}
}

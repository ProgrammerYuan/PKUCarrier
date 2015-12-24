package com.programmeryuan.PKUCarrier.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.client.HttpRequest;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.models.PKUCarryItem;
import com.programmeryuan.PKUCarrier.utils.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.LinkedHashMap;

/**
 * Created by ProgrammerYuan on 15/12/17.
 */
public class AddTaskActivity extends AngelActivity implements View.OnClickListener{

	EditText et_addr,et_detail_addr,et_pto,et_code,et_give_time,et_info;
	RelativeLayout rl_addr,rl_code,rl_givetime;
	ImageView iv_addr,iv_code,iv_givetime;
	String addr, detail_addr,pto,code,give_time,info;
	public static final int TYPE_TASK = 0;
	public static final int TYPE_WILL = 1;
	int type = -1;
	private Calendar now;
	private String date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_add_task);
		BUtil.setupActionBar(this,"添加任务");
		getAngelActionBar().setRightText("完成");
		getAngelActionBar().setRightListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkValid();
			}
		});
		Bundle data = getIntent().getExtras();
		if(data != null) {
			type = data.getInt("type",TYPE_TASK);
		}
		rl_addr = (RelativeLayout) findViewById(R.id.act_create_task_rl_addr);
		rl_code = (RelativeLayout) findViewById(R.id.act_create_task_rl_code);
		rl_givetime = (RelativeLayout) findViewById(R.id.act_create_task_rl_give_time);
		iv_addr = (ImageView) findViewById(R.id.act_create_task_addr_divider);
		iv_code = (ImageView) findViewById(R.id.act_create_task_code_divider);
		iv_givetime = (ImageView) findViewById(R.id.act_create_task_give_time_divider);
		et_addr = (EditText) findViewById(R.id.act_create_task_addr);
		et_detail_addr = (EditText) findViewById(R.id.act_create_task_detail_addr);
		et_pto = (EditText) findViewById(R.id.act_create_task_pto);
		et_code = (EditText) findViewById(R.id.act_create_task_code);
		et_give_time = (EditText) findViewById(R.id.act_create_task_give_time);
		et_info = (EditText) findViewById(R.id.act_create_task_info);
		et_give_time.setOnClickListener(this);
		if(type == TYPE_TASK ) {
			//DO NOTHING
		} else {
			rl_addr.setVisibility(View.GONE);
			rl_code.setVisibility(View.GONE);
			rl_givetime.setVisibility(View.GONE);
			iv_addr.setVisibility(View.GONE);
			iv_addr.setVisibility(View.GONE);
			iv_givetime.setVisibility(View.GONE);
		}
	}

	private void checkValid() {
		addr = et_addr.getText().toString();
		detail_addr = et_detail_addr.getText().toString();
		pto = et_pto.getText().toString();
		code = et_code.getText().toString();
		give_time = et_give_time.getText().toString();
		info = et_info.getText().toString();

		if(type == TYPE_TASK && !BUtil.isValidString(addr)) {
			Notifier.showNormalMsg(this,"请填写概要地址");
			return;
		}

		if(!BUtil.isValidString(detail_addr)) {
			Notifier.showNormalMsg(this,"请填写详细地址");
			return;
		}

		if(!BUtil.isValidString(pto)) {
			Notifier.showNormalMsg(this,"请填写交货地址");
			return;
		}

		if(type == TYPE_TASK && !BUtil.isValidString(code)) {
			Notifier.showNormalMsg(this,"请填写取货码");
			return;
		}

		if(type == TYPE_TASK && !BUtil.isValidString(give_time)) {
			Notifier.showNormalMsg(this,"请填写交货时间");
			return;
		}
		give_time += ":00";

		if(!BUtil.isValidString(info)) {
			Notifier.showNormalMsg(this,"请填写备注信息");
			return;
		}

		createTaskOnline();
	}

	private void createTask() {
//		final HttpUtils httpUtils = Net.getClient(this);
		PKUCarryItem item = new PKUCarryItem(PKUCarrierApplication.user,"",info,type,addr,detail_addr,code,give_time,pto);
		Intent data = new Intent();
		data.putExtra("item",item);
		setResult(PKUCarrierApplication.result_ok,data);
		finish();
	}

	private void createTaskOnline() {
		final HttpUtils http = Net.getClient(this);
		LinkedHashMap<String, String> para = new LinkedHashMap<>();
		para.put(type == TYPE_TASK ? "detailed_fplace" : "pfrom",detail_addr);
		para.put("pto",pto);
		para.put("info",info);
		para.put("owner" + (type == TYPE_TASK ? "" : "_id"),PKUCarrierApplication.getUserId());
		if(type == TYPE_TASK) {
			para.put("approximate_fplace", addr);
			para.put("code", code);
			para.put("fetch_btime", give_time);
			para.put("fetch_etime", give_time);
			para.put("give_time", give_time);
		}
		http.send(HttpRequest.HttpMethod.POST, type == TYPE_TASK ? Net.url_task_new : Net.url_will_new, Net.getParam(HttpRequest.HttpMethod.POST, para), new NetCallBack() {

			@Override
			public void onStart() {
				dialog = new LoadingDialog(AddTaskActivity.this,R.color.pku_red,true);
			}

			@Override
			public void onSuccess(int status, String json, JSONObject request_param) {
				Logger.out(json);
				dialog.dismiss();
				if(status == 0) {
					JSONObject jo = null;
					try {
						jo = new JSONObject(json);
						PKUCarryItem item = new PKUCarryItem(jo,type == TYPE_TASK ? 0 : 1);
						Intent data = new Intent();
						data.putExtra("item",item);
						setResult(PKUCarrierApplication.result_ok,data);
						finish();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}  else if(status == 3) {
					Notifier.showNormalMsg(AddTaskActivity.this,"用户尚未验证,无法发布任务或意愿");
				}
			}

			@Override
			public void onFailure(HttpException error, String msg, JSONObject request_param) {
				Logger.out(msg);
				dialog.dismiss();
			}

		});
	}

	private void showDataChooseDialog(final EditText et_chosen,String title) {
		now = Calendar.getInstance();
		final BYDatePickerDialog d = BYDatePickerDialog.newInstance(this, title,
				now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), R.color.main_1, R.style.BNumberPicker, BYDatePickerDialog.MODE_FULL_VERSION, new BYDatePickerDialog.OnDatePickedListener() {
					@Override
					public void onDatePicked(String result) {
						try {
							JSONObject jo = new JSONObject(result);
							date = jo.optString("date") + " " + jo.optString("time");
							et_chosen.setText(date);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
		d.show(getFragmentManager(), "datepicker");
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.act_create_task_give_time:
				showDataChooseDialog(et_give_time,"选择交货时间");
				break;
		}
	}
}

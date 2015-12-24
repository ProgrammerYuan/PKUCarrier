package com.programmeryuan.PKUCarrier.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.utils.IMDB;

/**
 * Created by ProgrammerYuan on 15/12/17.
 */
public class EntranceActivity extends AngelActivity implements View.OnClickListener{

	TextView tv_login,tv_register;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_entrance);
		PKUCarrierApplication.user = IMDB.getCurrentUser();
		if(PKUCarrierApplication.user != null) {
			PKUCarrierApplication.loginHX(PKUCarrierApplication.user.IM_Id,PKUCarrierApplication.user.IM_pwd);
			Intent intent = new Intent(EntranceActivity.this,MainActivity.class);
			intent.putExtra("user",PKUCarrierApplication.user);
			startActivity(intent);
			finish();
			return;
		}
		tv_login = (TextView) findViewById(R.id.act_entrance_login_btn);
		tv_register = (TextView) findViewById(R.id.act_entrance_register_btn);

		tv_login.setOnClickListener(this);
		tv_register.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		Intent intent;
		switch (id) {
			case R.id.act_entrance_login_btn:
				intent = new Intent(EntranceActivity.this,RegisterActivity.class);
				intent.putExtra("type",RegisterActivity.TYPE_LOGIN);
				startActivity(intent);
				finish();
				break;
			case R.id.act_entrance_register_btn:
				intent = new Intent(EntranceActivity.this,RegisterActivity.class);
				intent.putExtra("type",RegisterActivity.TYPE_REGISTER);
				startActivity(intent);
				finish();
				break;
		}
	}
}

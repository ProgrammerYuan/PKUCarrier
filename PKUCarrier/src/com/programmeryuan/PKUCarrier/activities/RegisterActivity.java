package com.programmeryuan.PKUCarrier.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.client.HttpRequest;
import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.models.User;
import com.programmeryuan.PKUCarrier.utils.*;
import com.programmeryuan.PKUCarrier.utils.image.ImageProvider;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Created by ProgrammerYuan on 15/11/18.
 */
public class RegisterActivity extends Activity implements View.OnClickListener {

	EditText et_username, et_id, et_password;
	RoundedImageView iv_avatar;
	RelativeLayout ll_user_name;
	ImageView iv_regiter;
	LoadingDialog dialog;
	public static final int TYPE_LOGIN = 0;
	public static final int TYPE_REGISTER = 1;
	int type = TYPE_REGISTER,random_avatar;
	boolean isRegister;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_register_step_1);
		Bundle data = getIntent().getExtras();
		if(data != null) {
			type = data.getInt("type",TYPE_REGISTER);
		}
		isRegister = type == TYPE_REGISTER;
		ll_user_name = (RelativeLayout) findViewById(R.id.act_register_username_ll);
		et_username = (EditText) findViewById(R.id.act_register_username_et);
		et_id = (EditText) findViewById(R.id.act_register_phone_et);
		et_password = (EditText) findViewById(R.id.act_register_pwd_et);
		iv_regiter = (ImageView) findViewById(R.id.act_register_next);
		iv_avatar = (RoundedImageView) findViewById(R.id.act_register_avatar);

		iv_regiter.setOnClickListener(this);
		if(type == TYPE_LOGIN) {
			iv_avatar.setVisibility(View.INVISIBLE);
			ll_user_name.setVisibility(View.GONE);
		} else {
			random_avatar = (int) (Math.floor(Math.random() * 5.0));
			ImageProvider.display(iv_avatar, PKUCarrierApplication.getRandomAvatarResId(random_avatar));
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.act_register_next:
				if (et_password.getText().toString().isEmpty()
						|| isRegister ? et_username.getText().toString().isEmpty() : false
						|| et_id.getText().toString().isEmpty()) {
					Notifier.showNormalMsg(this, "信息未填写完全");
					return;
				}
				register();
//				dialog = new LoadingDialog(this, R.color.pku_red, false);
//				new Handler().postDelayed(new Thread() {
//
//					@Override
//					public void run() {
//						dialog.dismiss();
//						Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//						intent.putExtra("user",new User(R.drawable.avatar3,et_username.getText().toString()));
//						startActivity(intent);
//						finish();
//					}
//				}, 1 * 1000);
				break;
		}

	}

	private void register() {
		final HttpUtils http = Net.getClient(this);
		LinkedHashMap<String, String> para = new LinkedHashMap<>();
		if(isRegister) {
			para.put("username", et_username.getText().toString());
			para.put("avatar",String.valueOf(random_avatar));
		}
		para.put("email", et_id.getText().toString() + "@pku.edu.cn");
		para.put("password", et_password.getText().toString());
		http.send(HttpRequest.HttpMethod.POST, isRegister ? Net.url_usr_signup : Net.url_usr_login, Net.getParam(HttpRequest.HttpMethod.POST, para), new NetCallBack() {

			@Override
			public void onStart() {
				dialog = new LoadingDialog(RegisterActivity.this,R.color.pku_red,true);
			}

			@Override
			public void onSuccess(int status, String json, JSONObject request_param) {
				dialog.dismiss();
				if (status == 0) {
					try {
						JSONObject jo = new JSONObject(json);
						User user = new User(jo);
						user.isCurrentUser = true;
						IMDB.updateCurrentUser(user);
						PKUCarrierApplication.loginHX(user.IM_Id,user.IM_pwd);
						Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
						intent.putExtra("user",user);
						startActivity(intent);
						finish();
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}

			@Override
			public void onFailure(HttpException error, String msg, JSONObject request_param) {
				dialog.dismiss();
				Net.handleErrorCode(RegisterActivity.this,error.getExceptionCode(),msg,request_param);
			}

		});
	}
}

package com.programmeryuan.PKUCarrier.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.easemob.EMCallBack;
import com.easemob.chat.*;
import com.easemob.exceptions.EaseMobException;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.adapters.PrivateMessageAdapter;
import com.programmeryuan.PKUCarrier.interfaces.OnRecordSuccessListener;
import com.programmeryuan.PKUCarrier.materialdesign.widgets.ButtonFlat;
import com.programmeryuan.PKUCarrier.models.*;
import com.programmeryuan.PKUCarrier.utils.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by shonenight on 2015/5/14.
 */
@ContentView(R.layout.act_chat)
public class ChatV2Activity extends BActivity implements SensorEventListener {
	//    @ViewInject(R.id.act_chat_ll_main)
//    SoftInputLinearLayout ll_main;
	@ViewInject(R.id.act_chat_list)
	PullToRefreshListView lv_msg;
	@ViewInject(R.id.act_chat_voice)
	Button b_voice;
	@ViewInject(R.id.act_chat_send)
	ButtonFlat b_send;
	@ViewInject(R.id.act_chat_change)
	ImageView b_change;
	@ViewInject(R.id.act_chat_text)
	EditText et_text;
	@ViewInject(R.id.act_chat_attach_layout)
	LinearLayout ll_attach;
	@ViewInject(R.id.act_chat_attach)
	ImageView b_attach;
	@ViewInject(R.id.act_chat_attach_image)
	ImageView iv_attach_image;
	@ViewInject(R.id.act_chat_attach_camera)
	ImageView iv_attach_camera;

	boolean b_group_chat = false;
	NewMessageBroadcastReceiver receiver;
	NewOfflineMessageBroadcastReceiver offlineReceiver;
	public PrivateMessage msg_playing = null;
	String IM_id;
	PrivateMessageAdapter adapter;
	public ArrayList<PrivateMessage> msgs = new ArrayList<>();
	int offset = 0, limit = 20;
	long date = 0l;
	int conversation_id;
	boolean is_top = false;
	boolean has_modify = false;
	boolean not_save = false;//true，onpause时不保存会话
	private SensorManager sensorManager;
	private AudioManager audioManager;
	private Sensor proximitySensor = null;
	boolean has_bestow = false;
	Intent bestow_data = null;
	PowerManager pm;
	PowerManager.WakeLock mWakeLock;
	Handler lv_handler;
	PKUDeal item;
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

		}
	};
	private User contact;

	//打开扬声器
	public void OpenSpeaker() {
		try {
			//获取当前通话音量
			if (!audioManager.isSpeakerphoneOn()) {
				audioManager.setSpeakerphoneOn(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//关闭扬声器
	public void CloseSpeaker() {

		try {
			if (audioManager != null) {
				if (audioManager.isSpeakerphoneOn()) {
					audioManager.setSpeakerphoneOn(false);
//                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currVolume, 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean initDefaultSensor() {
		if (proximitySensor != null) {
			return true;
		}
		if (sensorManager == null) return false;
		proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		if (proximitySensor == null || audioManager == null) {
			return false;
		}
		audioManager.setMode(AudioManager.MODE_IN_CALL);
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		Intent intent = getIntent();
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Recording");
		sensorManager = ((SensorManager) getSystemService(Context.SENSOR_SERVICE));
		audioManager = ((AudioManager) getSystemService(Context.AUDIO_SERVICE));
		BUtil.setupActionBar(this, "消息");
		adapter = new PrivateMessageAdapter(this, msgs);
		b_group_chat = intent.getBooleanExtra("b_group_chat", false);
		conversation_id = intent.getIntExtra("conversation_id", 0);
		initIMData(intent);

		lv_msg.getRefreshableView().setAdapter(adapter);
		lv_msg.setHideHeaderLayout(true);

		b_send.setVisibility(View.INVISIBLE);
		b_change.setVisibility(View.GONE);
		et_text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setListviewSelection(msgs.size());
				BUtil.showInputBoard(et_text, ChatV2Activity.this);
				if (lv_handler == null)
					lv_handler = new Handler();

				lv_handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						lv_msg.requestFocus();
						setListviewSelection(msgs.size());
						et_text.requestFocus();
					}
				}, 100);


//				setListviewSelection(msgs.size());
			}
		});
		b_send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = et_text.getText().toString();
				if (TextUtils.isEmpty(content)) {
					return;
				}
				try {
					sendTextMsg(content);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		b_send.setVisibility(View.VISIBLE);
		b_attach.setVisibility(View.INVISIBLE);
		iv_attach_image.setVisibility(View.INVISIBLE);
		lv_msg.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		lv_msg.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
		lv_msg.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getMessages();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

			}
		});
		lv_msg.getRefreshableView().setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Util.hideInputBoard(ChatV2Activity.this);
				return false;
			}
		});
		lv_msg.requestFocus();
		receiver = new NewMessageBroadcastReceiver();
		IntentFilter filter = new IntentFilter("com.qxstudy.bangyoung.easemob.newmessage");
		filter.setPriority(5);
		registerReceiver(receiver, filter);

		offlineReceiver = new NewOfflineMessageBroadcastReceiver();
		IntentFilter filter2 = new IntentFilter("com.qxstudy.bangyoung.easemob.offlinemessage");
		filter.setPriority(5);
		registerReceiver(offlineReceiver, filter2);
	}

	@Override
	protected void onResume() {
		super.onResume();
		ArrayList<PrivateMessage> temp = IMDB.getLatestMessages(contact.name,PKUCarrierApplication.getUsername(), msgs.isEmpty() ? 0 : msgs.get(msgs.size() - 1).id);
		if (temp.size() > 0) {
			msgs.addAll(temp);
			adapter.notifyDataSetChanged();
			temp.clear();
		}
		if (initDefaultSensor()) {
			sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (proximitySensor != null)
			sensorManager.unregisterListener(this, proximitySensor);
	}

	void initIMData(Intent intent) {
		Bundle data = intent.getExtras();
		if (data != null) {
			item = (PKUDeal) data.getSerializable("item");
			if (item == null) {
				Notifier.showNormalMsg(this, "用户信息有误");
				finish();
				return;
			}
		} else {
			Notifier.showNormalMsg(this, "用户信息有误");
			finish();
			return;
		}

		contact = item.getReceiver();
		IM_id = contact.IM_Id;
	}

	void getMessages() {
			ArrayList<PrivateMessage> temp = IMDB.getMessages(contact.name, PKUCarrierApplication.getUsername(), limit, offset);
			long last_date = 0l, this_date = 0l;
			LinkedList<String> date_temp = new LinkedList<>();
			for (int i = 0; i < temp.size(); i++) {
				PrivateMessage pm = temp.get(i);
				this_date = Long.parseLong(pm.date);
				if (Math.abs(last_date - this_date) > 60 * 1000 * 3) {
					date_temp.add(pm.id + "");
//                adapter.date_visible_msgs.add(pm.id + "");
				}
				last_date = this_date;
				if (offset == 0) {
					date = last_date;//date保存最后一条消息的时间
				} else {
					if (Math.abs(last_date - this_date) <= 60 * 1000 * 3) {//上一页最后一条时间和下一页第一条时间在3分钟之内，移除下一页第一条的时间
						adapter.date_visible_msgs.remove(0);
					}
				}
				adapter.date_visible_msgs.addAll(0, date_temp);
			}
			msgs.addAll(0, temp);
			if (temp != null && temp.size() > 0) {
				adapter.notifyDataSetChanged();
			}
			if (offset == 0) {
//            lv_msg.getRefreshableView().setSelection(msgs.size());
				if (lv_handler == null)
					lv_handler = new Handler();
				lv_handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						setListviewSelection(msgs.size());
					}
				}, 100);

			} else {
				setListviewSelection(0);
			}
			offset = msgs.size();
			if (handler == null) {
				handler = new Handler();
			}
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					lv_msg.onRefreshComplete();
				}
			},200);
	}

	void setListviewSelection(final int pos) {
		if (lv_handler == null) {
			lv_handler = new Handler();
		}
		lv_handler.post(new Runnable() {
			@Override
			public void run() {
//				lv_msg.getRefreshableView().setSelection(pos);
				lv_msg.getRefreshableView().smoothScrollToPositionFromTop(pos, 0, 10);
			}
		});
	}


	boolean checkValid() {
		return TextUtils.isEmpty(IM_id);
	}

	public void sendTextMsg(String content) {
		if (checkValid() && !b_group_chat) {//单聊
			Notifier.showNormalMsg(this, "网络不给力呀！");
			return;
		}
		boolean is_connected = EMChatManager.getInstance().isConnected();
		et_text.setText("");
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		TextMessageBody txtBody = new TextMessageBody(content);
		message.addBody(txtBody);
		message.setAttribute("admin_name", PKUCarrierApplication.getUsername());
		message.setAttribute("avatar", PKUCarrierApplication.getThumbnailIndex());
		message.setChatType(EMMessage.ChatType.Chat);
		message.setReceipt(IM_id);
		final PrivateMessage privateMessage;
		privateMessage = new PrivateMessage( PKUCarrierApplication.user,contact, message, ((TextMessageBody) message.getBody()).getMessage(), EMMessage.Type.TXT.ordinal());
		privateMessage.date = System.currentTimeMillis() + "";
		final int privateMessage_id = IMDB.savePrivateMessage(privateMessage);//存储消息
		privateMessage.id = privateMessage_id;
		if (adapter.date_visible_msgs == null) {
			adapter.date_visible_msgs = new LinkedList<>();
			adapter.date_visible_msgs.add(privateMessage_id + "");
		} else {
			if (Math.abs(Long.parseLong(privateMessage.date) - date) > 60 * 1000 * 3) {
				adapter.date_visible_msgs.add(privateMessage_id + "");
			}
		}
		date = Long.parseLong(privateMessage.date);
		msgs.add(privateMessage);
		offset = msgs.size();
		adapter.notifyDataSetChanged();
		setListviewSelection(msgs.size());
		if (is_connected) {
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
				@Override
				public void onSuccess() {
//                Notifier.showShortMsg(ChatV2Activity.this, "发送成功！");
//					IMDB.updatePrivateMessageStatus(privateMessage_id, PrivateMessage.STATUS_SENT);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							adapter.notifyDataSetChanged();
						}
					});
				}

				@Override
				public void onError(int i, String s) {
//                Notifier.showShortMsg(ChatV2Activity.this, "发送失败！");
//					IMDB.updatePrivateMessageStatus(privateMessage_id, PrivateMessage.STATUS_FAILED);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							adapter.notifyDataSetChanged();
						}
					});
				}

				@Override
				public void onProgress(int i, String s) {
					// TODO set status
				}
			});
		}
	}

	@Override
	public final void onAccuracyChanged(Sensor sensor, int accuracy) {
		if (!(sensor.getType() == Sensor.TYPE_PROXIMITY)) {
			return;
		}
	}

	@Override
	public final void onSensorChanged(SensorEvent event) {
		if (!(event.sensor.getType() == Sensor.TYPE_PROXIMITY)) {
			return;
		}
		float distanceInCentimeters = event.values[0];
		if (distanceInCentimeters < proximitySensor.getMaximumRange()) {
			Logger.out("Proximity sensor => NEAR state");
			CloseSpeaker();
		} else {
			Logger.out("Proximity sensor => FAR state");
			OpenSpeaker();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

	class NewMessageBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			EMMessage message = intent.getParcelableExtra("message");
			handleNewMessage(this, message);
		}
	}

	class NewOfflineMessageBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			ArrayList<EMMessage> messages = intent.getParcelableArrayListExtra("messages");
			if (messages == null || messages.size() == 0) {
				abortBroadcast();
				return;
			}
			for (EMMessage message : messages) {
				handleNewMessage(this, message);
			}
		}
	}

	void handleNewMessage(BroadcastReceiver broadcastReceiver, EMMessage message) {
		String HX_id = message.getFrom();
		int msgType = message.getType().ordinal();
		int chatType = message.getChatType().ordinal();
		broadcastReceiver.abortBroadcast();
		if (HX_id.equals(IM_id) && chatType == EMMessage.ChatType.Chat.ordinal()) {
			String user_name = message.getStringAttribute("admin_name", "");
			int user_thumbnail = message.getIntAttribute("avatar", 0);
			PrivateMessage privateMessage = new PrivateMessage(contact, PKUCarrierApplication.user, message, ((TextMessageBody) message.getBody()).getMessage(), msgType);
			int privateMessage_id = IMDB.savePrivateMessage(privateMessage);
			privateMessage.id = privateMessage_id;
			if (adapter.date_visible_msgs == null) {
				adapter.date_visible_msgs = new LinkedList<>();
				adapter.date_visible_msgs.add(privateMessage_id + "");
			} else {
				if (Math.abs(Long.parseLong(privateMessage.date) - date) > 60 * 1000 * 3) {
					adapter.date_visible_msgs.add(privateMessage_id + "");
				}
			}
			date = Long.parseLong(privateMessage.date);
			msgs.add(privateMessage);
			offset = msgs.size();
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onPause() {
		if (msgs.size() > 0 && !not_save) {
			PrivateMessage privateMessage = msgs.get(msgs.size() - 1);
		}
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		if (offlineReceiver != null) {
			unregisterReceiver(offlineReceiver);
			offlineReceiver = null;
		}
		finish();
	}

//    @Override
//    protected void onDestroy() {
//        if (receiver != null) {
//            unregisterReceiver(receiver);
//            receiver = null;
//        }
//        super.onDestroy();
//    }
}
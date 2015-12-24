package com.programmeryuan.PKUCarrier;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.easemob.*;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.programmeryuan.PKUCarrier.activities.AngelActionBar;
import com.programmeryuan.PKUCarrier.models.User;
import com.programmeryuan.PKUCarrier.utils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ProgrammerYuan on 15/11/4.
 */
public class PKUCarrierApplication extends Application {

	public static final int request_detail = 1001;
	public static final int result_ok = 10001;
	public static User user;
	public static PKUCarrierApplication instance;
	public static boolean db_inited = false;
	public static int request_code_take_picture = 1001;
	public static int request_code_select_photo = 1002;
	public static int request_code_create_group_event = 1003;
	public static int request_code_create_group_chat = 1004;

	public static int screen_width;
	public static int screen_height;
	public static int status_bar_height;

	@Override
	public void onCreate() {
		super.onCreate();
		Util.c = this;
		instance = this;
		IMDB.init(this);
		onImInit(this);
//		loginHX("pkucourier_1200012864", "123456");
		getScreenSize();
	}

	public static String getUserId() {
		return user == null ? "-1" : user.id;
	}

	public static String getUsername() {
		return user == null ? "" : user.name;
	}

	public static int getThumbnail() {
		return user.avatar_temp;
	}

	public static int getThumbnailIndex() {
		return getRandomAvatarByResId(getThumbnail());
	}

	public static int getUserAvatar() {
		return getThumbnail();
	}

	void getScreenSize() {
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screen_width = size.x;
		screen_height = size.y;
		AngelActionBar.default_color = R.color.pku_red;
		AngelActionBar.default_arrow_drawable = R.drawable.icon_back_new_arrow;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			status_bar_height = getResources().getDimensionPixelSize(resourceId);
		}
		Logger.out("screen size:" + screen_width + "×" + screen_height);
	}

	static Handler handler = new Handler();

	public static void loginHX(String username, String password) {
		if (username == null || password == null || username.isEmpty() || password.isEmpty()) return;
		EMChatManager.getInstance().login(username, password, new EMCallBack() {
			@Override
			public void onSuccess() {
				Notifier.showNormalMsg(instance, "登陆成功");
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Logger.out("xxxxxxxxxxxx");
						EMChat.getInstance().setAppInited();
					}
				}, 3000);
			}

			@Override
			public void onError(int i, final String s) {

				Notifier.showNormalMsg(instance, "登陆失败");
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        File file = new File("/sdcard/bangyoung_im_log.txt");
//                        if (!file.exists()) {
//                            try {
//                                file.createNewFile();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        writeContent(file, DateProvider.getDate(DateProvider.format_datetime, System.currentTimeMillis()) + " : " + s + "\n");
//                    }
//                });
			}

			@Override
			public void onProgress(int i, String s) {

			}
		});
	}

	private synchronized boolean onImInit(Context context) {
		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
		// 如果app启用了远程的service，此application:onCreate会被调用2次
		// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
		// 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回

		if (processAppName == null || !processAppName.equalsIgnoreCase("com.programmeryuan.pkucarrier")) {
//            Log.e(TAG, "enter the service process!");
			//"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名

			// 则此application::onCreate 是被service 调用的，直接返回
			return false;
		}
		//初始化环信SDK
		Log.d("DemoApplication", "Initialize EMChat SDK");
		EMChat.getInstance().init(context);
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
//        关闭消息提醒，始终走广播
		options.setShowNotificationInBackgroud(false);
		//关闭新消息震动或声音
		options.setNotifyBySoundAndVibrate(false);
		options.setNoticeBySound(false);
		options.setNoticedByVibrate(false);

        /*//获取到EMChatOptions对象
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        //默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        //设置收到消息是否有新消息通知，默认为true
        options.setNotificationEnable(false);
        //设置收到消息是否有声音提示，默认为true
        options.setNoticeBySound(false);
        //设置收到消息是否震动 默认为true
//        options.setNoticedByVibrate(false);
        //设置语音消息播放是否设置为扬声器播放 默认为true
        options.setUseSpeaker(false);
        options.setOnNotificationClickListener(new OnNotificationClickListener() {
            @Override
            public Intent onNotificationClick(EMMessage emMessage) {
                Intent intent = new Intent(BApplication.this,HomeV3Activity.class);
                Bundle data = new Bundle();
                if(pluralNotification){ //复数个推送的情况

                } else {

                }
                return null;
            }
        });
        *//*
		 * from TMallGod: 下面四个子函数在jar中调用顺序为
         *                  1.onNewMessageNotify
         *                  2.onSetNotificationTitle
         *                  3.onLatestMessageNotify
         *                  4.onSetSmallIcon
         *              如果相互之间判断条件有联系，请注意调用顺序
         *              另外：这四个函数每次推送都会调用，所以实际上onNewMessage仅负责
         *              设置Notification出来时在状态栏显示的信息，
         *              详细的推送显示都由onLatestMesageNotify负责
         *//*
        options.setNotifyText(new OnMessageNotifyListener() {
            @Override
            public String onNewMessageNotify(EMMessage emMessage) {
                String ret = "";
                return ret;
            }

            @Override
            public String onLatestMessageNotify(EMMessage emMessage, int fromUsersNum, int messageNum) {
                String ret = "";
                if (fromUsersNum > 1) { //多人消息推送的情况

                } else { //单个推送的情况

                }
                return ret;
            }

            @Override
            public String onSetNotificationTitle(EMMessage emMessage) {
                return "榜样教育";
            }

            @Override
            public int onSetSmallIcon(EMMessage emMessage) {
                return R.drawable.ic_launcher;
            }
        });*/

		EMChatManager.getInstance().addConnectionListener(new EMConnectionListener() {
			@Override
			public void onConnected() {
//                Net.handleErrorCode("成功连接到聊天服务器", instance);
			}

			@Override
			public void onDisconnected(final int error) {
//                File file = new File("/sdcard/bangyoung_im_log.txt");
//                if (!file.exists()) {
//                    try {
//                        file.createNewFile();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
				if (error == EMError.USER_REMOVED) {
					Net.handleErrorCode("账号被移除", instance);
//                    writeContent(file, DateProvider.getDate(DateProvider.format_datetime, System.currentTimeMillis()) + " : 账号被移除\tUSER_REMOVED\n");
				} else if (error == EMError.CONNECTION_CONFLICT) {
					Net.handleErrorCode("账号在其他设备登录", instance);
//                    writeContent(file, DateProvider.getDate(DateProvider.format_datetime, System.currentTimeMillis()) + " : 账号在其他设备登录\tCONNECTION_CONFLICT\n");
				} else {

					Net.handleErrorCode("当前网络不可用，请检查网络设置", instance);
//                        writeContent(file, DateProvider.getDate(DateProvider.format_datetime, System.currentTimeMillis()) + " : 当前网络不可用，请检查网络设置\tCONNECTION_CONFLICT\n");
				}
			}
		});

		EMChatManager.getInstance().registerEventListener(new EMEventListener() {

			@Override
			public void onEvent(EMNotifierEvent event) {

				switch (event.getEvent()) {
					case EventNewMessage: // 接收新消息
					{
						EMMessage message = (EMMessage) event.getData();
						Intent intent = new Intent("com.qxstudy.bangyoung.easemob.newmessage");
						intent.putExtra("message", message);
						sendOrderedBroadcast(intent, null);
						break;
					}
					case EventDeliveryAck: {//接收已发送回执

						break;
					}

					case EventNewCMDMessage: {//接收透传消息
						EMMessage message = (EMMessage) event.getData();
						Intent intent = new Intent("com.qxstudy.bangyoung.easemob.cmdmessage");
						intent.putExtra("message", message);
						sendOrderedBroadcast(intent, null);
						break;
					}

					case EventReadAck: {//接收已读回执

						break;
					}

					case EventOfflineMessage: {//接收离线消息
						ArrayList<EMMessage> messages = (ArrayList<EMMessage>) event.getData();
						Intent intent = new Intent("com.qxstudy.bangyoung.easemob.offlinemessage");
						intent.putParcelableArrayListExtra("messages", messages);
						sendOrderedBroadcast(intent, null);
						break;
					}

					case EventConversationListChanged: {//通知会话列表通知event注册（在某些特殊情况，SDK去删除会话的时候会收到回调监听）

						break;
					}

					default:
						break;
				}
			}
		});

		return true;
	}

	public static int getRandomAvatarResId(int random) {
		switch (random) {
			case 0:
				return R.drawable.avatar1;
			case 1:
				return R.drawable.avatar2;
			case 2:
				return R.drawable.avatar3;
			case 3:
				return R.drawable.avatar4;
			case 4:
			default:
				return R.drawable.avatar5;
		}
	}

	public static int getRandomAvatarByResId(int res_id) {
		switch (res_id) {
			case R.drawable.avatar1:
				return 0;
			case R.drawable.avatar2:
				return 1;
			case R.drawable.avatar3:
				return 2;
			case R.drawable.avatar4:
				return 3;
			case R.drawable.avatar5:
			default:
				return 4;
		}
	}

	public static String getAdminMessage(User need) {
		return need.name + "(ID:" + need.id + ") 想让你帮忙拿快递,去搜索一下看看能不能帮上忙吧~";
	}

	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = this.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i
					.next());
			try {
				if (info.pid == pID) {
					CharSequence c = pm.getApplicationLabel(pm
							.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
			}
		}
		return processName;
	}

	public static String getUserPwd() {
		return user == null ? "" : user.pwd;
	}
}

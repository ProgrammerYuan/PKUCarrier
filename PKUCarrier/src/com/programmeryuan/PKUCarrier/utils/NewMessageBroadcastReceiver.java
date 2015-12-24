package com.programmeryuan.PKUCarrier.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.models.PrivateMessage;
import com.programmeryuan.PKUCarrier.models.User;

import java.util.LinkedList;

/**
 * Created by shonenight on 2015/4/15.
 */
public class NewMessageBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		abortBroadcast();

		EMMessage message = intent.getParcelableExtra("message");
		String HX_id = message.getFrom();
		int msgType = message.getType().ordinal();
		int chatType = message.getChatType().ordinal();
		String user_name = message.getStringAttribute("admin_name", "");
		int user_thumbnail = message.getIntAttribute("avatar", 0);
		User contact = new User(PKUCarrierApplication.getRandomAvatarResId(user_thumbnail), user_name);
		PrivateMessage privateMessage = new PrivateMessage(contact, PKUCarrierApplication.user, message, ((TextMessageBody) message.getBody()).getMessage(), msgType);
		int privateMessage_id = IMDB.savePrivateMessage(privateMessage);
	}
}

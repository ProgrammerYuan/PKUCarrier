package com.programmeryuan.PKUCarrier.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.models.PrivateMessage;
import com.programmeryuan.PKUCarrier.models.User;

import java.util.ArrayList;

/**
 * Created by shonenight on 2015/6/19.
 */
public class NewOfflineMessageBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        abortBroadcast();

        ArrayList<EMMessage> messages = intent.getParcelableArrayListExtra("messages");
        if (messages == null || messages.size() == 0) {
            abortBroadcast();
            return;
        }
        for (EMMessage message : messages) {
            String HX_id = message.getFrom();
            int msgType = message.getType().ordinal();
            String user_name = message.getStringAttribute("admin_name", "");
            int user_thumbnail = message.getIntAttribute("avatar", 0);
            User contact = new User(PKUCarrierApplication.getRandomAvatarResId(user_thumbnail), user_name);
            PrivateMessage privateMessage = new PrivateMessage(contact, PKUCarrierApplication.user, message, ((TextMessageBody) message.getBody()).getMessage(), msgType);
            int privateMessage_id = IMDB.savePrivateMessage(privateMessage);
        }
    }
}

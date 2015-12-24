package com.programmeryuan.PKUCarrier.models;
import android.database.Cursor;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.utils.DateProvider;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Michael on 2014/12/12.
 */
public class PrivateMessage extends DBEntry implements Serializable {
    public static final int STATUS_RECEIVED = 0;//未读
    public static final int STATUS_SENDING = 1;//发送中
    public static final int STATUS_FAILED = -1;//发送失败
    public static final int STATUS_SENT = 2;//已发送
    public static final int STATUS_READ = 3;//已读消息，如果是IMAGE或者VOICE，则未下载
    public static final int STATUS_FILE_DOWNLOADING = 4;//已读、下载中
    public static final int STATUS_FILE_DOWNLOADED = 5;//已读、已下载
    public static final int STATUS_FILE_DOWNLOAD_FAILED = -2;//已读、下载失败

    public int id;
    public String receiver_name;
    public String contact_name;
    public String content;
    public String date;
    public int avatar;//头像缩略图

    public final static int EXT_TYPE_NORMAL = 0;
    public final static int EXT_TYPE_SHARE_GROUP = 1;
    public final static int EXT_TYPE_SHARE_GROUP_EVENT = 2;
    public final static int EXT_TYPE_SHARE_GROUP_BESTOW = 3;
    public final static int EXT_TYPE_GROUP_AT = 4;

    public PrivateMessage() {

    }

    public PrivateMessage(User sender, User receiver, EMMessage emMessage,String content,int type) {
        contact_name = sender.name;
        receiver_name = receiver.name;
        this.content = content;
        avatar = sender.avatar_temp;
        if (emMessage != null) {
            this.date = emMessage.getMsgTime() + "";
        }
    }

    public PrivateMessage(User sender, User receiver, long date ,String content,int type) {
        contact_name = sender.name;
        receiver_name = receiver.name;
        this.content = content;
        avatar = sender.avatar_temp;
        this.date = String.valueOf(date);
    }

    public PrivateMessage(Cursor c) {
        id = c.getInt(c.getColumnIndex("id"));
        receiver_name = c.getString(c.getColumnIndex("receiver_name"));
        contact_name = c.getString(c.getColumnIndex("sender_name"));
        content = c.getString(c.getColumnIndex("content"));
        date = c.getString(c.getColumnIndex("date"));
        avatar = c.getInt(c.getColumnIndex("avatar"));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PrivateMessage)) {
            return false;
        }
        PrivateMessage pm = (PrivateMessage) o;
        return pm.id == id;

    }

    @Override
    public String getDeletingSql() {
        return "delete from `privatemessage_v2` where id=" + id;
    }

    @Override
    public String getSavingSql() {
        return "insert into `privatemessage_v2` (`id`,`content`,`date`, `avatar`, `sender_name`,`receiver_name`) values(" + id + ",'" +
                content + "','" + date + "'," + avatar + ",'" + contact_name + "','" + PKUCarrierApplication.getUsername() + "')";
    }

    @Override
    public String getCreatingTableSql() {
        return "create table if not exists `privatemessage_v2`(" +
                "`id` INTEGER primary key autoincrement," +
                "`sender_name` varchar(255)," +
                "`receiver_name` varchar(255)," +
                "`content` text," +
                "`date` varchar(255)," +
                "`avatar` int" +
                ");";
    }

}

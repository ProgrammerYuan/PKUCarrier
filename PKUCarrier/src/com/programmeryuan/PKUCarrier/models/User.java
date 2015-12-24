package com.programmeryuan.PKUCarrier.models;

import android.accounts.Account;
import android.database.Cursor;
import android.media.Image;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.utils.image.ImageProvider;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by ProgrammerYuan on 15/11/3.
 */
public class User extends DBEntry implements Serializable{
	public String id = "1";
	public String IM_Id = "-1";
	public String sid;
	public String avatar;
	public int status;
	public String pwd,IM_pwd;
	public int avatar_temp,score;
	public boolean isCurrentUser = false;
	public String name;

	public User(){}

	public User(int avatar, String name){
		this.avatar_temp = avatar;
		this.name = name;
	}

	public User(Cursor cursor) {
		//TODO
		if(cursor == null) return;
		IM_Id = cursor.getString(cursor.getColumnIndex("IMID"));
		IM_pwd = cursor.getString(cursor.getColumnIndex("IM_pwd"));
		pwd = IM_pwd;
		id = cursor.getString(cursor.getColumnIndex("id"));
		sid = cursor.getString(cursor.getColumnIndex("sid"));
		name = cursor.getString(cursor.getColumnIndex("name"));
		status = cursor.getInt(cursor.getColumnIndex("status"));
		score = cursor.getInt(cursor.getColumnIndex("score"));
		avatar_temp = cursor.getInt(cursor.getColumnIndex("avatar"));
		isCurrentUser = cursor.getInt(cursor.getColumnIndex("current_user")) == 1;
	}

	public User(JSONObject jo) {
		//"avatar" : 3,
//		"bonus" : 0,
//				"hx_password" : "qqq",
//				"uid" : 2,
//				"hx_username" : "pkucourier_1200012864",
//				"status" : 0,
//				"email" : "1200012864@pku.edu.cn",
//				"signup_time" : "2015-12-20 15:54:27",
//				"name" : "lzy"
		if(jo == null) return;
		IM_Id = jo.optString("hx_username","");
		IM_pwd = jo.optString("hx_password","");
		pwd = IM_pwd;
		id = String.valueOf(jo.optInt("uid",0));
		sid = jo.optString("email","");
		name = jo.optString("name","");
		status = jo.optInt("status",0);
		score = jo.optInt("bonus",0);
		avatar_temp = PKUCarrierApplication.getRandomAvatarResId(jo.optInt("avatar",0));
	}

	public User(String avatar,String name,String id) {
		this.id = id;
		this.avatar = avatar;
		this.name = name;
	}

	@Override
	public String getDeletingSql() {
		return "delete from `user_v0` where id=" + id;
	}

	@Override
	public String getSavingSql() {
		return "replace into `user_v0` (`id`,`pwd`,`IMID`,`IM_pwd`,`name`,`sid`, `avatar`, `score`,`status`, `current_user`) " +
				"values('" + id + "','" + pwd + "','" + IM_Id + "','" + IM_pwd + "','" + name + "','" + sid + "'," + avatar_temp + "," + score + "," + status + "," + (isCurrentUser ? 1 : 0) +")";

	}

	@Override
	public String getCreatingTableSql() {
		return "create table if not exists `user_v0`(" +
				"`id` varchar(255)," +
				"`pwd` varchar(255)," +
				"`IMID` varchar(255)," +
				"`IM_pwd` varchar(255)," +
				"`name` varchar(255)," +
				"`sid` varchar(255)," +
				"`avatar` int default 0," +
				"`score` int default 0," +
				"`status` int default 0," +
				"`current_user` tinyint" +
				");";
	}

	public User(Account account) {

	}
}

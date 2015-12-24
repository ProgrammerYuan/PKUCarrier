package com.programmeryuan.PKUCarrier.models;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by ProgrammerYuan on 15/11/3.
 */
public class PKUCarryItem implements Serializable{

	public String id;
	public int status;
	public String title;
	public String content;
	public User uploader;
	public String detail_addr;
	public String addr;
	public String pto;
	public String code;
	public String start_time;
	public String end_time;
	public String give_time;
	public String info;
	public int type;

	public static final String[] type_name = {"取货任务","帮忙意愿"};

	public PKUCarryItem(){};

	public PKUCarryItem(User uploader, String title, String content, int type) {
		this.uploader = uploader;
		this.title = uploader.name + " " + type_name[type];
		this.content = content;
		addr = "34楼";
		detail_addr = "34楼圆通窗口";
		pto = "二教门口";
		code = "2333";
		info = title;
		give_time = "今天下午";
	}

	public PKUCarryItem(User uploader, String title, String content, int type ,String addr,String detail_addr,String code,String give_time,String pto) {
		this.uploader = uploader;
		this.title = uploader.name + " " + type_name[type];
		this.content = content;
		this.addr = addr;
		this.detail_addr = detail_addr;
		this.pto = pto;
		this.code = code;
		info = title;
		this.give_time = give_time;
	}

	public PKUCarryItem(JSONObject jo, int type) {
		if(jo == null) return;
		this.type = type;
		id = jo.optString("id","");
		status = jo.optInt("status",0);
		addr = type == 1 ? jo.optString("pfrom","") : jo.optString("approximate_fplace","");
		detail_addr = type == 1 ? jo.optString("pfrom","") : jo.optString("detailed_fplace","");
		code = jo.optString("code","");
		give_time = jo.optString("give_time","");
		pto = jo.optString("pto","");
		content = info = jo.optString("info","");
		uploader = new User(jo.optJSONObject("owner"));
		title = uploader.name + " " + type_name[type];
	}

	public String briefInfo() {
		return "取货地点:" + addr + ( type == 0 ? "\n交接时间:" + give_time : "") + "\n交接地点:" + pto + "\n附言:" + content;
	}

	public String detailedInfo() {
		return "取货地点:" + detail_addr + (type == 0 ? "\n取货码:" + code + "\n交接时间:" + give_time : "" ) + "\n交接地点:" + pto + "\n附言:" + content;
	}

}

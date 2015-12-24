package com.programmeryuan.PKUCarrier.models;

import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by ProgrammerYuan on 15/12/23.
 */
public class PKUDeal extends PKUCarryItem implements Serializable{

	public int id;
	public int status;
	public User responser;

	public PKUDeal() {
		super();
	}

	public PKUDeal(User uploader, String title, String content, int type) {
		super(uploader, title, content, type);
	}

	public PKUDeal(User uploader, User responser) {
		super();
		this.uploader = uploader;
		this.responser = responser;
	}

	public PKUDeal(JSONObject jo) {
		super(jo.optJSONObject("task"),0);
		responser = new User(jo.optJSONObject("helper"));
		id = jo.optInt("id",-1);
		status = jo.optInt("status",0);
	}

	public User getReceiver() {
		if(uploader.id.equals(PKUCarrierApplication.getUserId())) return responser;
		else return uploader;
	}
}

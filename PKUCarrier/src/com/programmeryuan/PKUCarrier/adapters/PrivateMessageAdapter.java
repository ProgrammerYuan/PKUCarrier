/**
 *
 */
package com.programmeryuan.PKUCarrier.adapters;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.easemob.chat.EMMessage;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.activities.ChatV2Activity;
import com.programmeryuan.PKUCarrier.listeners.OnCacheGeneratedListener;
import com.programmeryuan.PKUCarrier.materialdesign.widgets.MenuDialog;
import com.programmeryuan.PKUCarrier.models.PrivateMessage;
import com.programmeryuan.PKUCarrier.models.User;
import com.programmeryuan.PKUCarrier.utils.IMDB;
import com.programmeryuan.PKUCarrier.utils.Logger;
import com.programmeryuan.PKUCarrier.utils.Notifier;
import com.programmeryuan.PKUCarrier.utils.text.DateProvider;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Michael
 */
public class PrivateMessageAdapter extends CommonAdapter<PrivateMessage> {
	ChatV2Activity act;
	public MediaPlayer mp;
	public ImageView iv_playing;
	public LinkedList<String> date_visible_msgs;

	public PrivateMessageAdapter(Context context, List<PrivateMessage> list, int layout_id2) {
		super(context, list, layout_id2);
		act = (ChatV2Activity) context;
		mp = new MediaPlayer();
		date_visible_msgs = new LinkedList<>();
		init();

	}


	public PrivateMessageAdapter(Context context, List<PrivateMessage> list) {
		this(context, list, R.layout.item_chat);

	}

	public boolean is_self(PrivateMessage cm) {
		return (cm.contact_name + "").equals(PKUCarrierApplication.getUsername());
	}

	void init() {
		l = new OnCacheGeneratedListener<PrivateMessage>() {

			@Override
			public void onCacheGenerated(CommonAdapterViewCache c, final PrivateMessage t) {
				c.setViewValue(TextView.class, DateProvider.getDate(DateProvider.format_datetime_nosecond, Long.parseLong(t.date)), R.id.item_chat_date);
				if (date_visible_msgs != null) {
					c.getView(R.id.item_chat_date).setVisibility(date_visible_msgs.contains(t.id + "") ? View.VISIBLE : View.GONE);
				}
				c.getView(R.id.item_chat_msg_container).setVisibility(View.GONE);
				if (!is_self(t)) {//别人发的
					c.setViewValue(ImageView.class, R.color.trans, R.id.item_chat_avatar_self);
					c.setViewValue(ImageView.class, t.avatar, R.id.item_chat_avatar_friend);
					c.getView(R.id.item_chat_avatar_friend).setVisibility(View.INVISIBLE);
					c.getView(R.id.item_chat_info_friend_identity).setVisibility(View.GONE);
					c.setViewValue(TextView.class, t.contact_name, R.id.item_chat_info_friend_name);
				} else {
					c.setViewValue(ImageView.class, R.color.trans, R.id.item_chat_avatar_friend);
					c.getView(R.id.item_chat_avatar_friend).setVisibility(View.INVISIBLE);
					c.setViewValue(ImageView.class, PKUCarrierApplication.getUserAvatar(), R.id.item_chat_avatar_self);
				}
				switchTo("text", c, t);
			}
		};
	}

	void setupText(CommonAdapterViewCache c, final PrivateMessage t) {
			c.setViewValue(TextView.class, t.content, is_self(t) ? R.id.item_chat_content_self : R.id.item_chat_content_friend);
			c.getView(is_self(t) ? R.id.item_chat_content_self : R.id.item_chat_content_friend).setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {

					String content = t.content;
					if (content.length() > 10) {
						content = content.substring(0, 10) + "...";
					}
					final String finalContent = content;
					MenuDialog dialog = new MenuDialog(act, content, new String[]{"复制"}, R.layout.item_menu, R.id.item_menu_tv, new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							if (position == 0) {
								android.content.ClipboardManager clipboard = (android.content.ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE);
								ClipData clip = ClipData.newPlainText(finalContent, t.content);
								clipboard.setPrimaryClip(clip);
								Notifier.showNormalMsg(act, "已复制到剪贴板");
							}
						}
					});
					dialog.show();
					return true;
				}
			});
	}

	void switchTo(String type, CommonAdapterViewCache c, final PrivateMessage t) {
		c.getView(R.id.item_chat_info_friend).setVisibility(View.GONE);
//                c.getView(R.id.item_chat_avatar_friend).setVisibility(View.GONE);
//                c.getView(R.id.item_chat_avatar_self).setVisibility(View.GONE);
		c.getView(R.id.item_chat_voice_friend).setVisibility(View.GONE);
		c.getView(R.id.item_chat_voice_self).setVisibility(View.GONE);
		c.getView(R.id.item_chat_image_self_layout).setVisibility(View.GONE);
		c.getView(R.id.item_chat_image_friend).setVisibility(View.GONE);
		c.getView(R.id.item_chat_content_self_layout).setVisibility(View.GONE);
		c.getView(R.id.item_chat_content_friend).setVisibility(View.GONE);
		c.getView(R.id.item_chat_share_friend).setVisibility(View.GONE);
		c.getView(R.id.item_chat_share_self_layout).setVisibility(View.GONE);
		c.getView(R.id.item_chat_share_group_event_friend).setVisibility(View.GONE);
		c.getView(R.id.item_chat_share_group_event_self_layout).setVisibility(View.GONE);
		c.getView(R.id.item_chat_bestow_friend).setVisibility(View.GONE);
		c.getView(R.id.item_chat_bestow_self_layout).setVisibility(View.GONE);
		c.getView(R.id.item_chat_notify).setVisibility(View.GONE);
		c.getView(R.id.item_chat_voice_friend_reddot).setVisibility(View.GONE);
		c.getView(R.id.item_chat_msg_container).setVisibility(View.GONE);
		ImageView iv_resend = null;
		if (type.equals("text")) {
			c.getView(R.id.item_chat_msg_container).setVisibility(View.VISIBLE);
			c.getView(R.id.item_chat_avatar_friend).setVisibility(is_self(t) ? View.INVISIBLE : View.VISIBLE);
			c.getView(R.id.item_chat_avatar_self).setVisibility(is_self(t) ? View.VISIBLE : View.INVISIBLE);
			c.getView(R.id.item_chat_info_friend).setVisibility(View.GONE);
			c.getView(R.id.item_chat_content_self_layout).setVisibility(is_self(t) ? View.VISIBLE : View.GONE);
			c.getView(R.id.item_chat_content_friend).setVisibility(is_self(t) ? View.GONE : View.VISIBLE);
			c.getView(R.id.item_chat_text_sending).setVisibility(View.GONE);
			iv_resend = (ImageView) c.getView(R.id.item_chat_text_resend);
			iv_resend.setVisibility(View.GONE);
			setupText(c, t);
		}
	}
}

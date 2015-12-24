package com.programmeryuan.PKUCarrier.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.easemob.chat.EMMessage;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.models.DBEntry;
import com.programmeryuan.PKUCarrier.models.PrivateMessage;
import com.programmeryuan.PKUCarrier.models.User;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Michael on 2014/9/25.
 */
public class IMDB {


	//    static DbUtils db;
	static SQLiteDatabase db;

	static DBHelper helper;

	static void saveEntry(DBEntry e) {
		try {
			String sql = e.getSavingSql().replace("'null'", "null");
			Logger.out(sql);
			getDb().execSQL(sql);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public static void releaseDB() {
		if (db != null) {
			db.close();
			db = null;
		}
		if (helper != null) {
			helper.close();
			helper = null;
		}

	}

	public static SQLiteDatabase getDb() {
		if (db == null) {
			if (helper == null) {
				helper = new DBHelper(PKUCarrierApplication.instance);
			}
			db = helper.getWritableDatabase();
		}
		return db;
	}
//    static void check() {
//        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' ", null);
//        if (c != null) {
//            c.moveToFirst();
//        }
//        while (c.moveToNext()) {
//            Logger.out(c.getString(c.getColumnIndex("name")));
//        }
//    }

	public static void init(Context context) {
//        helper = new DBHelper(context);

//        db = helper.getWritableDatabase();
		db = getDb();
		for (String s : helper.database_create) {
			db.execSQL(s);
		}
		Cursor cursor = db.rawQuery("select tbl_name from sqlite_master ", null);
		int rows_num = cursor.getCount();    //取得資料表列數

		if (rows_num != 0) {
			cursor.moveToFirst();            //將指標移至第一筆資料
			for (int i = 0; i < rows_num; i++) {
				String name = cursor.getString(cursor.getColumnIndex("tbl_name"));
				Logger.out(name);
				cursor.moveToNext();        //將指標移至下一筆資料
			}
		}
		cursor.close();
		if (!PKUCarrierApplication.db_inited)
			PKUCarrierApplication.db_inited = true;
		Logger.out("dbHelper.database_create:" + helper.database_create);
//        check();
	}

	public static void updateCurrentUser(User user) {
		saveEntry(user);
	}

	public static User getCurrentUser() {
		String sql = "select * from `user_v0` where `current_user` = 1";
		Cursor cursor = getDb().rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return new User(cursor);
		}
		return null;
	}

	public static void logout(String user_id) {
		ContentValues values = new ContentValues();
		values.put("current_user", 0);
		getDb().update("user_v0", values, "id = " + user_id, null);
	}

	public static void delete(DBEntry e) {
		getDb().execSQL(e.getDeletingSql());
	}

	static Cursor query(String table, String[] cols, String where, String[] where_args, String orderby, String limit) {
		Cursor mCursor = getDb().query(true, table, cols, where
				, where_args, null, null, orderby, limit);
		return mCursor; // iterate to get each value.
	}

	public static void updateLastMessage(int sender, int receiver, String last_message_content, int last_msg_id, String date) {
		try {
			String sql = "update `privateconversation` set `unread_num` = 0 , `last_msg_id`=" + last_msg_id + ", `content`='" + last_message_content + "', date = '" + date + "' where " + "(receiver=" + receiver + " and sender=" + sender + ")";
			Logger.out(sql);
			getDb().execSQL(sql);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public static User findUserByIMID(String im_id) {
		User ret = null;
		Cursor cursor = query("user_v2", null, "(IM_id = '" + im_id + "')", null, null, 0 + "," + 1);
		int rows = cursor.getCount();
		if (rows == 0) {
			cursor.close();
			return null;
		} else {
			cursor.moveToFirst();
			ret = new User(cursor);
			cursor.close();
			return ret;
		}
	}

	public static String findIMIDByUser(String id) {
		Cursor cursor = query("user_v2", new String[]{"IM_id"}, "id = ?", new String[]{id}, null, null);
		int rows = cursor.getCount();
		if (rows == 0) {
			cursor.close();
			return null;
		} else {
			cursor.moveToFirst();
			String IM_id = cursor.getString(cursor.getColumnIndex("IM_id"));
			cursor.close();
			return IM_id;
		}
	}

	public static User findContactById(String id) {
		Cursor cursor = query("user_v2", null, "(id = " + id + ")", null, null, null);
		int rows = cursor.getCount();
		if (rows == 0) {
			cursor.close();
			return null;
		} else {
			cursor.moveToFirst();
			User user = new User(cursor);
			cursor.close();
			return user;
		}
	}

	public static void clearInvalidMessages() {
		try {
			String sql = "delete from `privatemessage_v2` where `content` like '%-来自新版榜样App'";
			Logger.out(sql);
			getDb().execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deletePrivateMessageByGroupId(String group_id) {
		getDb().delete("privatemessage_v2", "`group_id`=" + group_id, null);
	}

	public static void deletePrivateConversationByGroupId(String group_id) {
		int id = Integer.parseInt(PKUCarrierApplication.getUserId());
		String sql = "delete from privateconversation where group_id='" + group_id + "'";
		getDb().execSQL(sql);
		deletePrivateMessageByGroupId(group_id);
	}

//    public static void deletePrivateConversationByGroupNumber(String group_number) {
//        int id = Integer.parseInt(PKUCarrierApplication.getUserId());
//        String sql = "delete from privateconversation where group_number='" + group_number + "' and (sender=" + id + " or receiver=" + id + ")";
//        getDb().execSQL(sql);
//    }

	public static int savePrivateMessage(PrivateMessage msg) {
		ContentValues values = new ContentValues();
		values.put("sender_name", msg.contact_name);
		values.put("receiver_name", msg.receiver_name);
		values.put("content", msg.content);
		values.put("date", msg.date);
		values.put("avatar", msg.avatar);
		return (int) getDb().insert("privatemessage_v2", null, values);
	}

	public static void updatePrivateMessageStatus(int id, int msg_status) {
		String sql = "update privatemessage_v2 set msg_status = '" + msg_status + "' where id = " + id;
		getDb().execSQL(sql);
	}

	public static void deletePrivateMessageByConversationId(int conversation_id) {
		String sql = "delete from `privatemessage_v2` where `conversation_id`=" + conversation_id;
		getDb().execSQL(sql);
	}

	public static void insertContact(User user) {
		ContentValues values = new ContentValues();
		values.put("id", user.id);
		values.put("IM_id", user.IM_Id);
		values.put("name", user.name);
		values.put("avatar", user.avatar);
		values.put("status", 0);
		values.put("info", user.name);
		getDb().insert("user_v2", null, values);
	}

//    public static User searchContact(User user) {
//        Cursor cursor = query("user_v2", null, "id=" + user.id, null, null, null);
//        int rows = cursor.getCount();
//        if (rows == 0) {
//            cursor.close();
//            return null;
//        } else {
//            cursor.moveToFirst();
//            User u = new User(cursor);
//            return u;
//        }
//    }

	public static void saveContact(User user) {
		User u = findContactById(user.id);
		if (u == null) {
			insertContact(user);
		} else {
			updateContact(user);
		}
	}

	public static void updateContact(User user) {
		ContentValues values = new ContentValues();
		values.put("IM_id", user.IM_Id);
		values.put("name", user.name);
		values.put("avatar", user.avatar);
		values.put("status", 0);
		values.put("info", user.name);
		getDb().update("user_v2", values, "id='" + user.id + "'", null);

//        String sql = "update `user_v2` set IM_id='" + user.IM_id + "', name='" + user.name + "', avatar='" + user.avatar.thumbnail + "', status=" + user.status + " where id='" + user.id + "'";
//        getDb().execSQL(sql);
	}

	public static void updateContactForbid(User user, int forbid) {
		String sql = "update `user_v2` set forbid=" + forbid + " where id='" + user.id + "'";
		getDb().execSQL(sql);
	}
//    public static void saveContact(User user) {
//        User test = searchContact(user);
//        if (test != null) {
//            test.forbid = user.forbid;
////            user.is_top = test.is_top;
//            saveEntry(test);
//        } else {
//            test.forbid = 0;
////            user.is_top = 0;
//            saveEntry(test);
//        }
//    }

//    public static User getContact(String id) {
//        User user = null;
//        Cursor cursor = query("user", null, "id='" + id + "' or voip_id='" + id + "'", null, null, null);
//        int rows_num = cursor.getCount();    //取得資料表列數
//
//        if (rows_num != 0) {
//            cursor.moveToFirst();            //將指標移至第一筆資料
//            for (int i = 0; i < rows_num; i++) {
//                user = new User(cursor);
//                cursor.moveToNext();        //將指標移至下一筆資料
//            }
//        }
//        cursor.close();
//        return user;
//    }

//    public static void updateRecentContacts(User contact, ChatMessage msg) {
//        try {
//            contact.last_chat_date = Long.parseLong(msg.date);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        contact.last_chat_msg = msg;
//        Logger.out("updateRecentContacts:" + contact);
//        IMDB.saveContact(contact);
//    }

//    public static ArrayList<User> getRecentContacts() {
//        ArrayList<User> users = new ArrayList<User>();
//        Cursor cursor = query("user", null, " cur_user='" + PKUCarrierApplication.user_id + "'", null, "date DESC", null);
//        int rows_num = cursor.getCount();    //取得資料表列數
//
//        if (rows_num != 0) {
//            cursor.moveToFirst();            //將指標移至第一筆資料
//            for (int i = 0; i < rows_num; i++) {
//                User u = new User(cursor);
//                users.add(u);
//                cursor.moveToNext();        //將指標移至下一筆資料
//            }
//            for (int i = 0; i < PKUCarrierApplication.top_contacts.size(); i++) {
//                String id = PKUCarrierApplication.top_contacts.get(i);
//                for (int j = 0; j < users.size(); j++) {
//                    User u = users.get(j);
//                    if (!u.id.equals(id)) {
//                        continue;
//                    }
//                    u.is_set_top = true;
//                    users.remove(u);
//                    users.add(i, u);
//                }
//            }
//
//        }
//        cursor.close();
//        Logger.out(users);
//        return users;
//    }

//    public static ChatMessage getLastMessage(String user_voip_id) {
//        ArrayList<ChatMessage> msgs = new ArrayList<ChatMessage>();
//        Cursor cursor = query("chatmessage", null, " (sender='" + PKUCarrierApplication.user_im_voip_id + "' and receiver='" + user_voip_id + "') or (sender='" + user_voip_id + "' and receiver='" + PKUCarrierApplication.user_im_voip_id + "')", null, "date DESC", "0,1");
//        int rows_num = cursor.getCount();
//
//        if (rows_num != 0) {
//            cursor.moveToFirst();
//            for (int i = 0; i < rows_num; i++) {
//                ChatMessage cm = new ChatMessage(cursor);
//                msgs.add(cm);
//                cursor.moveToNext();
//            }
//        }
//        cursor.close();
//        if (msgs.size() != 0) {
//            return msgs.get(0);
//        }
//        return null;
//
//    }

	public static ArrayList<PrivateMessage> getPrivateMessages(int sender, int receiver, int limit, int offset) {
		ArrayList<PrivateMessage> msgs = new ArrayList<PrivateMessage>();
		Cursor cursor = query("privatemessage", null, " (sender=" + sender + " and receiver=" + receiver + ") or (sender=" + receiver + " and receiver=" + sender + ")", null, "date DESC", offset + "," + limit);
		int rows_num = cursor.getCount();    //取得資料表列數

		if (rows_num != 0) {
			cursor.moveToFirst();            //將指標移至第一筆資料
			for (int i = 0; i < rows_num; i++) {
				PrivateMessage cm = new PrivateMessage(cursor);
				msgs.add(cm);
				cursor.moveToNext();        //將指標移至下一筆資料
			}
		}
		cursor.close();
//        Collections.reverse(msgs);
		return msgs;
	}

	public static ArrayList<PrivateMessage> getNewPrivateMessages(int sender, int receiver, int last_id) {
		ArrayList<PrivateMessage> msgs = new ArrayList<PrivateMessage>();
		Cursor cursor = query("privatemessage", null, " ((sender=" + sender + " and receiver=" + receiver + ") or (sender=" + receiver + " and receiver=" + sender + "))" + "and ( id > " + last_id + " )", null, "date DESC", null);
		int rows_num = cursor.getCount();    //取得資料表列數

		if (rows_num != 0) {
			cursor.moveToFirst();            //將指標移至第一筆資料
			for (int i = 0; i < rows_num; i++) {
				PrivateMessage cm = new PrivateMessage(cursor);
				msgs.add(cm);
				cursor.moveToNext();        //將指標移至下一筆資料
			}
		}
		cursor.close();
//        Collections.reverse(msgs);
		return msgs;
	}

	public static boolean hasPrivateMessage(int id) {
		Cursor cursor = query("privatemessage", null, " id=" + id, null, null, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			return false;
		} else {
			cursor.moveToFirst();            //將指標移至第一筆資料
			for (int i = 0; i < count; i++) {
				try {
					int id1 = cursor.getInt(cursor.getColumnIndex("id"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				cursor.moveToNext();        //將指標移至下一筆資料
			}
		}
		cursor.close();
		return count != 0;
	}

	public static int getLastPrivateMessageId(int sender, int receiver) {
		Cursor cursor = query("privatemessage", new String[]{"max(id)"}, " (sender=" + sender + " and receiver=" + receiver + ") or (sender=" + receiver + " and receiver=" + sender + ")", null, null, null);
		int max = 0;
		int rows_num = cursor.getCount();    //取得資料表列數

		if (rows_num != 0) {
			cursor.moveToFirst();            //將指標移至第一筆資料
			for (int i = 0; i < rows_num; i++) {
				try {
					max = cursor.getInt(cursor.getColumnIndex("max(id)"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				cursor.moveToNext();        //將指標移至下一筆資料
			}
		}
		cursor.close();
		return max;
	}

	public static PrivateMessage getLastPrivateMessage(int sender, int receiver) {
		Cursor cursor = query("privatemessage", null, " (sender=" + sender + " and receiver=" + receiver + ") or (sender=" + receiver + " and receiver=" + sender + ")", null, null, null);
		int row_count = cursor.getCount();
		PrivateMessage privateMessage = null;
		if (row_count == 0) {
			return null;
		} else {
			try {
				cursor.moveToFirst();
				for (int i = 0; i < row_count; i++) {
					privateMessage = new PrivateMessage(cursor);
					cursor.moveToNext();        //將指標移至下一筆資料
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return privateMessage;
	}

	public static int getLastPushMessageId(String types) {
		String s = "type in (" + types + ")";
		Cursor cursor = query("pushmessage", new String[]{"max(id)"}, s, null, null, null);
		int max = 0;
		int rows_num = cursor.getCount();    //取得資料表列數

		if (rows_num != 0) {
			cursor.moveToFirst();            //將指標移至第一筆資料
			for (int i = 0; i < rows_num; i++) {
				try {
					max = cursor.getInt(cursor.getColumnIndex("max(id)"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				cursor.moveToNext();        //將指標移至下一筆資料
			}
		}
		cursor.close();
		return max;
	}

    /*public static ArrayList<ChatMessage> getMessages(String sender_voip_id, String receiver_voip_id, int limit, int offset) {
		ArrayList<ChatMessage> msgs = new ArrayList<ChatMessage>();
        Cursor cursor = query("chatmessage", null, " (sender='" + sender_voip_id + "' and receiver='" + receiver_voip_id + "') or (sender='" + receiver_voip_id + "' and receiver='" + sender_voip_id + "')", null, "date DESC", offset + "," + limit);
        int rows_num = cursor.getCount();    //取得資料表列數

        if (rows_num != 0) {
            cursor.moveToFirst();            //將指標移至第一筆資料
            for (int i = 0; i < rows_num; i++) {
                ChatMessage cm = new ChatMessage(cursor);
                msgs.add(cm);
                cursor.moveToNext();        //將指標移至下一筆資料
            }
        }
        cursor.close();
        Collections.reverse(msgs);
        return msgs;
    }*/

	public static ArrayList<PrivateMessage> getLatestMessages(String sender_name,String receiver_name, int last_msg_id) {
		ArrayList<PrivateMessage> ret = new ArrayList<>();
		Cursor cursor = query("privatemessage_v2", null,"(`sender_name`= '" + sender_name + "' and `receiver_name` = '" + receiver_name +
				"') or (`sender_name` = '" + receiver_name + "' and `receiver_name` = '" + sender_name + "')" + " and id > " + last_msg_id, null, "date DESC", null);
		int rows = cursor.getCount();
		if (rows > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < rows; i++) {
				PrivateMessage pm = new PrivateMessage(cursor);
				ret.add(pm);
				cursor.moveToNext();
			}
		}
		Collections.reverse(ret);
		cursor.close();
		return ret;
	}

	public static ArrayList<PrivateMessage> getMessages(String sender_name, String receiver_name,int offset,int limit) {
		ArrayList<PrivateMessage> msgs = new ArrayList<>();
		Cursor cursor = query("privatemessage_v2", null,
				"(`sender_name`= '" + sender_name + "' and `receiver_name` = '" + receiver_name +
				"') or (`sender_name` = '" + receiver_name + "' and `receiver_name` = '" + sender_name + "')", null, "date DESC", offset + "," + limit);
		int rows = cursor.getCount();
		if (rows > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < rows; i++) {
				PrivateMessage privateMessage = new PrivateMessage(cursor);
				msgs.add(privateMessage);
				cursor.moveToNext();
			}
		}
		Collections.reverse(msgs);
		cursor.close();
		return msgs;
	}

//    public static ArrayList<PrivateMessage> getMessages(String sender, String reciever, int limit, int offset) {
//        ArrayList<PrivateMessage> msgs = new ArrayList<>();
//        Cursor cursor = query("privatemessage_v2", null, "((sender='" + sender + "' and receiver='" + reciever + "') or (sender='" + reciever + "' and receiver='" + sender + "')) and chat_type=" + EMMessage.ChatType.Chat.ordinal(), null, "date DESC", offset + "," + limit);
//        int rows = cursor.getCount();
//        if (rows > 0) {
//            cursor.moveToFirst();
//            for (int i = 0; i < rows; i++) {
//                PrivateMessage privateMessage = new PrivateMessage(cursor);
//                msgs.add(privateMessage);
//                cursor.moveToNext();
//            }
//        }
//        Collections.reverse(msgs);
//        cursor.close();
//        return msgs;
//    }
//
//    public static ArrayList<PrivateMessage> getGroupMessage(String group_id, int limit, int offset) {
//        String user_id = PKUCarrierApplication.getUserId();
//        ArrayList<PrivateMessage> msgs = new ArrayList<>();
//        Cursor cursor = query("privatemessage_v2", null, "group_id='" + group_id + "' and (receiver='" + user_id + "' or sender='" + user_id + "')", null, "date DESC", offset + "," + limit);
//        int rows = cursor.getCount();
//        if (rows > 0) {
//            cursor.moveToFirst();
//            for (int i = 0; i < rows; i++) {
//                PrivateMessage privateMessage = new PrivateMessage(cursor);
//                msgs.add(privateMessage);
//                cursor.moveToNext();
//            }
//        }
//        cursor.close();
//        Collections.reverse(msgs);
//        return msgs;
//    }

	public static ArrayList<String> getImagesFromMessage(int conversation_id) {
		ArrayList<String> urls = new ArrayList<>();
		Cursor cursor = query("privatemessage_v2", new String[]{"file_url", "file_local"}, "`conversation_id`=" + conversation_id, null, "date ASC", null);
		int rows = cursor.getCount();
		if (rows > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < rows; i++) {
				String file_url = cursor.getString(cursor.getColumnIndex("file_url"));
				String file_local = cursor.getString(cursor.getColumnIndex("file_local"));
				if (!TextUtils.isEmpty(file_url)) {
					urls.add(file_url);
				} else if (!TextUtils.isEmpty(file_local)) {
					urls.add("file://" + file_local);
				}
				cursor.moveToNext();
			}
		}
		cursor.close();
		return urls;
	}

//    public static ArrayList<String> getImagesFromMessage(String group_id) {
//        ArrayList<String> urls = new ArrayList<>();
//        Cursor cursor = query("privatemessage_v2", new String[]{"file_url", "file_local"}, "group_id='" + group_id + "' and (receiver='" + PKUCarrierApplication.getUserId() + "' or sender='" + PKUCarrierApplication.getUserId() + "') and type=" + EMMessage.Type.IMAGE.ordinal(), null, "date ASC", null);
//        int rows = cursor.getCount();
//        if (rows > 0) {
//            cursor.moveToFirst();
//            for (int i = 0; i < rows; i++) {
//                String file_url = cursor.getString(cursor.getColumnIndex("file_url"));
//                String file_local = cursor.getString(cursor.getColumnIndex("file_local"));
//                if (!TextUtils.isEmpty(file_url)) {
//                    urls.add(file_url);
//                } else if (!TextUtils.isEmpty(file_local)) {
//                    urls.add("file://" + file_local);
//                }
//                cursor.moveToNext();
//            }
//        }
//        cursor.close();
//        return urls;
//    }
//
//    public static ArrayList<String> getImagesFromGroupMessage(String group_id) {
//        ArrayList<String> urls = new ArrayList<>();
//        Cursor cursor = query("privatemessage_v2", null, "group_id='" + group_id + "' and (receiver='" + PKUCarrierApplication.getUserId() + "' or sender='" + PKUCarrierApplication.getUserId() + "')", null, "date ASC", null);
//        int rows = cursor.getCount();
//        if (rows > 0) {
//            cursor.moveToFirst();
//            for (int i = 0; i < rows; i++) {
//                String file_url = cursor.getString(cursor.getColumnIndex("file_url"));
//                String file_local = cursor.getString(cursor.getColumnIndex("file_local"));
//                if (!TextUtils.isEmpty(file_url)) {
//                    urls.add(file_url);
//                } else if (!TextUtils.isEmpty(file_local)) {
//                    urls.add("file://" + file_local);
//                }
//                cursor.moveToNext();
//            }
//        }
//        cursor.close();
//        return urls;
//    }

}


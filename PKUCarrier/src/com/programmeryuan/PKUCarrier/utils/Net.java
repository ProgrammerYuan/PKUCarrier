package com.programmeryuan.PKUCarrier.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

public class Net {
    public static String cookie_name = "X-CSRFtoken";
    public static String cookie_value = null;

    public static String root = "http://www.wyxpku.com";

    public static String url_usr_signup = root + "/user/signup/";
    public static String url_usr_login = root + "/user/login/";
    public static String url_task_new = root + "/task/new/";
    public static String url_task_list = root + "/task/all/";
    public static String url_task_user_list = root + "/task/user/{user_id}/";
    public static String url_task_info = root + "/task/view/";
    public static String url_task_detail_info = root + "/task/view_detail/";
    public static String url_task_respond = root + "/task/resp/";
    public static String url_task_delete = root + "/task/delete/";
    public static String url_will_new = root + "/will/new/";
    public static String url_will_list = root + "/will/all/";
    public static String url_will_info = root + "/will/view/";
    public static String url_will_respond = root + "/user/message/";
    public static String url_will_delete = root + "/will/delete/";
    public static String url_deal_user_list = root + "/deal/user/{user_id}/";
    public static String url_deal_complete = root + "/deal/complete/";

    public static HttpUtils client;
    static ConnectivityManager cm;

    public static void init() {

    }

    /**
     * 统一的错误提示信息
     */
    public static void handleErrorCode(Context c, int status, String ret, JSONObject request_param) {
//        switch(status){
//            case 20110:
//            Notifier.showNormalMsg(c, "你的账号已被封禁，如有任何问题请联系客服！");
//                break;
//            case 20501:
//                Notifier.showNormalMsg(c, "群组不存在");
//                break;
//            case 20502:
//                Notifier.showNormalMsg(c, "群组已经加入");
//                break;
//            case 20503:
//                Notifier.showNormalMsg(c,"你尚未加入此群组");
//                break;
//            case 20504:
//                Notifier.showNormalMsg(c, "你没有权限执行此操作");
//                break;
//            case 20510:
//                Notifier.showNormalMsg(c, "遇到未知错误");
//                break;
//            case 20523:
//                Notifier.showNormalMsg(c, "申请已经处理完毕");
//                break;
//            default:
//                Notifier.showNormalMsg(c, "网络不给力呀！");
//                break;
//        }
        Notifier.showNormalMsg(c,ret);
        JSONArray names = request_param.names();
        if (names != null) {
            for (int i = 0; i < names.length(); i++) {
                String name = names.optString(i);
                if (name.contains("pass")) {
                    request_param.remove(name);
                }
            }
        }
    }

    public static HttpUtils getClient(Context c) {
        if (client == null) {

            client = new HttpUtils();
        }

        if (getConnectionState(c) == 1) {
            client.configTimeout(10000);
        } else if (getConnectionState(c) == 0) {
            client.configTimeout(30000);
        } else if (getConnectionState(c) == -1) {
            // 没网
        }
        return client;
    }

    /**
     * 获取网络环境状态
     *
     * @return 1：Wifi，0：2G网 -1：未知
     */
    public static int getConnectionState(Context c) {
        if (cm == null) {
            cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        NetworkInfo activeNetInfo = null;
        try {

            activeNetInfo = cm.getActiveNetworkInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return 1;
            } else if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

//    static synchronized void saveLastParam(Map<String, String> map) {
//        last_param.clear();
//        last_param.putAll(map);
//    }
//
//    static synchronized void saveLastParam(String key, String value) {
//        last_param.clear();
//        last_param.put(key, value);
//    }
//
//    static synchronized void saveLastParam() {
//        last_param.clear();
//    }

    public static RequestParams getParam() {
//        saveLastParam();
        RequestParams p = new RequestParams("utf-8");
        setCookie(p);
        return p;
    }

    public static RequestParams getParam(HttpMethod method, String key, String value) {
//        saveLastParam(key, value);
        Logger.out("request parameters:" + key + "->" + value);
        RequestParams p = new RequestParams("utf-8");
        setCookie(p);
        if (method == HttpMethod.GET) {
            p.addQueryStringParameter(key, value);
        } else if (method == HttpMethod.POST) {
            p.addBodyParameter(key, value);
        }

        return p;
    }

    public static RequestParams getParam(HttpMethod method, Map<String, String> map) {
//        saveLastParam(map);
        Logger.out("request parameters:" + map);
        RequestParams p = new RequestParams("utf-8");
        setCookie(p);
        for (Entry<String, String> en : map.entrySet()) {
            if (method == HttpMethod.GET) {
                p.addQueryStringParameter(en.getKey(), en.getValue());
            } else if (method == HttpMethod.POST) {
                String value = en.getValue();
                if (value != null && value.startsWith("[!file]")) {
                    p.addBodyParameter(en.getKey(), new File(value.replace("[!file]", "")));
                } else {
                    p.addBodyParameter(en.getKey(), value);
                }

            }

        }

        return p;
    }

    public static void setCookie(RequestParams p) {
        // Logger.out(cookie_value);
        if (cookie_value != null) {
            p.addHeader(cookie_name, cookie_value);
        } else {
            cookie_value = null;
            if (cookie_value != null) {
                p.addHeader(cookie_name, cookie_value);
            }
        }
        p.addHeader("Accept", "application/vnd.bangyang.v5+json");
    }

    public static void handleErrorCode(String s, Context context) {
        Notifier.showNormalMsg(context,s);
    }
}

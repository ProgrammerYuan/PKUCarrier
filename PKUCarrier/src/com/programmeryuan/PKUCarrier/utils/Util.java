/**
 *
 */
package com.programmeryuan.PKUCarrier.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.activities.AngelActionBar;
import com.programmeryuan.PKUCarrier.activities.AngelActivity;

import java.io.*;
import java.lang.reflect.Field;

/**
 * 一些实用方法
 *
 * @author Michael
 */
public class Util {
    public static Context c;
    public final static String namespace_android = "http://schemas.android.com/apk/res/android";
    private static PowerManager.WakeLock wakeLock = null;

    /**
     * 隐藏软键盘
     *
     * @param v 正在输入内容（调用软键盘）的控件
     * @param c 上下文
     */
    public static void hideInputBoard(View v, Context c) {

        if (c instanceof Activity) {
            ((Activity) c).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        } else {
            InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }


    public static int createDarkerColor(int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        if (r + g + b == 0) {
            return Color.argb(Math.min(a + 15, 255), 0, 0, 0);
        }
//        a = (int) (a * 7 / 8.0);
        r = (int) (r * 7 / 8.0);
        g = (int) (g * 7 / 8.0);
        b = (int) (b * 7 / 8.0);
        return Color.rgb(r, g, b);
    }

    /**
     * 计算两点之间距离
     *
     * @param a 点A
     * @param b 点B
     * @return AB间距离
     */
    public static float getDistance(Point a, Point b) {
        return (float) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    /**
     * 隐藏软键盘
     *
     * @param a 正在显示软键盘的界面
     *          有可能没效果！
     */
    public static void hideInputBoard(Activity a) {
        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = a.getCurrentFocus();
        if (v != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     *
     * @param target 需要输入内容的控件
     * @param a      正在显示软键盘的界面
     */
    public static void showInputBoard(View target, Activity a) {
        InputMethodManager keyboard = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(target, InputMethodManager.SHOW_FORCED);
    }

//    public static void setupActionBar(AngelActivity act, String title) {
//        setupActionBar(act, title, null);
//    }
//
//    public static void setupActionBar(AngelActivity act, String title, String left) {
//        ActionBar bar = act.getActionBar();
//        if (bar == null) {
//            return;
//        }
//        bar.setIcon(c.getResources().getDrawable(R.color.trans));
//        bar.setDisplayHomeAsUpEnabled(false);
//        bar.setDisplayShowCustomEnabled(true);
//        bar.setDisplayShowHomeEnabled(false);
//        bar.setTitle("");
//        AngelActionBar aab = act.getAngelActionBar();
//        if (aab == null) {
//            aab = new AngelActionBar(act);
//            act.setAngelActionBar(aab);
//        }
//        bar.setCustomView(aab);
//        aab.setTitleText(title);
//        aab.setLeftText(left);
//    }

    /**
     * 将sp转换为px
     *
     * @param spValue sp值
     * @return 相应的px
     */
    public static int getPXfromSP(float spValue) {
        float fontScale = c.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);

    }

    /**
     * 将dp转换为px
     *
     * @param dipValue dp值
     * @return 相应的px
     */
    public static int getPX(float dipValue) {
        final float scale = c.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px转换为dip
     *
     * @param pxValue px值
     * @return 相应的dp
     */
    public static int getDP(float pxValue) {
        final float scale = c.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getReversedColor(int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(a, 255 - r, 255 - g, 255 - b);
    }

    /**
     * 为指定的EditText设置输入限制，并把提示信息显示到指定的TextView
     *
     * @param et  要设置输入限制的EditText
     * @param tv  用来显示提示信息的TextView
     * @param max 最大输入长度
     */
    public static void setInputLimit(final EditText et, final TextView tv, final int max) {
        int l = et.getText().toString().length();
        if (l <= max) {
            tv.setText("还可以输入" + (max - l) + "字");
            tv.setTextColor(et.getContext().getResources().getColor(R.color.black));
        } else {
            tv.setText("已超出" + (l - max) + "字");
            tv.setTextColor(et.getContext().getResources().getColor(R.color.red));
        }
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int l = s.length();
                if (l <= max) {
                    tv.setText("还可以输入" + (max - l) + "字");
                    tv.setTextColor(et.getContext().getResources().getColor(R.color.black));
                } else {
                    tv.setText("已超出" + (l - max) + "字");
                    tv.setTextColor(et.getContext().getResources().getColor(R.color.red));
                }
            }
        });
    }

    public static void setRemainInputLimit(final EditText et, final TextView tv, final int max,final String template) {
        int l = et.getText().toString().length();
        tv.setText(template.replace("count", String.valueOf(max - l)));
        if (l <= max) {
            tv.setTextColor(et.getContext().getResources().getColor(R.color.black));
        } else {
            tv.setTextColor(et.getContext().getResources().getColor(R.color.red));
        }
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int l = s.length();
                tv.setText(template.replace("count", String.valueOf(max - l)));
                if (l <= max) {
                    tv.setTextColor(et.getContext().getResources().getColor(R.color.black));
                } else {
                    tv.setTextColor(et.getContext().getResources().getColor(R.color.red));
                }
            }
        });
    }

    public static void setInputLimit(final EditText et, final TextView tv, final int max,final String template) {
        int l = et.getText().toString().length();
        tv.setText(template.replace("count", String.valueOf(l)));
        if (l <= max) {
            tv.setTextColor(et.getContext().getResources().getColor(R.color.black));
        } else {
            tv.setTextColor(et.getContext().getResources().getColor(R.color.red));
        }
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int l = s.length();
                tv.setText(template.replace("count", String.valueOf(l)));
                if (l <= max) {
                    tv.setTextColor(et.getContext().getResources().getColor(R.color.black));
                } else {
                    tv.setTextColor(et.getContext().getResources().getColor(R.color.red));
                }
            }
        });
    }

    public static void setInputLimit(final EditText et, final TextView tv, final int max,final String templateEnable,final String templateDisable) {
        int l = et.getText().toString().length();
        if (l <= max) {
            tv.setText(templateEnable.replace("count", String.valueOf(max - l)));
            tv.setTextColor(et.getContext().getResources().getColor(R.color.black));
        } else {
            tv.setText(templateEnable.replace("count", String.valueOf(l - max)));
            tv.setTextColor(et.getContext().getResources().getColor(R.color.red));
        }
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int l = s.length();
                if (l <= max) {
                    tv.setText("还可以输入" + (max - l) + "字");
                    tv.setTextColor(et.getContext().getResources().getColor(R.color.black));
                } else {
                    tv.setText("已超出" + (l - max) + "字");
                    tv.setTextColor(et.getContext().getResources().getColor(R.color.red));
                }
            }
        });
    }

    public static void setupActionBar(AngelActivity act, String title) {
        setupActionBar(act, title, null);
    }

    public static void setupActionBar(AngelActivity act, String title, String left) {
        ActionBar bar = act.getActionBar();
        if (bar == null) {
            return;
        }
        bar.setIcon(c.getResources().getDrawable(R.color.trans));
        bar.setDisplayHomeAsUpEnabled(false);
        bar.setDisplayShowCustomEnabled(true);
        bar.setDisplayShowHomeEnabled(false);
        bar.setTitle("");
        AngelActionBar aab = act.getAngelActionBar();
        if (aab == null) {
            aab = new AngelActionBar(act);
            act.setAngelActionBar(aab);
        }
        bar.setCustomView(aab);
        aab.setTitleText(title);
        aab.setLeftText(left);
    }

    /**
     * 获得屏幕锁
     *
     * @param c 上下文
     * @return 屏幕锁
     */
    static PowerManager.WakeLock getWakeLock(Context c) {
        if (wakeLock != null) {
            return wakeLock;
        }
        try {
            PowerManager powerManager = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
            int field = 0x00000020;

            // Yeah, this is hidden field.
            field = PowerManager.class.getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);

            wakeLock = powerManager.newWakeLock(field, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wakeLock;
    }

    /**
     * 激活近物传感器。当被遮挡时关闭屏幕
     *
     * @param c 上下文
     */
    public static void enableProximitySensor(Context c) {
        PowerManager.WakeLock wakeLock = getWakeLock(c);
        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }
    }

    /**
     * 关闭近物传感器
     *
     * @param c 上下文
     */
    public static void disableProximitySensor(Context c) {
        PowerManager.WakeLock wakeLock = getWakeLock(c);
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    /**
     * 强制显示Actionbar的Overflow图标（三个点）
     *
     * @param a
     */
    public static void setForceOverFlowIcon(Application a) {
        try {
            ViewConfiguration config = ViewConfiguration.get(a);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
    }

    /**
     * 获取用户友好的文件大小
     *
     * @param size 文件大小，单位：字节
     * @return
     */
    public static String getFileSize(float size) {
        try {
            if (size < 1024) {
                return String.format("%d B", (int) size);
            } else {
                size /= 1024;
                if (size < 1024) {
                    return String.format("%.1f KB", size);
                } else {
                    size /= 1024;
                    if (size < 1024) {
                        return String.format("%.1f MB", size);
                    } else {
                        size /= 1024;
                        return String.format("%.1f GB", size);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    public static String getFileSize(String path) {
        return getFileSize(new File(path));
    }

    public static String getFileSize(File f) {
        float size = f.length();
        return getFileSize(size);

    }
    /*
     * 获得可用的内存
     */
    public static long getMemUnused(Context mContext) {
        long MEM_UNUSED;
        // 得到ActivityManager
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 创建ActivityManager.MemoryInfo对象
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // 取得剩余的内存空间
        MEM_UNUSED = mi.availMem / 1024;
        return MEM_UNUSED;
    }
    /*
     * 获得总内存
     */
    public static long getMemTotal() {
        long mTotal;
        // /proc/meminfo读出的内核信息进行解释
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // beginIndex
        int begin = content.indexOf(':');
        // endIndex
        int end = content.indexOf('k');
        // 截取字符串信息
        content = content.substring(begin + 1, end).trim();
        mTotal = Integer.parseInt(content);
        return mTotal;
    }

    /**
     * 获取系统信息
     * @param context
     * @return
     */
    public static String getSysInfo(Context context){
        StringBuilder phoneInfo = new StringBuilder();
        phoneInfo.append("Product: " + android.os.Build.PRODUCT + System.getProperty("line.separator"));
        phoneInfo.append( "CPU_ABI: " + android.os.Build.CPU_ABI + System.getProperty("line.separator"));
        phoneInfo.append( "TAGS: " + android.os.Build.TAGS + System.getProperty("line.separator"));
        phoneInfo.append( "VERSION_CODES.BASE: " + android.os.Build.VERSION_CODES.BASE + System.getProperty("line.separator"));
        phoneInfo.append( "MODEL: " + android.os.Build.MODEL + System.getProperty("line.separator"));
        phoneInfo.append( "SDK: " + android.os.Build.VERSION.SDK + System.getProperty("line.separator"));
        phoneInfo.append( "VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE + System.getProperty("line.separator"));
        phoneInfo.append( "DEVICE: " + android.os.Build.DEVICE + System.getProperty("line.separator"));
        phoneInfo.append( "DISPLAY: " + android.os.Build.DISPLAY + System.getProperty("line.separator"));
        phoneInfo.append( "BRAND: " + android.os.Build.BRAND + System.getProperty("line.separator"));
        phoneInfo.append( "BOARD: " + android.os.Build.BOARD + System.getProperty("line.separator"));
        phoneInfo.append( "FINGERPRINT: " + android.os.Build.FINGERPRINT + System.getProperty("line.separator"));
        phoneInfo.append( "ID: " + android.os.Build.ID + System.getProperty("line.separator"));
        phoneInfo.append( "MANUFACTURER: " + android.os.Build.MANUFACTURER + System.getProperty("line.separator"));
        phoneInfo.append( "USER: " + android.os.Build.USER + System.getProperty("line.separator"));
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        phoneInfo.append("DeviceId(IMEI) = " + tm.getDeviceId() + System.getProperty("line.separator"));
        phoneInfo.append("DeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion() + System.getProperty("line.separator"));
        phoneInfo.append("Line1Number = " + tm.getLine1Number() + System.getProperty("line.separator"));
        phoneInfo.append("NetworkCountryIso = " + tm.getNetworkCountryIso() + System.getProperty("line.separator"));
        phoneInfo.append("NetworkOperator = " + tm.getNetworkOperator() + System.getProperty("line.separator"));
        phoneInfo.append("NetworkOperatorName = " + tm.getNetworkOperatorName() + System.getProperty("line.separator"));
        phoneInfo.append("NetworkType = " + tm.getNetworkType() + System.getProperty("line.separator"));
        phoneInfo.append("PhoneType = " + tm.getPhoneType() + System.getProperty("line.separator"));
        phoneInfo.append("SimCountryIso = " + tm.getSimCountryIso() + System.getProperty("line.separator"));
        phoneInfo.append("SimOperator = " + tm.getSimOperator() + System.getProperty("line.separator"));
        phoneInfo.append("SimOperatorName = " + tm.getSimOperatorName() + System.getProperty("line.separator"));
        phoneInfo.append("SimSerialNumber = " + tm.getSimSerialNumber() + System.getProperty("line.separator"));
        phoneInfo.append("SimState = " + tm.getSimState() + System.getProperty("line.separator"));
        phoneInfo.append("SubscriberId(IMSI) = " + tm.getSubscriberId() + System.getProperty("line.separator"));
        phoneInfo.append("VoiceMailNumber = " + tm.getVoiceMailNumber() + System.getProperty("line.separator"));
        return phoneInfo.toString();
    }
}

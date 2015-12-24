package com.programmeryuan.PKUCarrier.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BUtil extends Util {
//    public static void initActionBar(Context c, ActionBar bar, String title) {
////        bar.setIcon(c.getResources().getDrawable(R.drawable.ic_launcher));
////        bar.setDisplayShowCustomEnabled(false);
//        bar.setIcon(c.getResources().getDrawable(R.color.trans));
//        bar.setTitle(title);
//    }
    public static String pattern = "^([\\u4e00-\\u9fa5]|\\w+)+$";
    public static String pattern_pwd  = "^(\\w+)+$";
    public static String pattern_chinese = "([\\u4e00-\\u9fa5])+";
    public static boolean checkValidName(String name){
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(name);
        return matcher.find();
    }

    public static boolean checkValidPassword(String name){
        Pattern p = Pattern.compile(pattern_pwd);
        Matcher matcher = p.matcher(name);
        return matcher.find();
    }

    public static boolean checkChineseInString(String name){
        Pattern p = Pattern.compile(pattern_chinese);
        Matcher matcher = p.matcher(name);
        return matcher.find();
    }

    public static boolean isValidString(String s){
        return !(s == null || s.isEmpty());
    }

    public static boolean containsEmoji(String source) {
        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);

            if (isEmojiCharacter(codePoint)) {
                //do nothing，判断到了这里表明，确认有表情字符
                return true;
            }
        }

        return false;
    }

    public static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    /**
     * 过滤emoji 或者 其他非文字类型的字符
     *
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {

        if (!containsEmoji(source)) {
            return source;//如果不包含，直接返回
        }
        //到这里铁定包含
        StringBuilder buf = null;

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);

            if (!isEmojiCharacter(codePoint)) {
                if (buf == null) {
                    buf = new StringBuilder(source.length());
                }

                buf.append(codePoint);
            } else {
            }
        }

        if (buf == null) {
            return "";
        } else {
            return buf.toString();
        }
    }

    public static void setEmojiFilter(final EditText et) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    String str = s.toString();
                    if (str.length() > 0) {
                        if (BUtil.containsEmoji(str)) {
                            if (str.length() > 0) {
                                et.setText(BUtil.filterEmoji(str));
                                et.setSelection(et.getText().toString().length());
                            }
                        }
                    }
                }
            }
        });
    }

    public static String getValidString(String input){
        return isValidString(input) ? input : "";
    }

    public static String getResizedPicture(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int width = options.outWidth;
        Logger.out("================");
        Logger.out("options.outWidth = " + width);
        int height = options.outHeight;
        Logger.out("options.outHeight = " + height);
        Logger.out("================");
        if (width <= 700 || height <= 700) {
            return path;//不压缩
        } else {
            int ratio = (int) Math.min(Math.ceil(width / 700), Math.ceil(height / 700));
            options.inSampleSize = ratio;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(path, options);
            File dir = new File(Environment.getExternalStorageDirectory() + "/bangyoung_filter");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, "thumbnail_" + path.substring(path.lastIndexOf("/") + 1) + ".jpg");
            try {
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                Logger.out("================");
                Logger.out("width = " + bitmap.getWidth());
                Logger.out("height = " + bitmap.getWidth());
                Logger.out("================");
                fOut.flush();
                fOut.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return file.getAbsolutePath();
        }
    }

    public static String transformStandardDateToChineseForLive(String whole_date) {
        String[] dates = whole_date.split(" ");
        if (dates.length > 1) return transformStandardDateToChineseForLive(dates[0],dates[1]);
        else return "";
    }

    public static String transformStandardDateToChineseForLive(String date, String time){
        String[] dates = date.split("-");
        String date_cn = dates[0] + "年" + dates[1] + "月" + dates[2] + "日";
        String[] times = time.split(":");
        String time_cn = times[0] + ":" + times[1];
        String prefix = Integer.parseInt(times[0]) < 12 ? "上午" : "下午";
        return date_cn + " " + prefix + " " + time_cn;
    }

    public static String transformStandardDateToChineseForReplay(String whole_date) {
        String[] dates = whole_date.split(" ");
        if (dates.length > 1) return transformStandardDateToChineseForReplay(dates[0],dates[1]);
        else return "";
    }

    public static String transformStandardDateToChineseForReplay(String date, String time){
        String[] dates = date.split("-");
        String date_cn = dates[0] + "年" + dates[1] + "月" + dates[2] + "日";
        String[] times = time.split(":");
        String time_cn = times[0] + ":" + times[1];
        return date_cn + " " + time_cn;
    }
}

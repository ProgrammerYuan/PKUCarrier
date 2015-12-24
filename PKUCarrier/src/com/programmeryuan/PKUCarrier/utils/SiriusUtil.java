package com.programmeryuan.PKUCarrier.utils;

import android.content.Context;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ProgrammerYuan on 15/8/7.
 */
public class SiriusUtil {

	public static String pattern = "^([\\u4e00-\\u9fa5]|\\w+)+$";
	public static String pattern_pwd  = "^(\\w+)+$";
	public static String pattern_chinese = "([\\u4e00-\\u9fa5])+";
	public static String pattern_num = "^([0-9])+";
	public static String pattern_phone = "^(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$";

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

	public static boolean checkValidPhoneNumber(String phone) {
		Pattern p = Pattern.compile(pattern_phone);
		Matcher matcher = p.matcher(phone);
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

	public static String getEllipsizedStringByLength(String str, int length) {
		if(str.length() <= length) return str;
		return str.substring(0,length) + "...";
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
	 * 将sp转换为px
	 *
	 * @param spValue sp值
	 * @return 相应的px
	 */
	public static int getPXfromSP(Context c, float spValue) {
		float fontScale = c.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);

	}

	/**
	 * 将dp转换为px
	 *
	 * @param dipValue dp值
	 * @return 相应的px
	 */
	public static int getPX(Context c, float dipValue) {
		final float scale = c.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px转换为dip
	 *
	 * @param pxValue px值
	 * @return 相应的dp
	 */
	public static int getDP(Context c, float pxValue) {
		final float scale = c.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * MD5加密，加密失败返回 null
	 * @param string 待加密字符串
	 * @return 加密后字符串
	 */
	public static String getStringMD5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
//			throw new RuntimeException("Huh, MD5 should be supported?", e);
			return null;
		} catch (UnsupportedEncodingException e) {
//			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
			return null;
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10) hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

	public static int cut(int value,int min,int max) {
		if(value < min) return min;
		if(value > max) return max;
		return value;
	}

	public static float cut(float value,float min,float max) {
		if(value < min) return min;
		if(value > max) return max;
		return value;
	}

	public static String formalizeForSQLUse(String str) {
		return str == null ? "" : str.replaceAll("'","''");
	}
}

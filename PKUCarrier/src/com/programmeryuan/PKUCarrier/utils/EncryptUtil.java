/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.cloopen.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.programmeryuan.PKUCarrier.utils;

import java.security.MessageDigest;


public class EncryptUtil {

    private static final String UTF8 = "utf-8";

    /**
     * MD5数字签名
     *
     * @param src
     * @return
     * @throws Exception
     */
    public static String md5Digest(String src) {
        // 定义数字签名方法, 可用：MD5, SHA-1
        MessageDigest md = null;
        byte[] b = null;
        try {
            md = MessageDigest.getInstance("MD5");
            b = md.digest(src.getBytes(UTF8));
        } catch (Exception e) {
            b = new byte[1];
            e.printStackTrace();
        }

        return byte2HexStr(b);
    }

    /**
     * BASE64编码
     * @param src
     * @return
     * @throws Exception
     */

    /**
     * 字节数组转化为大写16进制字符串
     *
     * @param b
     * @return
     */
    private static String byte2HexStr(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String s = Integer.toHexString(b[i] & 0xFF);
            if (s.length() == 1) {
                sb.append("0");
            }
            sb.append(s.toUpperCase());
        }
        return sb.toString();
    }
}  

package com.aliyun.gts.gmall.center.trade.core.util;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

/**
 * 功能：支付宝MD5加密处理核心文件，不需要修改
 * 版本：3.1
 * 修改日期：2010-11-01
 * 备注：支付宝官方提供
 */

public class Md5Encrypt {
    /**
     * Used building output as Hex
     */
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 对字符串默认用UTF-8格式进行MD5加密
     *
     * @param text:明文
     * @return 密文
     */
    public static String md5(String text) {
        MessageDigest msgDigest = null;

        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }

        try {
            msgDigest.update(text.getBytes("UTF-8"));    //注意改接口是按照指定编码形式签名
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("System doesn't support your  EncodingException.");
        }
        byte[] bytes = msgDigest.digest();
        return new String(encodeHex(bytes));
    }

    /**
     * 对字符串使用指定编码格式进行MD5加密
     *
     * @param text:明文
     * @param charsetType:加密使用的字符编码
     * @return 密文
     */
    public static String md5(String text, String charsetType) {
        MessageDigest msgDigest = null;

        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }

        try {
            if (StringUtils.isNotBlank(charsetType)) {
                msgDigest.update(text.getBytes(charsetType));
            } else {
                msgDigest.update(text.getBytes());
            }
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("System doesn't support your  EncodingException.");
        }

        byte[] bytes = msgDigest.digest();
        return new String(encodeHex(bytes));
    }


    public static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }
        return out;
    }

    /**
     * 签名字符串
     *
     * @param text          需要签名的字符串
     * @param key           密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static String sign(String text, String key, String input_charset) {
        text = text + key;
        return DigestUtils.md5Hex(getContentBytes(text, input_charset));
    }

    /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws UnsupportedEncodingException
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5"+I18NMessageUtils.getMessage("error.during.signing")+","+I18NMessageUtils.getMessage("incorrect.encoding.set")+","+I18NMessageUtils.getMessage("current.encoding.set")+":" + charset);  //# 签名过程中出现错误,指定的编码集不对,您目前指定的编码集是
        }
    }
}
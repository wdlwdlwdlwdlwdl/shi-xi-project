package com.aliyun.gts.gmall.manager.front.b2bcomm.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class SignatureUtils {

    static final String sKey = "df^dUDmdf#$-d132.@+=}sd1licm";


    public static String signatureMd5(Object o){
        String md5 = "";
        String data = o.toString() + "@" + sKey;
        try {
            md5 = DigestUtils.md5Hex(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return md5;

    }

    public static boolean checkMd5(String token , Object o){
        String md5 = signatureMd5(o);
        return token.equals(md5);
    }

    public static String encrypt(String text) {
        try {
            text = text + "@" + sKey;
            Cipher cipher = makeCipher();
            SecretKey secretKey = makeKeyFactory();
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            String encrypt = new String(Base64.encodeBase64(cipher.doFinal(text.getBytes())));
            return URLEncoder.encode(encrypt);
        }catch (Exception e){
            return "";
        }
    }

    public static String decrypt(String text) {
        try {
            Cipher cipher = makeCipher();
            SecretKey secretKey = makeKeyFactory();
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            text = URLDecoder.decode(text);
            String decrypt = new String(cipher.doFinal(Base64.decodeBase64(text.getBytes())));
            return decrypt;
        }catch (Exception e){
            return "";
        }
    }

    public static boolean checkDecrypt(String text) {
        String decrypt = decrypt(text);
        String check = StringUtils.substringAfter(decrypt , "@");
        return sKey.equals(check);
    }

    private static Cipher makeCipher() throws Exception{
        return Cipher.getInstance("DES");
    }

    private static SecretKey makeKeyFactory() throws Exception{
        SecretKeyFactory des = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = des.generateSecret(new DESKeySpec(sKey.getBytes()));
        return secretKey;
    }

    public static void main(String[] args) throws Exception {
        String d = encrypt("13");
        System.out.println(d);

        System.out.println(decrypt(d));

        System.out.println(checkDecrypt(d));
    }



}

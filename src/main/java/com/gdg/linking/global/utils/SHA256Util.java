package com.gdg.linking.global.utils;

import java.security.MessageDigest;

public class SHA256Util {

    public static final String ENCRYPTION_KEY = "SHA-256";
    public static String encryptSHA256(String str){

        String SHA = null;
        MessageDigest sh;
        try {
            sh = MessageDigest.getInstance(ENCRYPTION_KEY);
            sh.update(str.getBytes());
            byte[] byteData = sh.digest();
            StringBuffer sb = new StringBuffer();
            for (byte byteDatum : byteData) {
                sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
            }
            SHA = sb.toString();
        } catch (Exception e) {
            SHA = null;
        }

        return SHA;
    }

}

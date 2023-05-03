package com.strongcom.doormate.crypto;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AES256Cipher {

    final static byte[] KEY = {
            0x6d, 0x79, 0x56, 0x65, 0x72, 0x79, 0x54, 0x6f, 0x70,
            0x53, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b
    };


    /**
     * 암호화
     *
     * @param planStr 암호화 대상 문자열
     * @return String 암호화 문자열
     */
    public static String encrypt(String planStr) {

        String encodedStr = null;

        SecretKeySpec aesKeySpec = new SecretKeySpec(KEY, "AES");
        Cipher cipher = null;
        try {
            // instance 생성
            cipher = Cipher.getInstance("AES");

            // Cipher 초기화
            cipher.init(Cipher.ENCRYPT_MODE, aesKeySpec);

            // Encoding
            encodedStr = new String(Base64.encodeBase64(cipher.doFinal(planStr.getBytes())));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return encodedStr;

    }

    /**
     * 복호화
     *
     * @param encStr Decoding 대상 문자열
     * @return String 복호화 문자열
     */
    public static String decrypt(String encStr) {

        Cipher cipher = null;
        byte[] byteStr = null;
        String decodedStr = null;

        SecretKeySpec aesKeySpec = new SecretKeySpec(KEY, "AES");
        try {
            // instance 생성
            cipher = Cipher.getInstance("AES");

            // Cipher 초기화
            cipher.init(Cipher.DECRYPT_MODE, aesKeySpec);

            // Base64 Decoding
            byteStr = Base64.decodeBase64(encStr.getBytes());

            // Decoding
            decodedStr = new String(cipher.doFinal(byteStr),"UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return decodedStr;
    }
}

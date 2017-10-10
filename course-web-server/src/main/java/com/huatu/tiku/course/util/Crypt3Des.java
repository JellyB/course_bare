package com.huatu.tiku.course.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Locale;

/**
 * 加解密 3Des，与课程相对应
 * Created by ht on 2016/10/12.
 */
public class Crypt3Des {

    //密钥
    private static final String key = "0123456789QWEQWEEWQQ1234";
    private static final String Algorithm = "DESede"; // 定义 加密算法,可用  DES,DESede,Blowfish

    /**
     * 加密
     *
     * @param ciphertext 密文的字符串
     * @return
     */
    public static String encryptMode(String ciphertext) {
        try {
            byte[] keyBytes = key.getBytes();
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keyBytes, Algorithm);
            // 加密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return bytesToHexString(c1.doFinal(ciphertext.getBytes()));
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return "";
    }

    /**
     * 解密
     *
     * @param plaintext 明文字符串
     * @return
     */
    public static String decryptMode(String plaintext) {
        try {
            byte[] keyBytes = key.getBytes();
            SecretKey deskey = new SecretKeySpec(keyBytes, Algorithm);
            // 解密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return new String(c1.doFinal(hexStr2Bytes(plaintext)));
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return "";
    }

    //转换成十六进制字符串
    public static String byte2hex(byte[] bytes) {
        String hs = "";
        String stmp = "";

        for (int n = 0; n < bytes.length; n++) {
            stmp = (Integer.toHexString(bytes[n] & 0XFF));
            if (stmp.length() == 1) hs = hs + "0" + stmp;
            else hs = hs + stmp;
            if (n < bytes.length - 1) hs = hs + ":";
        }
        return hs.toUpperCase();
    }

    /**
     * 将字节转换为字符串
     *
     * @param bytes
     * @return
     */
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }

    /**
     * 将字符转换为字节码
     *
     * @param src
     * @return
     */
    public static byte[] hexStr2Bytes(String src) {
        /* 对输入值进行规范化整理 */
        src = src.trim().replace(" ", "").toUpperCase(Locale.US);
        // 处理值初始化
        int m = 0, n = 0;
        int iLen = src.length() / 2; // 计算长度
        byte[] ret = new byte[iLen]; // 分配存储空间

        for (int i = 0; i < iLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = (byte) (Integer.decode("0x" + src.substring(i * 2, m)
                    + src.substring(m, n)) & 0xFF);
        }
        return ret;
    }
    public static void main(String[] args){
        String str = "AA1A166606D6DB0BCD7A4DD733CC11AF32669BB3ED181FBEDA1FBD2AB6E59D56DC254B52A0291A2996413AE94979969A12E16A4E154498E23E44A3CF0439B5D6D3D2E637D1ECD0DA2BE09E72172233CD65E212D4E2FBECA77828D1D0D46A6EF9";
        System.out.println(Crypt3Des.decryptMode(str));
    }
}

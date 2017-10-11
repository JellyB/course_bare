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
        String str = "637F142047BFAC9252CC7E75A11698465F8E6549E7EF90EEA834A2632E0871840C59B2D83A0651B2CFCC77CC56C5540528ADB30C14F0366BC1F2BC8780E5E39BA382BDE57579DF788F8919571062E4437F170B36E9E21CE3D5B860962A542F3F400120007C61512FA93F8289F812138A468343E24C08D7189EE99E784AB15B0375136B46760C9090950C7A7890ACD42C236E08811724A4B6F84A7F703637BDC568FF69D2CBE2ED32D6A33800254A79108B5B88E52C75CACAB44085D0D847DFE564F589CB9AA6C27AC24A33BFC633ADA00DC351B611C92D70";
        System.out.println(Crypt3Des.decryptMode(str));
    }
}

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
        String str = "" +
                "45AECA59B1557C977B1AAF487764F846132D006487CB3F160BDB52395A9CCF13B5C1E7531737AE97D3DECD1F2B9EE28B38E76FB69B2E28CBD168502814B4DEB36C939D56EFF3E99DA6F2828B4C98A6C60930E9AE4218BE0EC011F8E27828A3205C22A0E229718F1A3B710C2C121D70F04CDFF93DB5E5A6BE530F58A42990E7ECA9400522FC1123AC4D60BA502BC4C1DE820144BEE981FD70C3EFD18D446A64D2211ACCA5FEA65536E55E9829A19581E4F0566D5A17B2CF51B47CA42FC425284F5A148945761CD5B1DFE253CBA998FF0AAC139D8DDC742D895C7AFE3081BB9662DE042E0A6035E8E8FAC15F597D58EFB785139077C2338CFFEE12FDC0CD72E4859696C848CCDCDC88FAD42AD2574A077A022BC6E4E44DC0ABDB48D26264727526BF963166A7A8A88E1A348D624BCF82A8B9BDD7F2BCA3654F89467213662BF56E4A060DD25EADEC68A0DA78BD57FA9AD44091FC4F8373E83123323E71349577AE82C674E573D2743B0C4CD7601DC2272FAD4DEC54EFE5D40C6F88610693AF32277EC677DC098CF8BAA96F16763B80F05A94D8DF3242EBB6B702731B72A81231CD1A56B2639BBF53F761200F8B852045E0B8CD69B57C23B01D633F3332EB6A86CEB423B8A63388CAF1"
                ;
        System.out.println(Crypt3Des.decryptMode(str));
    }
}

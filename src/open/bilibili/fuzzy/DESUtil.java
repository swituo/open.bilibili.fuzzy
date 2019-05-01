package open.bilibili.fuzzy;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;


public class DESUtil {

    private byte[] key;

    public DESUtil() throws Exception {
        this.key = initKey();
    }

    public DESUtil(String key) {
        this.key = initKey(key);
    }


    private byte[] initKey() throws Exception{
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56);
        SecretKey secretKey = keyGen.generateKey();
        return secretKey.getEncoded();
    }


    private byte[] initKey(String key) {
        byte[] keyBytes = key.getBytes();
        byte[] ret;
        if (keyBytes.length == 8) {
            ret = keyBytes;
        } else if (keyBytes.length < 8) {
            ret = new byte[8];
            for (int i=0; i<keyBytes.length; i++) {
                ret[i] = keyBytes[i];
            }
        } else {
            ret = Arrays.copyOf(keyBytes, 8);
        }
        return ret;
    }


    public byte[] encrypt(byte[] data) throws Exception{
        SecretKey secretKey = new SecretKeySpec(this.key, "DES");

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherBytes = cipher.doFinal(data);
        return cipherBytes;
    }


    public byte[] decrypt(byte[] data) throws Exception{
        SecretKey secretKey = new SecretKeySpec(this.key, "DES");

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] plainBytes = cipher.doFinal(data);
        return plainBytes;
    }

}
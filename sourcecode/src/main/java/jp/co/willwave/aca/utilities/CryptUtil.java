package jp.co.willwave.aca.utilities;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

public class CryptUtil {
    private static final Logger LOG = LoggerFactory.getLogger(CryptUtil.class);

    /**
     * @param passwordToHash password to hash
     * @return hashed string
     */
    public static String encryptOneWay(String passwordToHash, String key) throws CommonException {
        try {
            MessageDigest md = MessageDigest.getInstance(Constant.EnCryption.SHA_512);
            md.update(key.getBytes(Constant.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes(Constant.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & Constant.EnCryption._0XFF)
                        + Constant.EnCryption._0X100, Constant.EnCryption.RADIUS)
                        .substring(1));
            }
            return sb.toString();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new CommonException(e);
        }
    }

    public static String encrypt(String plainText) throws CommonException {
        try {
            Cipher cipher = Cipher.getInstance(Constant.EnCryption.PKCS5PADDING);
            SecretKeySpec secretKey = new SecretKeySpec(Constant.EnCryption.KEY, Constant.EnCryption.AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(plainText.getBytes(Constant.UTF_8));
            return new String(Base64.getEncoder().encode(cipherText), Constant.UTF_8);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new CommonException(e);
        }
    }

    public static String decrypt(String encryptedText) throws CommonException {
        try {
            Cipher cipher = Cipher.getInstance(Constant.EnCryption.PKCS5PADDING);
            SecretKeySpec secretKey = new SecretKeySpec(Constant.EnCryption.KEY, Constant.EnCryption.AES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] cipherText = Base64.getDecoder().decode(encryptedText.getBytes(Constant.UTF_8));
            return new String(cipher.doFinal(cipherText), Constant.UTF_8);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new CommonException(e);
        }
    }

    public static String hmacSha(String KEY, String VALUE, String SHA_TYPE) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(KEY.getBytes(Constant.UTF_8), SHA_TYPE);
            Mac mac = Mac.getInstance(SHA_TYPE);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(VALUE.getBytes(Constant.UTF_8));

            byte[] hexArray = {
                    (byte) '0', (byte) '1', (byte) '2', (byte) '3',
                    (byte) '4', (byte) '5', (byte) '6', (byte) '7',
                    (byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
                    (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'
            };
            byte[] hexChars = new byte[rawHmac.length * 2];
            for (int j = 0; j < rawHmac.length; j++) {
                int v = rawHmac[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String encodeBase64(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    public static String decodeBase64(String str) {
        return new String(Base64.getDecoder().decode(str));
    }
}

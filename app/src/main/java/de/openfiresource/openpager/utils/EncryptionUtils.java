package de.openfiresource.openpager.utils;

import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by stieglit on 11.08.2016.
 */
public class EncryptionUtils {
    public static final String CHARACTER_ENCODING = "UTF-8";
    static final String cipherTransformation = "AES/CBC/PKCS5Padding";
    static final String aesEncryptionAlgorithm = "AES";

    // / <summary>
    // / Decrypts a base64 encoded string using the given key (AES 128bit key
    // and a Chain Block Cipher)
    // / </summary>
    // / <param name="encryptedText">Base64 Encoded String</param>
    // / <param name="key">Secret Key</param>
    // / <returns>Decrypted String</returns>
    public static String decrypt(String encryptedText, String key)
            throws GeneralSecurityException, IOException {
        byte[] cipheredBytes = Base64.decode(encryptedText, Base64.DEFAULT);
        byte[] keyBytes = getKeyBytes(key);
        return new String(decrypt(cipheredBytes, keyBytes, keyBytes),
                CHARACTER_ENCODING);
    }

    private static byte[] decrypt(byte[] cipherText, byte[] key,
                                  byte[] initialVector) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpecy = new SecretKeySpec(key,
                aesEncryptionAlgorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
        cipherText = cipher.doFinal(cipherText);
        return cipherText;
    }

    private static byte[] getKeyBytes(String key)
            throws UnsupportedEncodingException {
        byte[] keyBytes = new byte[16];
        byte[] parameterKeyBytes = key.getBytes(CHARACTER_ENCODING);
        System.arraycopy(parameterKeyBytes, 0, keyBytes, 0,
                Math.min(parameterKeyBytes.length, keyBytes.length));
        return keyBytes;
    }
}

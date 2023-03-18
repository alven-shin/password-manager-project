package password_manager.database;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypter {
    private SecretKey secret;
    private Cipher cipher;

    public Encrypter(byte[] salt, String password) {
        // Generate secret key from password and salt
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            this.secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public byte[] encryptString(String s) throws BadPaddingException {
        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, this.secret, generateIV());
            return cipher.doFinal(s.getBytes("UTF-8"));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
                | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] decryptString(byte[] encrypted) throws BadPaddingException {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secret, generateIV());
            return cipher.doFinal(encrypted);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }

    public static IvParameterSpec generateIV() {
        // SecureRandom random = new SecureRandom();
        byte[] iv = new byte[16]; // 16 bytes for AES
        // random.nextBytes(iv); // fill with random bytes
        return new IvParameterSpec(iv); // create IvParameterSpec
        // return iv;
    }

    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public static String generateRandomString() {
        // Define possible characters
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-=_+?<>";

        // Create a StringBuilder object
        StringBuilder sb = new StringBuilder();

        // Loop for 25 times
        for (int i = 0; i < 25; i++) {
            // Generate a random index
            int index = (int) (Math.random() * chars.length());
            // Append a character at that index
            sb.append(chars.charAt(index));
        }

        // Convert StringBuilder to String
        return sb.toString();
    }
}

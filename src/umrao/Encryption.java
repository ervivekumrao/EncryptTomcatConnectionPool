package umrao;


import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import static umrao.Constants.*;

public class Encryption {

    private static final Logger oLog = Logger.getLogger(Encryption.class.getName());
    private final String encryptionKey;
    private final String encryptionAlgo;

    public Encryption(String encryptionKey, String encryptionAlgorithm) {
        if (encryptionKey == null || encryptionKey.isEmpty()) {
            this.encryptionKey = ENCRYPTION_KEY;
        } else {
            this.encryptionKey = encryptionKey;
        }

        if (encryptionAlgorithm == null || encryptionAlgorithm.isEmpty()) {
            this.encryptionAlgo = ENCRYPTION_ALGO;
        } else {
            this.encryptionAlgo = encryptionAlgorithm;
        }
    }

    public String getEncryptedMessage(String msg) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, generateStaticSecretKey(), getIvParameterSpec());

            // Encode the encrypted message and IV to Base64 for easy display
            byte[] encryptedMessage = cipher.doFinal(msg.getBytes());
            return Base64.getEncoder().encodeToString(encryptedMessage);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            oLog.log(Level.SEVERE, "Message encoding failed. ", e);
            throw new RuntimeException(e);
        }
    }

    public String getDecryptedMessage(String msg) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, generateStaticSecretKey(), getIvParameterSpec());
            byte[] decryptedMessage = cipher.doFinal(Base64.getDecoder().decode(msg));
            return new String(decryptedMessage);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                 InvalidAlgorithmParameterException | BadPaddingException | InvalidKeyException e) {
            oLog.log(Level.SEVERE, "Decryption failed. ", e);
            throw new RuntimeException(e);
        }
    }

    public SecretKey generateStaticSecretKey() {
        try {
            MessageDigest digester = MessageDigest.getInstance(HASHING_ALGO);

            int maxKeyLen = Cipher.getMaxAllowedKeyLength(encryptionAlgo);
            char[] keySalt;
            if (this.encryptionKey.length() > maxKeyLen) {
                keySalt = this.encryptionKey.substring(0, maxKeyLen).toCharArray();
            } else {
                keySalt = this.encryptionKey.toCharArray();
            }

            for (char word : keySalt) {
                digester.update((byte) word);
            }
            String encode = Base64.getEncoder().encodeToString(digester.digest());
            byte[] key;
            if (encode.length() > 32) {
                key = Base64.getDecoder().decode(encode.substring(0, 32));
            } else {
                key = Base64.getDecoder().decode(encode);
            }
            return new SecretKeySpec(key, encryptionAlgo);
        } catch (NoSuchAlgorithmException ex) {
            oLog.log(Level.SEVERE, "Secret key generation failed. ", ex);
            throw new RuntimeException(ex);
        }
    }

    public IvParameterSpec getIvParameterSpec() {
        return new IvParameterSpec(IV.getBytes());
    }
}
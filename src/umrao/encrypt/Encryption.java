package umrao.encrypt;


import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import static umrao.encrypt.Constants.*;

public class Encryption {

    private static final Logger oLog = Logger.getLogger(Encryption.class.getName());
    private static final String ENCRYPTION_IDENTIFIER = "@##$$$&&&&";
    private static final String HEX_CONVERTED_ENCRYPTION_IDENTIFIER = "40232324242426262626";
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
        if (msg == null || msg.isEmpty()) {
            throw new RuntimeException("Encryption message is empty or null.");
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, generateStaticSecretKey(), getIvParameterSpec());

            // Encode the encrypted message and IV to Base64 for easy display
            byte[] encryptedMessage = cipher.doFinal(msg.getBytes());
            return stringToHex(Base64.getEncoder().encodeToString(encryptedMessage) + ENCRYPTION_IDENTIFIER);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IllegalArgumentException e) {
            oLog.log(Level.SEVERE, "Message encoding failed. ", e);
            throw new RuntimeException(e);
        }
    }

    public String getDecryptedMessage(String msg) {
        if (msg == null || msg.isEmpty()) {
            throw new RuntimeException("Decryption message is empty or null.");
        } else {
            if (msg.length() > HEX_CONVERTED_ENCRYPTION_IDENTIFIER.length() && HEX_CONVERTED_ENCRYPTION_IDENTIFIER.equals(msg.substring(msg.length() - HEX_CONVERTED_ENCRYPTION_IDENTIFIER.length()))) {
                msg = hexToString(msg);
            } else {
                return msg;
            }
            if (msg.length() > ENCRYPTION_IDENTIFIER.length()) {
                if (ENCRYPTION_IDENTIFIER.equals(msg.substring(msg.length() - ENCRYPTION_IDENTIFIER.length()))) {
                    msg = msg.substring(0, msg.length() - ENCRYPTION_IDENTIFIER.length());
                } else {
                    return msg;
                }
            } else {
                return msg;
            }
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, generateStaticSecretKey(), getIvParameterSpec());
            byte[] decryptedMessage = cipher.doFinal(Base64.getDecoder().decode(msg));
            return new String(decryptedMessage);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                 InvalidAlgorithmParameterException | BadPaddingException | InvalidKeyException |
                 IllegalArgumentException e) {
            oLog.log(Level.SEVERE, "Decryption failed. ", e);
            throw new RuntimeException("Decryption value check failed, either not generated from this app or contains some incorrect character.", e);
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

    public void decryptProperties(Reference reference) {
        Enumeration<RefAddr> enumeration = reference.getAll();
        int index = 0;
        StringBuilder sb = new StringBuilder();
        while (enumeration.hasMoreElements()) {
            RefAddr refAddr = enumeration.nextElement();
            String property = refAddr.getType();
            if (property != null && !property.isEmpty()) {
                if (refAddr.getContent() != null) {
                    String value = refAddr.getContent().toString();
                    if (!value.isEmpty()) {
                        String decryptedValue = getDecryptedMessage(value);
                        if (!value.equals(decryptedValue)) {
                            reference.remove(index);
                            reference.add(index, new StringRefAddr(property, decryptedValue));
                        }
                    }
                    sb.append(property).append(":").append(value).append(System.lineSeparator());
                }
            }
            index++;
        }
        oLog.log(Level.SEVERE, "Here are Reference data count: " + index + System.lineSeparator() + "And all properties: " + sb);
    }

    private String stringToHex(String str) {
        StringBuilder hexString = new StringBuilder();
        for (char c : str.toCharArray()) {
            hexString.append(Integer.toHexString(c));
        }
        return hexString.toString().toUpperCase();
    }

    private String hexToString(String hex) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }
}

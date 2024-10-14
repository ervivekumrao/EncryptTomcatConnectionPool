package umrao;

import static umrao.Constants.ENCRYPTION_ALGO;
import static umrao.Constants.ENCRYPTION_KEY;

public class EncryptDecrypt {
    public static void main(String... args) {
        if (args.length != 2) {
            System.out.println("Wrong input parameter use below \njava -jar tomcat-dbcp2-encrypt.jar -e messageToEncrypt \nor \njava -jar tomcat-dbcp2-encrypt.jar -d messageToDecrypt");
            System.exit(0);
        }

        Encryption c = new Encryption(ENCRYPTION_KEY, ENCRYPTION_ALGO);
        if ("-e".equalsIgnoreCase(args[0])) {
            System.out.println("encrypted: " + c.getEncryptedMessage(args[1]));
        } else if ("-d".equalsIgnoreCase(args[0])) {
            System.out.println("decrypted: " + c.getDecryptedMessage(args[1]));
        } else {
            System.out.println("Invalid arguments, it should be either -e or -d");
        }
    }
}

package umrao;

import static umrao.Constants.ENCRYPTION_ALGO;
import static umrao.Constants.ENCRYPTION_KEY;

public class EncryptDecrypt {


    //"java -jar tomcat-dbcp2-encrypt.jar [-e/-d] [msg]"
    public static void main(String ... args) {
        if (args.length != 2) {
            System.out.println("Tomcat dbcp2 encrypt lib.");
            //System.exit(0);
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

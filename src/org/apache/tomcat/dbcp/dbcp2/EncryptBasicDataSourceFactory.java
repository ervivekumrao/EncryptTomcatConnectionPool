package org.apache.tomcat.dbcp.dbcp2;


import umrao.Encryption;

import javax.naming.*;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import static umrao.Constants.ENCRYPTION_ALGO;
import static umrao.Constants.ENCRYPTION_KEY;


public class EncryptBasicDataSourceFactory extends BasicDataSourceFactory {

    private static final Logger oLog = Logger.getLogger(EncryptBasicDataSourceFactory.class.getName());

    public EncryptBasicDataSourceFactory() {
    }

    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws SQLException {
        if (obj instanceof Reference reference) {
            try {
                setUsername(reference);
                setPassword(reference);
                setURL(reference);
                oLog.log(Level.ALL, "Property reference: ", reference);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return super.getObjectInstance(obj, name, nameCtx, environment);
    }

    private void setUsername(Reference reference) throws Exception {
        decryptAndReplace("username", reference);
    }

    private void setPassword(Reference reference) throws Exception {
        decryptAndReplace("password", reference);
    }

    private void setURL(Reference reference) throws Exception {
        decryptAndReplace("url", reference);
    }

    private void decryptAndReplace(String propertyName, Reference reference) throws Exception {
        int propertyIndex = getPropertyIndex(propertyName, reference);
        Encryption encryption = new Encryption(ENCRYPTION_KEY, ENCRYPTION_ALGO);
        String decryptedValue = encryption.getDecryptedMessage(reference.get(propertyIndex).getContent().toString());
        reference.remove(propertyIndex);
        reference.add(propertyIndex, new StringRefAddr(propertyName, decryptedValue));
    }

    private int getPropertyIndex(String propertyName, Reference reference) throws Exception {
        Enumeration<RefAddr> enumeration = reference.getAll();

        for (int index = 0; enumeration.hasMoreElements(); index++) {
            RefAddr adder = enumeration.nextElement();
            if (adder.getType().compareTo(propertyName) == 0) {
                return index;
            }
        }
        throw new Exception("The Property " + propertyName + " not found in configuration. " + "\nThe reference Object is: " + reference);
    }
}

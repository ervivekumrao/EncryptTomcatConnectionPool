package org.apache.tomcat.dbcp.dbcp2;

import umrao.encrypt.Encryption;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import static umrao.encrypt.Constants.ENCRYPTION_ALGO;
import static umrao.encrypt.Constants.ENCRYPTION_KEY;

public class EncryptBasicDataSourceFactory extends BasicDataSourceFactory {

    private static final Logger oLog = Logger.getLogger(EncryptBasicDataSourceFactory.class.getName());

    public EncryptBasicDataSourceFactory() {
    }

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws SQLException {
        if (obj instanceof Reference) {
            try {
                new Encryption(ENCRYPTION_KEY, ENCRYPTION_ALGO).decryptProperties((Reference) obj);
                oLog.log(Level.ALL, "Property reference: ", obj);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return super.getObjectInstance(obj, name, nameCtx, environment);
    }
}

# EncryptTomcatConnectionPool


## How To Build Project
1. Open this as project with `IntelliJ`
2. Copy latest `apache-tomcat-9.0.96/lib/tomcat-dbcp.jar` and `apache-tomcat-9.0.96/lib/tomcat-jdbc.jar` from Tomcat build to project `libs` directory.
3. Refresh project so libs show up in the structure (right click on project top level, refresh/synchronize)
4. Expand libs and right-click on the jar > add as Library...


## How To Use Encryption/Decryption  
Encrypt/decrypt username, password and URL through 

    $ java -jar tomcat-dbcp-ext.jar -e/-d MSG_TO_ENCRYPT_DECRYPT
                                or
    $ java -cp tomcat-dbcp-ext.jar umrao.encrypt.EncryptDecrypt -e/-d MSG_TO_ENCRYPT_DECRYPT
encrypt or decrypt username, password and JDBC URL one by one
example:

    encrypt username='my_user'
    $ java -jar tomcat-dbcp-ext.jar -e my_user
    encrypt password='user_password'
    $ java -jar tomcat-dbcp-ext.jar -e user_password

This will generate encrypted string that can be used in resource
Or Run umrao/encrypt/EncryptDecrypt.java from IntelliJ with your input values by modifying it.


## Use Jar For Connection Pool
1. Either download specific jar for your use or create your own custom jar using source code.
2. Use `tomcat-dbcp2-encrypt-pass.jar` for only password decryption.
3. Use `tomcat-dbcp2-encrypt-user.jar` for username and password decryption.
4. Use `tomcat-dbcp2-encrypt-url.jar` for JDBC URL, username and password decryption.
5. All these jars support commandline encryption and decryption but with Tomcat connection pool only specific property decryption is supported. 
6. If you want only encrypted password then use `tomcat-dbcp2-encrypt-pass.jar` jar in your Tomcat `lib` directory and Tomcat `context.xml` should only have encrypted password value.
7. If you want only encrypted username and password then use `tomcat-dbcp2-encrypt-user.jar` jar in your Tomcat `lib` directory and Tomcat `context.xml` should only have encrypted username and password value.
8. If you want encrypted JDBC URL, username and password then use `tomcat-dbcp2-encrypt-url.jar` jar in your Tomcat `lib` directory and Tomcat `context.xml` should only have encrypted url, username and password value.
9. Basically these jar are created by commenting `encryption.decryptUsername(reference)` or `encryption.decryptPassword(reference)` or `encryption.decryptURL(reference)` from `org.apache.tomcat.dbcp.dbcp2.EncryptBasicDataSourceFactory` and `org.apache.tomcat.jdbc.pool.EncryptDataSourceFactory` files.
10. So it is advised to use appropriate jar based on your need otherwise incorrect jar can try decrypt non encrypted data and may result connection pool failure.
11. In `context.xml` add encrypted JDBC URL, username and password and put corresponding jar into your Tomcat `lib` directory.


## Choose Factory
In `context.xml` add factory according to your connection pool type
    
    If you want to use Tomcat JDBC Connection Pool
    Use: factory='org.apache.tomcat.jdbc.pool.EncryptDataSourceFactory'

    If you want to use Tomcat DBCP2 Connection Pool
    Use: factory='org.apache.tomcat.dbcp.dbcp2.EncryptBasicDataSourceFactory'


## Loggers
1. `umrao.encrypt.Encryption` with `Level.SEVERE`
2. `org.apache.tomcat.jdbc.pool.EncryptDataSourceFactory` with `Level.ALL`
3. `org.apache.tomcat.dbcp.dbcp2.EncryptBasicDataSourceFactory` with `Level.ALL`


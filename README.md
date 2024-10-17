# EncryptTomcatConnectionPool


## How To Build Project
1. Open this as project with `IntelliJ`
2. Copy latest `tomcat-dbcp.jar` and `tomcat-jdbc.jar` from Tomcat server `apache-tomcat-9.0.96/lib/` to project `libs` directory.
3. Refresh project so libs show up in the structure (right click on project top level, refresh/synchronize)
4. Expand libs and right-click on the jar > add as Library...


## How To Use Encryption/Decryption  
Encrypt/decrypt username, password and URL with below command. Use `-e` for encryption and `-d` for decryption. 

    $ java -jar  tomcat-cp-encrypt.jar -e/-d MSG_TO_ENCRYPT_DECRYPT

or

    $ java -cp tomcat-cp-encrypt.jar umrao.encrypt.EncryptDecrypt -e/-d MSG_TO_ENCRYPT_DECRYPT
encrypt or decrypt username, password and JDBC URL one by one
example:

To encrypt `username="DB_USERNAME"`

    $ java -jar tomcat-cp-encrypt.jar -e DB_USERNAME

To encrypt `password="DB_PASSWORD"`

    $ java -jar tomcat-cp-encrypt.jar -e DB_PASSWORD

To encrypt `url="DB_JDBC_URL"`

    $ java -jar tomcat-cp-encrypt.jar -e DB_JDBC_URL

This will generate encrypted string that can be used in resource
Or Run umrao/encrypt/EncryptDecrypt.java from IntelliJ with your input values by modifying it.


## Use Jar For Connection Pool
1. Either download specific jar for your use or create your own custom jar using source code.
2. Use `tomcat-cp-encrypt-pass.jar` for only password decryption.
3. Use `tomcat-cp-encrypt-user.jar` for username and password decryption.
4. Use `tomcat-cp-encrypt-url.jar` for JDBC URL, username and password decryption.
5. All these jars support commandline encryption and decryption but with Tomcat connection pool only specific property decryption is supported. 
6. If you want only encrypted password then use `tomcat-dbcp2-encrypt-pass.jar` jar in your Tomcat `lib` directory and Tomcat `context.xml` should only have encrypted password value.
7. If you want only encrypted username and password then use `tomcat-dbcp2-encrypt-user.jar` jar in your Tomcat `lib` directory and Tomcat `context.xml` should only have encrypted username and password value.
8. If you want encrypted JDBC URL, username and password then use `tomcat-dbcp2-encrypt-url.jar` jar in your Tomcat `lib` directory and Tomcat `context.xml` should only have encrypted url, username and password value.
9. Basically these jar are created by commenting `encryption.decryptUsername(reference)` or `encryption.decryptPassword(reference)` or `encryption.decryptURL(reference)` from `org.apache.tomcat.dbcp.dbcp2.EncryptBasicDataSourceFactory` and `org.apache.tomcat.jdbc.pool.EncryptDataSourceFactory` files.
10. So it is advised to use appropriate jar based on your need otherwise incorrect jar can try decrypt non encrypted data and may result connection pool failure.
11. In `context.xml` add encrypted JDBC URL, username and password and put corresponding jar into your Tomcat `lib` directory.


## Choose Factory
In `context.xml` add factory according to your connection pool type
    
If you want to use Tomcat JDBC Connection Pool then use

    factory="org.apache.tomcat.jdbc.pool.EncryptDataSourceFactory"

If you want to use Tomcat DBCP2 Connection Pool then use

    factory="org.apache.tomcat.dbcp.dbcp2.EncryptBasicDataSourceFactory"


## Loggers
1. `umrao.encrypt.Encryption` with `Level.SEVERE`
2. `org.apache.tomcat.jdbc.pool.EncryptDataSourceFactory` with `Level.ALL`
3. `org.apache.tomcat.dbcp.dbcp2.EncryptBasicDataSourceFactory` with `Level.ALL`


## Change Encryption Algorithm and Key
Modify `umrao.encrypt.Constants` according to your need and rebuild custom jar. You can modify below parameters according to your need-
1. `Encryption` algorithm
2. `Hashing` algorithm
3. Encryption `key`
4. `IV`


## How to build jar
1. Navigate to `File` > `Project Structure` > `Artifacts` > Under Artifacts click `+` > `JAR` > `From modules with dependency`
2. Provide name to build, same way you can also modify jar name. 
3. Make sure you have included `'EncryptTomcatConnectionPool' compile output` from right side `Available Elements` to left under jar name.
4. You may need to include `umrao.encrypt.EncryptDecrypt` class as main class.
5. Now you are ready for build `Build` > `Build artifacts...` > On the popup menu under `Build Artifacts` select the build name you gave in step 2 > under `Action` select `Build`.
6. You can find your jar under `out/artifacts/` directory.

# EncryptDBCP2

[How To Build Project]
1. Open this as project with IntelliJ
2. Copy latest apache-tomcat-9.0.96/lib/tomcat-dbcp.jar from Tomcat build to project libs directory.
3. Refresh project so libs show up in the structure (right click on project top level, refresh/synchronize)
4. Expand libs and right click on the jar

[How To Use Encryption/Decryption]   
Encrypt/decrypt username, password and URL through java -jar tomcat-dbcp-ext.jar -e/-d MSG_TO_ENCRYPT_DECRYPT. 
Or Run umrao/EncryptDecrypt.java from IntelliJ with your input values by modifying it.


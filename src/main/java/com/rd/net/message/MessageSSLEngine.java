package com.rd.net.message;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.FileInputStream;
import java.security.KeyStore;

public class MessageSSLEngine {

    private static SSLContext sslcontext;

    //-Djavax.net.debug=ssl,handshake
    static {
        try {
            sslcontext = SSLContext.getInstance("SSLv3");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            KeyStore ks = KeyStore.getInstance("JKS");
            String keyStorePath = "/opt/server/ssl/triumbest.net.jks";
            String keyStorePassword = "123456";
            ks.load(new FileInputStream(keyStorePath), keyStorePassword.toCharArray());
            String keyPassword = "123456";
            kmf.init(ks, keyPassword.toCharArray());
            sslcontext.init(kmf.getKeyManagers(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SSLEngine getSSLEngine() {
        SSLEngine sslEngine = sslcontext.createSSLEngine();
        sslEngine.setUseClientMode(false);
        sslEngine.setNeedClientAuth(false);
        return sslEngine;
    }
}

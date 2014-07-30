package com.cryptophoto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.cryptophoto.CryptoPhotoUtils.CryptoPhotoResponse;
import static java.lang.System.getProperty;
import static java.lang.System.out;

public class CPTest {

    static String publicKey;

    static String privateKey;

    static String userId;

    static String ip;

    static String selector;

    static String row;

    static String col;

    static String cph;

    static {
        // Try to read test parameters as Java system properties given from the command line (e.g. -DpublicKey=...)
        // or use defaults!

        publicKey = getProperty("publicKey", "efe925bda3bc2b5cd6fe3ad3661075a7");
        privateKey = getProperty("privateKey", "384b1bda2dafcd909f607083da22fef0");

        userId = getProperty("userId", "octavian");

        ip = getProperty("ip");
        if (ip == null || ip.trim().length() == 0) {
            InputStream in = null;
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://cp.vu/show_my_ip").openConnection();
                ip = new BufferedReader(new InputStreamReader(in = connection.getInputStream())).readLine();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        selector = getProperty("selector", "selector");
        row = getProperty("row", "row");
        col = getProperty("col", "col");
        cph = getProperty("cph", "cph");
    }

    public static void main(String[] args) throws Exception {
        // CryptoPhoto 'client':
        CryptoPhotoUtils cryptoPhoto = new CryptoPhotoUtils(publicKey, privateKey);

        // What's my visible IP?
        out.printf("Visible IP: %s%n%n", ip);

        // Establish a CryptoPhoto session:
        CryptoPhotoResponse cryptoPhotoSession = cryptoPhoto.getSession(userId, ip);

        if (!cryptoPhotoSession.is("valid")) {
            out.printf("CryptoPhoto session not established!%nERROR: %s, %s%n%n", cryptoPhotoSession.get("error"),
                       cryptoPhotoSession.get("signature"));
            return;
        }

        out.printf("CryptoPhoto session established.%nSID: %s, signature: %s, session has%s token%n%n",
                   cryptoPhotoSession.get("id"), cryptoPhotoSession.get("signature"),
                   cryptoPhotoSession.has("token") ? "" : " no");

        // See how CryptoPhoto widgets code would look like:
        out.println(cryptoPhoto.getTokenGenerationWidget(cryptoPhotoSession));
        out.println(cryptoPhoto.getChallengeWidget(cryptoPhotoSession));

        // Ask CryptoPhoto to verify a user response:
        // (! CODE SAMPLE ONLY! REQUIRES ACTUAL CORRECT USER INPUT TO SUCCEED !)
        CryptoPhotoResponse cryptoPhotoVerification = cryptoPhoto.verify("selector", "row", "col", "cph", userId, ip);

        boolean success = cryptoPhotoVerification.is("valid");
        out.printf("%nCryptoPhoto verification %s: %s%n%n", success ? "succeeded" : "failed",
                   success ? cryptoPhotoVerification.get("message") : cryptoPhotoVerification.get("error"));
    }
}

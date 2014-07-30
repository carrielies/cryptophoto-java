package com.cryptophoto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import static com.cryptophoto.CryptoPhotoUtils.CryptoPhotoResponse;
import static java.lang.System.out;

public class CPTest {

    static {
        System.setProperty("http.proxyHost", "158.169.131.13");
        System.setProperty("http.proxyPort", "8012");
        System.setProperty("http.proxyUser", "nitanoc");
        System.setProperty("http.proxyPassword", "th81on$");
        System.setProperty("https.proxyHost", "158.169.131.13");
        System.setProperty("https.proxyPort", "8012");
        System.setProperty("https.proxyUser", "nitanoc");
        System.setProperty("https.proxyPassword", "th81on$");

        Authenticator.setDefault(new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("nitanoc", "th81on$".toCharArray());
            }
        });
    }

    static String visibleIp() {
        String ip = null;

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

        return ip;
    }

    public static void main(String[] args) throws Exception {
        CryptoPhotoUtils cryptoPhoto =
            new CryptoPhotoUtils("efe925bda3bc2b5cd6fe3ad3661075a7", "384b1bda2dafcd909f607083da22fef0");

        String ip = visibleIp();
        out.printf("Visible IP: %s%n%n", ip);

        CryptoPhotoResponse cryptoPhotoSession = cryptoPhoto.getSession("octavian", ip);

        if (!cryptoPhotoSession.is("valid")) {
            out.printf("CryptoPhoto session is not valid!%nERROR: %s, %s%n%n", cryptoPhotoSession.get("error"),
                       cryptoPhotoSession.get("signature"));
            return;
        }

        out.printf("CryptoPhoto session is valid.%nSID: %s, signature: %s, session has%s token%n%n",
                   cryptoPhotoSession.get("id"), cryptoPhotoSession.get("signature"),
                   cryptoPhotoSession.has("token") ? "" : " no");

        out.println(cryptoPhoto.getTokenGenerationWidget(cryptoPhotoSession));

        out.println(cryptoPhoto.getChallengeWidget(cryptoPhotoSession));
    }
}

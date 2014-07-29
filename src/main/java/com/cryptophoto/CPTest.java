package com.cryptophoto;

import static com.cryptophoto.CryptoPhotoUtils.CryptoPhotoSession;

public class CPTest {

    //    static {
    //        // Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("158.169.131.13", 8012));
    //
    //        System.setProperty("http.proxyHost", "158.169.131.13");
    //        System.setProperty("http.proxyPort", "8012");
    //        System.setProperty("http.proxyUser", "nitanoc");
    //        System.setProperty("http.proxyPassword", "th81on$");
    //        System.setProperty("https.proxyHost", "158.169.131.13");
    //        System.setProperty("https.proxyPort", "8012");
    //        System.setProperty("https.proxyUser", "nitanoc");
    //        System.setProperty("https.proxyPassword", "th81on$");
    //
    //        Authenticator.setDefault(new Authenticator() {
    //
    //            @Override
    //            protected PasswordAuthentication getPasswordAuthentication() {
    //                return new PasswordAuthentication("nitanoc", "th81on$".toCharArray());
    //            }
    //        });
    //    }

    public static void main(String[] args) throws Exception {
        CryptoPhotoUtils cryptoPhoto =
            new CryptoPhotoUtils("efe925bda3bc2b5cd6fe3ad3661075a7", "384b1bda2dafcd909f607083da22fef0");

        CryptoPhotoSession cryptoPhotoSession = cryptoPhoto.getSession("octavian", "87.65.63.97");

        if (!cryptoPhotoSession.isValid) {
            System.out
                .printf("CryptoPhoto session is not valid, ERROR: %s%n(Signature: %s)%n%n", cryptoPhotoSession.error,
                        cryptoPhotoSession.signature);
            return;
        }

        System.out.printf("CryptoPhoto session is valid, SID: %s%n(Signature: %s)%n%n", cryptoPhotoSession.id,
                          cryptoPhotoSession.signature);

        System.out.println(cryptoPhoto.getTokenGenerationWidget(cryptoPhotoSession));

        System.out.println(cryptoPhoto.getChallengeWidget(cryptoPhotoSession));
    }
}

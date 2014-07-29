package com.cryptophoto;

import static com.cryptophoto.CryptoPhotoUtils.CryptoPhotoSession;

public class InvalidCryptoPhotoSession extends Exception {

    private CryptoPhotoSession cryptoPhotoSession;

    public InvalidCryptoPhotoSession(CryptoPhotoSession cryptoPhotoSession) {
        this();
        this.cryptoPhotoSession = cryptoPhotoSession;
    }

    public InvalidCryptoPhotoSession() {
        super("no valid CryptoPhoto session could be established");
    }

    @Override
    public String getMessage() {
        return super.getMessage() +
               (cryptoPhotoSession != null && cryptoPhotoSession.error != null ? ": " + cryptoPhotoSession.error : "");
    }
}

package com.cryptophoto;

import static com.cryptophoto.CryptoPhotoUtils.CryptoPhotoSession;

public class CryptoPhotoInvalidSession extends Exception {

    private CryptoPhotoSession cryptoPhotoSession;

    public CryptoPhotoInvalidSession(CryptoPhotoSession cryptoPhotoSession) {
        this();
        this.cryptoPhotoSession = cryptoPhotoSession;
    }

    public CryptoPhotoInvalidSession() {
        super("no valid CryptoPhoto session could be established");
    }

    @Override
    public String getMessage() {
        return super.getMessage() +
               (cryptoPhotoSession != null && cryptoPhotoSession.error != null ? ": " + cryptoPhotoSession.error : "");
    }
}

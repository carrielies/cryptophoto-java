/*
 * This is a Java library that handles calling CryptoPhoto.
 *   - Main Page
 *       http://cryptophoto.com/
 *   - About CryptoPhoto
 *       http://cryptophoto.com/about
 *   - Register to CryptoPhoto
 *       http://cryptophoto.com/admin/register
 *
 * Copyright (C) 2014 Cryptophoto.com. All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.cryptophoto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static java.net.URLEncoder.encode;

/**
 * Helper class that handles calling the <a href="http://cryptophoto.com/admin/api">CryptoPhoto API</a>.
 *
 * @author <a href="http://cryptophoto.com">CryptoPhoto</a>,
 *         <a href="mailto:tech@cryptophoto.com">tech@cryptophoto.com</a>
 * @version 1.20140728
 */
public class CryptoPhotoUtils {

    private static final char[] HEX = "0123456789ABCDEF".toCharArray();
    private final String server;
    private final String publicKey;
    private final byte[] privateKey;
    private Mac mac; // used to sign outgoing data

    public CryptoPhotoUtils(String publicKey, String privateKey) throws InvalidKeyException {
        this(null, publicKey, privateKey);
    }

    public CryptoPhotoUtils(String server, String publicKey, String privateKey) throws InvalidKeyException {
        if (publicKey == null || privateKey == null) {
            throw new NullPointerException("cannot use null public or private CryptoPhoto keys");
        }

        this.server = server == null || (server = server.trim()).length() == 0 ? "http://cryptophoto.com" : server;
        this.publicKey = publicKey;
        this.privateKey = privateKey.getBytes();

        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(this.privateKey, "HmacSHA1"));
        } catch (NoSuchAlgorithmException e) {
            // cannot happen since we hard-code the algorithm
        }
    }

    public CryptoPhotoSession getSession(String userId, String ip)
        throws IOException, CryptoPhotoResponseParseException {
        long time = new Date().getTime() / 1000L; // number of seconds since epoch...

        String signature =
            sign(new StringBuilder(new String(privateKey)).append(time).append(userId).append(publicKey).toString());

        String data = new StringBuilder("publickey=").append(encode(publicKey, "UTF-8")).append("&uid=")
                                                     .append(encode(userId, "UTF-8")).append("&time=").append(time)
                                                     .append("&signature=").append(encode(signature, "UTF-8"))
                                                     .append("&ip=").append(encode(ip, "UTF-8")).toString();

        URL url = new URL(server + "/api/get/session");

        return new CryptoPhotoSession(post(url, data.getBytes()));
    }

    protected String sign(String data) {
        if (data == null) {
            return null;
        }

        byte[] bytes = mac.doFinal(data.getBytes());
        char[] chars = new char[bytes.length * 2];

        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            chars[i * 2] = HEX[v >>> 4];
            chars[i * 2 + 1] = HEX[v & 0x0F];
        }

        return new String(chars);
    }

    protected String post(URL url, byte[] data) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Length", String.valueOf(data.length));
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        OutputStream out = connection.getOutputStream();
        try {
            out.write(data);
            out.flush();
        } finally {
            out.close();
        }

        StringBuilder response = new StringBuilder();

        InputStream in = connection.getInputStream();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                response.append(line).append("\n");
            }
        } finally {
            in.close();
        }

        return response.toString();
    }

    public String getTokenGenerationWidget(CryptoPhotoSession cryptoPhotoSession) throws InvalidCryptoPhotoSession {
        if (cryptoPhotoSession == null) {
            throw new NullPointerException("cannot obtain a token generation widget using a null CryptoPhoto session");
        }

        if (!cryptoPhotoSession.isValid) {
            throw new InvalidCryptoPhotoSession(cryptoPhotoSession);
        }

        return "<script type=\"text/javascript\" src=\"" + server + "/api/token?sd=" + cryptoPhotoSession.id +
               "\"></script>";
    }

    public String getChallengeWidget(CryptoPhotoSession cryptoPhotoSession) throws InvalidCryptoPhotoSession {
        if (cryptoPhotoSession == null) {
            throw new NullPointerException("cannot obtain a challenge widget using a null CryptoPhoto session");
        }

        if (!cryptoPhotoSession.isValid) {
            throw new InvalidCryptoPhotoSession(cryptoPhotoSession);
        }

        return "<script type=\"text/javascript\" src=\"" + server + "/api/challenge?sd=" + cryptoPhotoSession.id +
               "\"></script>";
    }

    /**
     * Immutable CryptoPhoto session abstraction.
     *
     * @author <a href="http://cryptophoto.com">CryptoPhoto</a>,
     *         <a href="mailto:tech@cryptophoto.com">tech@cryptophoto.com</a>
     * @version 1.20140728
     */
    public static class CryptoPhotoSession {

        public final String id;

        public final String error;

        public final boolean isValid;

        public final boolean hasToken;

        public final String signature;

        /**
         * Initializes a {@link CryptoPhotoSession} by parsing a CryptoPhoto response to an <code>api/get/session</code>
         * API request.
         *
         * @param cpResponse CryptoPhoto response to an <code>/get/session</code> API request call
         */
        public CryptoPhotoSession(String cpResponse) throws CryptoPhotoResponseParseException {
            if (cpResponse == null) {
                throw new NullPointerException("cannot parse a null CryptoPhoto response");
            }

            String[] lines = cpResponse.split("(\\r?\\n)+");
            if (lines.length < 2) {
                throw new CryptoPhotoResponseParseException("unexpected CryptoPhoto response length (< 2 lines)");
            }

            String status = lines[0].trim().toLowerCase();
            switch (status) { // requires Java 7; if not available, just use if/else-if/else with .equals()
            case "success":
                id = lines[1].trim();
                error = null;
                isValid = true;
                break;
            case "error":
                id = null;
                error = lines[1].trim();
                isValid = false;
                break;
            default:
                throw new CryptoPhotoResponseParseException("unexpected CryptoPhoto response status: " + status);
            }

            hasToken = lines.length > 2 ? "true".equalsIgnoreCase(lines[2].trim()) : false;
            signature = lines.length > 3 ? lines[3].trim() : null;
        }
    }
}

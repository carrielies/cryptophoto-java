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
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Formatter;

import static java.lang.String.format;

/**
 * Helper class that handles calling the <a href="http://cryptophoto.com/admin/api">CryptoPhoto API</a>.
 *
 * @author <a href="http://cryptophoto.com">CryptoPhoto</a>,
 *         <a href="mailto:tech@cryptophoto.com">tech@cryptophoto.com</a>
 * @version 1.20140728
 */
public class CryptoPhotoUtils {

    private final String server;

    private final String publicKey;

    private final byte[] privateKey;

    private Mac mac; // used to sign outgoing data

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
        } catch (NoSuchAlgorithmException e) { // cannot happen since we hard-code the algorithm
            e.printStackTrace();
        }
    }

    public CryptoPhotoUtils(String publicKey, String privateKey) throws InvalidKeyException {
        this(null, publicKey, privateKey);
    }

    public CryptoPhotoSession getSession(String userId, String ip)
        throws IOException, CryptoPhotoResponseParseException {
        long time = new Date().getTime();

        URL url = new URL(server + "/api/get/session");

        String data = format("publickey=%s&uid=%s&time=%d&signature=%s&ip=%s", publicKey, userId, time, sign(
            new StringBuilder().append(privateKey).append(time).append(userId).append(publicKey).toString()), ip);

        return new CryptoPhotoSession(postRequest(url, data));
    }

    protected String sign(String data) {
        Formatter formatter = new Formatter();
        for (byte b : mac.doFinal(data.getBytes())) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    protected String postRequest(URL url, String data) throws IOException {
        String encodedData = URLEncoder.encode(data, "UTF-8");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));

        OutputStream out = connection.getOutputStream();
        out.write(encodedData.getBytes());
        out.flush();
        out.close();

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            response.append(line).append("\n");
        }
        rd.close();
        return response.toString();
    }

    /**
     * Will typically work; otherwise, check out Stephen C's answer at:
     * http://stackoverflow.com/questions/9481865/how-to-get-ip-address-of-current-machine-using-java
     */
    public static String getLocalHostIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
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

        //public final boolean hasToken;

        //public final String signature;

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
            //if (lines.length < 4) { // expect / parse at least 4 lines since we might want to check the signature:
            //    throw new CryptoPhotoResponseParseException("unexpected CryptoPhoto response length (< 4 lines)");
            //}

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
                throw new CryptoPhotoResponseParseException("unexpected CryptoPhoto response first line: " + status);
            }

            //hasToken = "true".equalsIgnoreCase(lines[2].trim());
            //signature = lines[3].trim();
        }
    }
}

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

/**
 * Helper class that handles calling the <a href="http://cryptophoto.com/admin/api>CryptoPhoto API</a>.
 *
 * @author CryptoPhoto http://cryptophoto.com <a href="mailto:tech@cryptophoto.com">tech@cryptophoto.com</a>
 * @version 1.0, Jul 28, 2014
 */
public class CryptoPhotoUtils {

    private String server;

    private byte[] privateKey;

    private String publicKey;

    /**
     * Immutable CryptoPhoto session abstraction.
     */
    public static class CryptoPhotoSession {

        private final String id;

        private final String error;

        private final boolean isValid;

        private final boolean hasToken;

        public CryptoPhotoSession(String id, String error, boolean isValid, boolean hasToken) {
            this.id = id;
            this.error = error;
            this.isValid = isValid;
            this.hasToken = hasToken;
        }

        /**
         * Initializes a {@link CryptoPhotoSession} by parsing a CryptoPhoto response to an <code>api/get/session</code>
         * API request.
         *
         * @param cpResponse CryptoPhoto response to an <code>api/get/session</code> API request call
         */
        public CryptoPhotoSession(String cpResponse) {
            if (cpResponse == null) {
                throw new NullPointerException("cannot parse a null CryptoPhoto response");
            }

            String []lines = cpResponse.split("(\\r?\\n)+");
            if(lines.length < 4) {
                throw new IllegalArgumentException("unexpected CryptoPhoto response length (< 4 lines)");
            }

            String status = lines[0].trim().toLowerCase();
            switch(status) { // requires Java 7; if not available, just use if/else-if/else with .equals()
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
                id = error = null;
                isValid = false;
                // throw new IllegalArgumentException("unexpected CryptoPhoto response line at position 0 (" + id + ")");
                // add logging???
            }

            this.hasToken = "true".equalsIgnoreCase(lines[2].trim());
        }
    }

    public static void main(String []args) {
        String []lines = "abc\n123\r\n \t qwq\r".split("(\\r?\\n)+");
        for (String line : lines) {
            System.out.println("[" + line.trim() + "]");
        }
    }
}

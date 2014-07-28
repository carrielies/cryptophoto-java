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

    public static class CryptoPhotoSession {

        private String id;

        private boolean valid;

        private boolean token;

        private String error;
    }
}

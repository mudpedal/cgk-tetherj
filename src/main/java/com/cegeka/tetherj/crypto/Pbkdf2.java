package com.cegeka.tetherj.crypto;

/*
 * Copyright (c) 2012
 * Cole Barnes [cryptofreek{at}gmail{dot}com]
 * http://cryptofreek.org/
 * 
 */

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Pbkdf2 {
    /* START RFC 2898 IMPLEMENTATION */

    /**
     * Derive a key.
     * 
     * @param password
     *            password from which to derive a key.
     * @param salt
     *            Salt for the derivation.
     * @param iterations
     *            Number of iterations.
     * @param keyLength
     *            Derived key length.
     * @return Returns the derived key.
     */
    public static byte[] derive(String password, byte[] salt, int iterations, int keyLength) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            int blockLength = 20; // hlen

            if (keyLength > ((Math.pow(2, 32)) - 1) * blockLength) {
                System.out.println("derived key too long");
            } else {
                int length = (int) Math.ceil((double) keyLength / (double) blockLength);
                // int r = dkLen - (l-1)*hLen;

                for (int i = 1; i <= length; i++) {
                    byte[] block = xor(password, salt, iterations, i);
                    baos.write(block);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        byte[] baDerived = new byte[keyLength];
        System.arraycopy(baos.toByteArray(), 0, baDerived, 0, baDerived.length);

        return baDerived;
    }

    private static byte[] xor(String password, byte[] salt, int iterations, int offset)
            throws Exception {
        byte[] lastIteration = null;
        byte[] xor = null;

        SecretKeySpec key = new SecretKeySpec(password.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance(key.getAlgorithm());
        mac.init(key);

        for (int j = 0; j < iterations; j++) {
            if (j == 0) {
                byte[] baS = salt;
                byte[] baI = intToBytes(offset);
                byte[] baU = new byte[baS.length + baI.length];

                System.arraycopy(baS, 0, baU, 0, baS.length);
                System.arraycopy(baI, 0, baU, baS.length, baI.length);

                xor = mac.doFinal(baU);
                lastIteration = xor;
                mac.reset();
            } else {
                byte[] baU = mac.doFinal(lastIteration);
                mac.reset();

                for (int k = 0; k < xor.length; k++) {
                    xor[k] = (byte) (xor[k] ^ baU[k]);
                }

                lastIteration = baU;
            }
        }

        return xor;
    }

    private static byte[] intToBytes(int integerValue) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(integerValue);

        return bb.array();
    }

}
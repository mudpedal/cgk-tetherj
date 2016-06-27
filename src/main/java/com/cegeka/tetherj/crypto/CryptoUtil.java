package com.cegeka.tetherj.crypto;

import java.math.BigInteger;
import java.util.Formatter;

/**
 * Util for both crypto and decoding.
 *
 * @author Andrei Grigoriu
 *
 */
public class CryptoUtil {

    /**
     * Convert bytes to hex + 0x in front.
     *
     * @param data
     *            to convert
     * @return hex string
     */
    public static String byteToHexWithPrefix(final byte[] data) {
        return "0x" + byteToHex(data);
    }

    /**
     * Convert bytes to hex without (no 0x in front).
     *
     * @param data
     *            to convert
     * @return hex strng
     */
    public static String byteToHex(final byte[] data) {
        Formatter formatter = new Formatter();
        for (byte b : data) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    /**
     * Converts hex to bytes.
     *
     * @param hex
     *            to decoded (may have 0x in front)
     * @return byte data
     */
    public static byte[] hexToBytes(String hex) {
        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }
        return hexToBytes(hex.toCharArray());
    }

    /**
     * Converts hex to bytes.
     *
     * @param hex
     *            to decoded (may have 0x in front)
     * @return byte data
     */
    public static byte[] hexToBytes(char[] hex) {
        if (hex.length % 2 != 0) {
            throw new IllegalArgumentException("Must pass an even number of characters.");
        }

        int length = hex.length >> 1;
        byte[] raw = new byte[length];
        for (int o = 0, i = 0; o < length; o++) {
            raw[o] = (byte) ((getHexCharValue(hex[i++]) << 4) | getHexCharValue(hex[i++]));
        }
        return raw;
    }

    /**
     * Convert hex to BigInteger.
     *
     * @param hex
     *            to convert
     * @return biginteger from hex
     */
    public static BigInteger hexToBigInteger(String hex) {
        if (hex == null) {
            return null;
        }

        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }

        // remove starting zeros
        while (hex.length() > 1 && hex.charAt(0) == '0') {
            hex = hex.substring(1);
        }

        return new BigInteger(hex, 16);
    }

    /**
     * Get associated byte for char.
     * @param ch char
     * @return byte associated with char
     */
    public static byte getHexCharValue(char ch) {
        if (ch >= '0' && ch <= '9') {
            return (byte) (ch - '0');
        }
        if (ch >= 'A' && ch <= 'F') {
            return (byte) (10 + ch - 'A');
        }
        if (ch >= 'a' && ch <= 'f') {
            return (byte) (10 + ch - 'a');
        }
        throw new IllegalArgumentException("Invalid hex character");
    }

    /**
     * Get the address as short string.
     * @param ethereumAddress Address as big integer.
     * @return short string represent 1f21c...
     */
    public static String bigIntegerToAddress(BigInteger ethereumAddress) {
        String address = ethereumAddress.toString(16);

        while (address.length() < 40) {
            address = "0" + address;
        }

        if (!isValidAddress(address)) {
            throw new Error("not an address");
        }

        return "0x" + address;
    }

    /**
     * Returns true if address is a valid ethereum address.
     * @param address to check
     * @return true if valid
     */
    public static boolean isValidAddress(byte[] address) {
        return address != null && address.length == 20;
    }

    /**
     * Returns true if address is a valid ethereum address.
     * @param address to check
     * @return true if valid
     */
    public static boolean isValidAddress(String address) {
        return address != null && address.length() == 40;
    }
}

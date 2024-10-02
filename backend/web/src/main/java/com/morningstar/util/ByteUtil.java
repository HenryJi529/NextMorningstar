package com.morningstar.util;

public class ByteUtil {
    public static String byteToHexString(byte aByte) {
        String str = Integer.toHexString(aByte);
        if (str.length() == 1) {
            return "0" + str;
        } else if (str.length() == 2) {
            return str;
        } else {
            return str.substring(str.length() - 2);
        }
    }
}

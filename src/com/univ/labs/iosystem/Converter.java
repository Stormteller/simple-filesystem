package com.univ.labs.iosystem;

import java.nio.ByteBuffer;

/**
 * Created by Masha Kereb on 24-Apr-17.
 */
public class Converter {
    static byte[] intToByteArray(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }


    static int intFromByteArray(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }
}

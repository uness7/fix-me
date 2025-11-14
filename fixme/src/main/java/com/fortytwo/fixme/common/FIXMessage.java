package com.fortytwo.fixme.common;

import java.nio.ByteBuffer;

public class FIXMessage {
    public static ByteBuffer addHeader(byte[] message) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES + message.length);
        byteBuffer.putInt(message.length);
        byteBuffer.put(message);
        byteBuffer.flip();
        return byteBuffer;
    }
    public String[] getPairs(String message) {
        return message.split("\\|");
    }
}

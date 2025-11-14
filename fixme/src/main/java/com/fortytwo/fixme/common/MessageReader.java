package com.fortytwo.fixme.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class MessageReader {

    private ByteBuffer headerBuffer = ByteBuffer.allocate(Integer.BYTES);
    private ByteBuffer bodyBuffer = null;
    private int expectedBodyLength = -1;

    public byte[] read(SocketChannel channel) throws IOException {
        if (expectedBodyLength == -1) {
            if (headerBuffer.hasRemaining()) {
                int bytesRead = channel.read(headerBuffer);
                if (bytesRead == -1) {
                    throw new IOException("Connection closed.");
                }
                if (headerBuffer.hasRemaining()) {
                    return null;
                }
            }
            headerBuffer.flip();
            expectedBodyLength = headerBuffer.getInt();
            bodyBuffer = ByteBuffer.allocate(expectedBodyLength);
            headerBuffer.clear();
        }

        if (bodyBuffer.hasRemaining()) {
            int bytesRead = channel.read(bodyBuffer);
            if (bytesRead == -1) {
                throw new IOException("Connection closed.");
            }
            if (bodyBuffer.hasRemaining()) {
                return null;
            }
        }

        bodyBuffer.flip();
        byte[] message = new byte[expectedBodyLength];
        bodyBuffer.get(message);

        bodyBuffer = null;
        expectedBodyLength = -1;

        return message;
    }
}
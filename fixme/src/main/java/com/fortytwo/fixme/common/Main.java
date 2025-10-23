package com.fortytwo.fixme.common;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Main {

    // understanding relative and absolute positioning
    public static void ex5() {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        String message = "Hello";
        System.out.println(buffer.position());
        buffer.put(message.getBytes());
        buffer.flip();
        byte x = buffer.get(0);
        System.out.println((char) x);
        System.out.println(buffer.position());
        buffer.put(0, (byte) 'O');
        System.out.println(buffer.position());
    }

    public static void ex4() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        String message = "Hello hell";
        buffer.put(message.getBytes());
        buffer.flip(); // switch to reading mode
        byte[] arr = new byte[5];
        buffer.get(arr);
//        buffer.compact();
        buffer.clear();
//        buffer.get(arr);
        System.out.println(new String(arr));
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        byte[] y = new byte[5];
        buffer.get(y);
        System.out.println(new String(y));
    }


    public static void main(String[] args) {
//        ex5();
        ex4();
        //ByteBuffer buffer = ByteBuffer.allocate(1024);
//        int x = 23;
//        long y = 999999999L;
//        buffer.putInt(x);
//        buffer.putLong(y);
//        System.out.println(buffer.limit());
//        System.out.println(buffer.capacity());
//        buffer.flip();
//        System.out.println(buffer.limit());
//        System.out.println(buffer.capacity());
//        System.out.println("Int " + buffer.getInt());
//        System.out.println("Long " + buffer.getLong());
//        buffer.flip();
//        System.out.println(buffer.limit());
//        System.out.println(buffer.capacity());
//
//        String message = "Hello from Broker";
//        byte[] messagesBytes = message.getBytes();
//        buffer.put(messagesBytes);
//        System.out.println("buffer position " + buffer.position());
//        buffer.flip(); // limit becomes message.length
//        System.out.println("buffer position " + buffer.position());
//        System.out.println("buffer.remaining = " + buffer.remaining());
//        byte[] remaining = new byte[buffer.remaining()];
//        // how to read from a buffer into a byte[] array?
//        buffer.get(remaining);
//        System.out.println("Remaining length " + remaining.length);
//        // convert a byte array to a String
//        String remainingString = new String(remaining);
//        System.out.println("Remaining string: " + remainingString);
    }
}

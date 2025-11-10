package com.fortytwo.fixme.common;

import com.fortytwo.fixme.router.ClientType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public abstract class Client {
    protected String name;
    protected ClientType clientType;
    protected InetSocketAddress address;
    protected static long uniqueId = 111111;

    public Client(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public long getUniqueId() {
        long result = uniqueId;
        uniqueId++;
        return result;
    }

    public void activate(int port) throws IOException {
        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(Utils.HOST, port));
        System.out.println(name + " trying to connect to Router");
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        while (true) {
            if (selector.select() == 0) {
                continue;
            }
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isConnectable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    if (client.finishConnect()) {
                        System.out.println(name + " connected successfully to Router");
                        key.interestOps(SelectionKey.OP_READ |  SelectionKey.OP_WRITE);
                    } else {
                        System.err.println("Failed to connect.");
                        return;
                    }
                } else if (key.isReadable()) {
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int bytesRead = clientChannel.read(buffer);
                    if (bytesRead > 0) {
                        buffer.flip();
                        byte[] data = new byte[buffer.remaining()];
                        buffer.get(data);
                        System.out.println("Received message from router: " + new String(data));
                    } else if (bytesRead == -1) {
                        System.out.println("Router has closed the connection.");
                        clientChannel.close();
                        return;
                    }
                    key.interestOps(SelectionKey.OP_WRITE);
                } else if (key.isWritable()) {
                    SocketChannel socket = (SocketChannel) key.channel();
                    String message = "Great doing business with you Router";
                    byte[] arr = message.getBytes();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(arr.length);
                    byteBuffer.put(arr);
                    byteBuffer.flip();
                    socket.write(byteBuffer);
                    key.interestOps(SelectionKey.OP_READ);
                    socket.close();
                }
                iterator.remove();
            }
        }
    }

    public void log(String message, String sender) {
        System.out.println("[" + sender + "]: " + message);
    }
}

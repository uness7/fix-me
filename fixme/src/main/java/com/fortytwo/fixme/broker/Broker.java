package com.fortytwo.fixme.broker;

import com.fortytwo.fixme.router.Router;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class Broker {
    private final String HOST = "localhost";
    private final int PORT = 5000;
    private final String name;
    private long uniqueId = 1;

    public Broker(String name) {
        this.name = name;
    }

    public void activate() throws IOException {
        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(HOST, PORT));
        System.out.println("[Broker]: " + name + " trying to connect to Router");
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
                        System.out.println("[Broker]: " + name + " connected successfully to Router");
                        key.interestOps(SelectionKey.OP_READ |  SelectionKey.OP_WRITE);
                    } else {
                        System.err.println("[Broker]: Failed to connect.");
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
                        System.out.println("[Broker]: Received message from router: " + new String(data));
                    } else if (bytesRead == -1) {
                        System.out.println("[Broker]: Router has closed the connection.");
                        clientChannel.close();
                        return;
                    }
                } else if (key.isWritable()) {
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    String message = "Hello from Broker " + name;
                    ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
                    clientChannel.write(buffer);
                    System.out.println("[Broker]: Sent message: '" + message + "'");
                    key.interestOps(SelectionKey.OP_READ);
                }
                iterator.remove();
            }
        }
    }

    public void makeOrder(String message) throws IOException {
    }

    public void listenForAckMessage() throws IOException {
    }

    public void sendBuyRequest(String message) throws IOException {
    }

    public long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return this.name;
    }

    public static void main(String[] args) {
        Broker br = new Broker("NASDAQ");
        try {
            br.activate();
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}

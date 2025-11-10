package com.fortytwo.fixme.router;

import com.fortytwo.fixme.common.Client;
import com.fortytwo.fixme.common.Utils;
import jdk.jshell.execution.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Router {
    private static Router instance = null;
    private LinkedList<Client> clients = new LinkedList<>();

    private Router() {
    }

    public static Router getInstance() {
        if (instance == null) {
            instance = new Router();
        }
        return instance;
    }

    private void activate() throws IOException {
        Selector selector = Selector.open();
        System.out.println("[Router]: listening on [port] " + Utils.BROKER_PORT + " && on " + Utils.BROKER_PORT);
        ServerSocketChannel brokerServerSocketChannel = ServerSocketChannel.open();
        brokerServerSocketChannel.bind(new InetSocketAddress(Utils.BROKER_PORT));
        brokerServerSocketChannel.configureBlocking(false);
        brokerServerSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        ServerSocketChannel marketServerSocketChannel = ServerSocketChannel.open();
        marketServerSocketChannel.bind(new InetSocketAddress(Utils.MARKET_PORT));
        marketServerSocketChannel.configureBlocking(false);
        marketServerSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer buffer = ByteBuffer.allocate(Utils.BUFFSIZE);

        while (true) {
            if (selector.select() == 0) continue;
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = serverSocketChannel1.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_WRITE);
                    System.out.println("[Router]: New connection established from " +
                            socketChannel.getRemoteAddress());
                } else if (key.isWritable()) {
                    SocketChannel socket = (SocketChannel) key.channel();
                    send("Welcome to Router!", socket);
                    key.interestOps(SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    SocketChannel socket = (SocketChannel) key.channel();
                    final int localPort = brokerServerSocketChannel.socket().getLocalPort();

                    String message = rcvMessage(socket);
                    if (message == null) {
                        System.out.println("[Router]: Connection closed");
                        socket.close();
                        return;
                    }

                    if (localPort == Utils.BROKER_PORT) {
                        handleRequest(message);
                    } else if (localPort == Utils.MARKET_PORT) {
                        handleResponse(message);
                    }

                    key.interestOps(SelectionKey.OP_WRITE);
                    socket.close();
                }
                keyIterator.remove();
            }
        }
    }

    private void send(String message, SocketChannel socket) throws IOException {
        byte[] arr = message.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(arr.length);
        byteBuffer.put(arr);
        byteBuffer.flip();
        socket.write(byteBuffer);
    }

    private void handleResponse(String message) {
        System.out.println("[Router]: Received a response from the Market" + message);
    }

    private void handleRequest(String message) {
        System.out.println("[Router]: Received a request from a Broker" + message);
        // 1. validate the checksum
        // 1.1 parsing of the message
        // 1.2 get pairs
        // 1.3 validate the checksum

        // 2. identify the destination
        // 2.1 get pairs -> get destination pair

        // 3. forward the message
//        forwardMessage(message)
    }

    private String rcvMessage(SocketChannel socket) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = socket.read(buffer);
        if (bytesRead == -1) {
            return null;
        } else {
            buffer.flip();
            byte[] arr = new byte[buffer.remaining()];
            buffer.get(arr);
            return new String(arr);
        }
    }

    public static void main(String[] args) {
        Router router = Router.getInstance();
        try {
            router.activate();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
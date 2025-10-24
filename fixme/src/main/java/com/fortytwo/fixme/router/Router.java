package com.fortytwo.fixme.router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Router {
    private static Router instance = null;

    private Router() {
    }

    public static Router getInstance() {
        if (instance == null) {
            instance = new Router();
        }
        return instance;
    }

    private void activate(int port) throws IOException {
        System.out.println("[Router]: listening on port " + port);
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            if (selector.select() == 0) {
                continue;
            }
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = serverSocketChannel1.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("[Router]: New connection established from " + socketChannel.getRemoteAddress());
                } else if (key.isReadable()) {
                } else if (key.isWritable()) {
                }
                keyIterator.remove();
            }
        }
    }

    public static void main(String[] args) {
        Router router = Router.getInstance();
        new Thread(() -> {
            try {
                router.activate(5000);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }).start();
        new Thread(() -> {
            try {
                router.activate(5001);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }).start();
    }
}
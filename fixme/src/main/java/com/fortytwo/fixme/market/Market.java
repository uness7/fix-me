package com.fortytwo.fixme.market;

import com.fortytwo.fixme.common.Instrument;
import com.fortytwo.fixme.common.Utils;
import com.fortytwo.fixme.router.Request;
import com.fortytwo.fixme.router.Router;

import java.awt.desktop.OpenURIEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class Market {
    public LinkedList<Instrument> instrumentsList;
    private String name;

    public Market(String name, LinkedList<Instrument> instrumentsList) {
        this.name = name;
        this.instrumentsList = instrumentsList;
    }

    public void activate() throws IOException {
        int PORT = 5001;
        String HOST = "localhost";
        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(HOST, PORT));
        System.out.println("[Market]: " + name + " trying to connect to Router");
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
                        System.out.println("[Market]: " + name + " connected successfully to Router");
                        key.interestOps(SelectionKey.OP_READ |  SelectionKey.OP_WRITE);
                    } else {
                        System.err.println("[Market]: Failed to connect.");
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
                        System.out.println("[Market]: Received message from router: " + new String(data));
                    } else if (bytesRead == -1) {
                        System.out.println("[Market]: Router has closed the connection.");
                        clientChannel.close();
                        return;
                    }
                } else if (key.isWritable()) {
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    String message = "Hello from Market" + name;
                    ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
                    clientChannel.write(buffer);
                    System.out.println("[Market]: Sent message: '" + message + "'");
                    key.interestOps(SelectionKey.OP_READ);
                }
                iterator.remove();
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    private void handleWriteEvent() {
        System.out.println("handle write event");
    }

    private void handleReadEvent() {
        System.out.println("handle read event");
    }

    public static void main(String[] args) {
        Market market = new Market("mr", Utils.getInstruments("/waizi/home/Desktop/42PostCommonCore/fix-me/fixe-me/config.tt"));
        try {
            market.activate();
        } catch (IOException e) {
            System.out.println("Exception " + e.getMessage());
        }
    }
}
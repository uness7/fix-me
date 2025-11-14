package com.fortytwo.fixme.broker;

import com.fortytwo.fixme.common.FIXMessage;
import com.fortytwo.fixme.common.MessageReader;
import com.fortytwo.fixme.common.PendingChange;
import com.fortytwo.fixme.common.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class Broker implements Runnable {
    private String name;
    private long id = 0;
    private final ConcurrentLinkedQueue<PendingChange> pendingChanges = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<ByteBuffer> outboundQueue = new ConcurrentLinkedQueue<>();
    private Selector selector;
    private SocketChannel socketChannel;
    private final CountDownLatch readyLatch = new CountDownLatch(1);

    public Broker(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setBrokerId(long id) {
        this.id = id;
    }

    public void awaitReady() throws InterruptedException {
        readyLatch.await();
    }

    public long getBrokerId() {
        return this.id;
    }

    @Override
    public void run() {
        try {
            PendingChange change;
            int port = Utils.BROKER_PORT;
            this.selector = Selector.open();
            this.socketChannel = SocketChannel.open();
            this.socketChannel.configureBlocking(false);
            this.socketChannel.connect(new InetSocketAddress(Utils.HOST, port));
            System.out.println(name + " trying to connect to Router");
            this.socketChannel.register(this.selector, SelectionKey.OP_CONNECT);

            readyLatch.countDown();
            while (true) {
                while ((change = pendingChanges.poll()) != null) {
                    if (change.type == PendingChange.Type.CHANGE_OPS) {
                        SelectionKey key = change.channel.keyFor(selector);
                        if (key != null && key.isValid()) key.interestOps(change.ops);
                    }
                }
                if (this.selector.select() == 0) continue;

                Set<SelectionKey> keys = this.selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isConnectable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        if (client.finishConnect()) {
                            log("connected to Router successfully.");
                            key.interestOps(SelectionKey.OP_READ);
                            key.attach(new MessageReader());
                        }
                    } else if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        MessageReader messageReader = (MessageReader) key.attachment();
                        byte[] message;
                        try {
                            while ((message = messageReader.read(clientChannel)) != null) {
                                handleMessage(message);
                            }
                        } catch (IOException e) {
                            socketChannel.close();
                            log("Connection failed due to an internal error.");
                            return;
                        }
                    } else if (key.isWritable()) {
                        SocketChannel socket = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = this.outboundQueue.peek();
                        if (byteBuffer != null) {
                            socket.write(byteBuffer);
                            if (!byteBuffer.hasRemaining()) {
                                this.outboundQueue.poll();
                            }
                        }
                        if (this.outboundQueue.isEmpty()) {
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            System.out.println("Connection failed due to an I/O error." + " " + e.getMessage());
        }
    }

    public void send(String message) {
        ByteBuffer byteBuffer = FIXMessage.addHeader(message.getBytes());
        outboundQueue.add(byteBuffer);
        pendingChanges.add(new PendingChange(PendingChange.Type.CHANGE_OPS, SelectionKey.OP_READ, this.socketChannel));
        this.selector.wakeup();
    }

    private void handleMessage(byte[] message) {
        FIXMessage fixMessage = new FIXMessage();
        String[] pairs = fixMessage.getPairs(new String(message));
        int numberOfPairs = pairs.length;

        // case 1: ID message
        if (numberOfPairs == 1 && pairs[0].startsWith("id=")) {
            // case 1: ID message
            log("this is an ID message coming from the Router" + new String(message));
            log("now that I received an ID, I'm ready to make requests!");
        } else if (numberOfPairs > 1) {
            // case 2: Response Message coming from the Market via the Router
            log("this is a Response coming from the Market via the Router" + new String(message));
        } else {
            // case 3: Something is wrong, since the numberOfPairs can either be 1 or > 1
            // < 1 is an impossible case.
            log("Something went wrong badly!");
        }
    }

    private void log(String message) {
        System.out.println("[" + this.name + "]: " + message);
    }

    public static void main(String[] args) {
        Broker br = new Broker("BRK1");
        Thread selectorThread = new Thread(br);
        selectorThread.start();
        try {
            br.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        br.send("id=34242424|23=hello world!");
        br.send("This something new");
    }
}

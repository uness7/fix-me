package com.fortytwo.fixme.router;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Router {
    private static final int BUFFER_SIZE = 1024;
    private static Router instance = null;
    public final int BROKER_PORT = 5000;
    public final int MARKET_PORT = 5001;
    private long uniqueId = 111111;
    private MessageType messageType = MessageType.ACK;

    private Router() {
    }

    public static Router getInstance() {
        if (instance == null) {
            instance = new Router();
        }
        return instance;
    }

    private String getUniqueId() {
        return Long.toString(uniqueId++);
    }

    private String getChecksum(int len) {
        return Integer.toString(len % 20);
    }

    private void handleMarket(Socket socket) {
        int dataLength = 5;
        byte[] data = new byte[dataLength];
        recv(socket, 20);
    }

    private void sendAck(Socket socket) throws IOException {
        if (socket.isConnected()) {
            OutputStream out = socket.getOutputStream();
            String ack = "35=ID|56=" + getUniqueId() + "|10=21";
            out.write(ack.getBytes());
            out.flush();
            this.messageType = MessageType.ORDINARY;
        }
    }

    private void wait(Socket socket) {
        // listening for data coming from brokers, the message should include
        //      the ID previously assigned to each client
        int dataLength = 0;
        byte[] data = new byte[dataLength];
        if (socket.isConnected()) {
            // waiting for buy or sell requests from the Broker
            recv(socket, 5);
        }
        this.messageType = MessageType.IDLE;
    }

    private void recv(Socket socket, int dataLength) {
        byte[] data = new byte[dataLength];

        try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
            in.read(data, 0, dataLength);
            String message = new String(data, 0, dataLength);
            System.out.println("Message received from broker: " + message);
        } catch (IOException e) {
            System.out.println("Exception occurred " + e.getMessage());
        }
    }

    private void handleBrokers(Socket socket) {
        // ISSUE: once ack message is sent, the thread goes idle since we exit this function
        if (socket.isConnected()) {
            while (true) {
                if (this.messageType == MessageType.ACK) {
                    try {
                        sendAck(socket);
                    } catch (IOException e) {
                        System.out.println("Exception occurred " + e.getMessage());
                    }
                } else if (this.messageType == MessageType.ORDINARY) {
                    System.out.println("Ordinary message received");
                    wait(socket);
                } else if (this.messageType == MessageType.IDLE) {
                    try {
                        System.out.println("Socket closed");
                        socket.close();
                        break ;
                    } catch (IOException e) {
                        System.out.println("Exception occurred " + e.getMessage());
                    }
                }
            }
        }
    }

    // upon reception of new connections, brokerService will assign a new thread the task of handling the connection
    public void brokerService() throws IOException {
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            try (ServerSocket server = new ServerSocket(BROKER_PORT)) {
                while (true) {
                    Socket socket = server.accept();
                    executor.execute(() -> {
                        handleBrokers(socket);
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Exception occurred " + e.getMessage());
        }
    }

      /**
     * Router is listening onto port 5000 & 5001 in parallel via thread pools
     */
    public void activate() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            executor.execute(() -> {
                try {
                    brokerService();
                } catch (IOException e) {
                    System.out.println("Exception occurred " + e.getMessage());
                }
            });

            //executor.execute(this::marketService);
            if (executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Executor was interrupted: " + e.getMessage());
        }
    }
}
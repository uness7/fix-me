package com.fortytwo.fixme.router;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Router {
    private static final int BUFFER_SIZE = 1024;
    private static Router instance = null;
    public final int BROKER_PORT = 5000;
    public final int MARKET_PORT = 5001;
    private long id = 111111;
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
        return Long.toString(id++);
    }

    private String getChecksum(int len) {
        return Integer.toString(len % 20);
    }

    private void handleBroker(Socket socket) throws IOException {
    }

    private void handleMarket(Socket socket) {
    }

    private void sendAck(Socket socket) throws IOException {
        System.out.println("Sending Client its assigned ID ");
        OutputStream out = socket.getOutputStream();
        String ack = "35=ID|56=" + getUniqueId();
        out.write(ack.getBytes());
        out.flush();
        this.messageType = MessageType.ORDINARY;
    }

    private void listen(int port){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Listening on port " + socket.getLocalPort());
                if (socket.isConnected()) {
                    System.out.println("Accepted connection from Broker");
                    if (messageType == MessageType.ACK) {
                        sendAck(socket);
                    } else if (messageType == MessageType.ORDINARY) {
                        if (socket.getLocalPort() == BROKER_PORT) {
                            handleBroker(socket);
                        } else if (socket.getLocalPort() == MARKET_PORT) {
                            handleMarket(socket);
                        } else {
                            throw new IllegalArgumentException("Invalid port number");
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error accepting socket");
        }
    }

    public void brokerService() {
        listen(BROKER_PORT);
    }

    public void marketService() {
        listen(MARKET_PORT);
    }

    /**
     * Router is listening onto port 5000 & 5001 in parallel via thread pools
     */
    public void activate() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            executor.execute(this::brokerService);
            executor.execute(this::marketService);
            if (executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Executor was interrupted: " + e.getMessage());
        }
    }
}
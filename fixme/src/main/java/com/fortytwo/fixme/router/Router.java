package com.fortytwo.fixme.router;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.UUID;

public class Router {
    private static final int BUFFER_SIZE = 1024;
    private static Router instance = null;
    public final int BROKER_PORT = 5000;
    public final int MARKET_PORT = 5001;

    private Router() {
    }

    public static Router getInstance() {
        if (instance == null) {
            instance = new Router();
        }
        return instance;
    }

    private String getUniqueId() {
        return UUID.randomUUID().toString();
    }

    private String getChecksum(int len) {
        return Integer.toString(len % 20);
    }

    private void handleBroker(Socket socket) throws IOException {
        // the first key-value is the msgType(35), ID refers to first message sent by the Router communicating the ID
        OutputStream out = socket.getOutputStream();
        String initMessage = "35=ID|56=" + getUniqueId() + "|10=";
        String firstMessage = initMessage + getChecksum(initMessage.length());
        out.write(firstMessage.getBytes());
    }

    public void listen(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Listening on port " + socket.getLocalPort());
                    if (socket.isConnected()) {
                        System.out.println("Accepted connection from a Broker " + socket.getInetAddress());
                        if (socket.getLocalPort() == BROKER_PORT) {
                            System.out.println("Handling a Broker " + socket.getInetAddress());
                            handleBroker(socket);
                        } else if (socket.getLocalPort() == MARKET_PORT) {
                            handleMarket(socket);
                        } else {
                            throw new IllegalStateException("Invalid port " + socket.getLocalPort());
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleMarket(Socket socket) {
    }
}
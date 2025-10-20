package com.fortytwo.fixme.router;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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

    public void listen(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                System.out.println("Listening on port " + serverSocket.getLocalPort());
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Accepted connection from " + socket.getInetAddress());
                    if (socket.isConnected()) {
                        // accept BROKER tcp connections
                        if (socket.getLocalPort() == BROKER_PORT) {
                            int recvMsgSize = 0;
                            byte[] recvBuffer = new byte[BUFFER_SIZE];
                            InputStream inputStream = socket.getInputStream();
                            OutputStream outputStream = socket.getOutputStream();
                            while ((recvMsgSize = inputStream.read(recvBuffer)) > 0) {
                                outputStream.write(recvBuffer, 0, recvMsgSize);
                            }
                            System.out.println("Received from broker" + new String(recvBuffer));
                        } else if (socket.getLocalPort() == MARKET_PORT) {
                            // accept MARKET tcp connections
                            int recvMsgSize = 0;
                            byte[] recvBuffer = new byte[BUFFER_SIZE];
                            InputStream inputStream = socket.getInputStream();
                            OutputStream outputStream = socket.getOutputStream();
                            while ((recvMsgSize = inputStream.read(recvBuffer)) > 0) {
                                outputStream.write(recvBuffer, 0, recvMsgSize);
                            }
                            System.out.println("Received from Market" + new String(recvBuffer));
                        } else {
                            throw new IllegalStateException("Something is wrong");
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
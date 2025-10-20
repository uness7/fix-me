package com.fortytwo.fixme.broker;

import com.fortytwo.fixme.router.Router;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class Broker {
    private final String name;
    private Socket socket;

    public Broker(String name) {
        System.out.println("A Broker named " + name + " was created.");
        this.name = name;
    }

    public void start() throws IOException {
        socket = new Socket("localhost", Router.getInstance().BROKER_PORT);
        if (socket.isConnected()) {
            //System.out.println("Broker is already running.");
            // listen for the first message coming from the Router
            byte[] data = new byte[1024];
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            int size;
            while ((size = in.read(data)) != -1) {
                out.write(data, 0, size);
            }
            System.out.println(new String(data));
        }
    }
}

package com.fortytwo.fixme.broker;

import com.fortytwo.fixme.router.Router;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

public class Broker {
    private final String HOST = "localhost";
    private final int PORT = Router.getInstance().BROKER_PORT;
    private final String name;
    private Socket socket = null;
    private long uniqueId = -1;

    public Broker(String name) {
        this.name = name;
    }

    public void init() throws IOException {
        System.out.println("Establishing connection with Router");
        socket = new Socket(HOST, PORT);
    }

    /*
        This would take an order of type Request that contains ID, message in FIX, timestamp of creation,
        this request object will be used by the Router to easily create the Routing table.
    *  */
    public void makeOrder(String message) throws IOException {
        if (socket != null && !socket.isClosed() && this.uniqueId != -1) {
            OutputStream out = socket.getOutputStream();
            out.write(message.getBytes());
            out.flush();
        }
    }

    public void listenForAckMessage() throws IOException {
        final int dataLength = 19;
        if (socket.isConnected()) {
            System.out.println("Listening for acknowledgement");
            InputStream in = socket.getInputStream();
            byte[] data = in.readNBytes(dataLength);
            String message = new String(data, 0, dataLength);
            if (message.contains("35=ID|56=")) {
                message = message.replace("35=ID|56=", "");
                message = message.substring(0, 5);
                this.uniqueId = Long.parseLong(message, 16);
                System.out.println("Unique Id received from Router : " + uniqueId);
                socket.close();
            }
        }
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
}

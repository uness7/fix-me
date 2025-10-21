package com.fortytwo.fixme.broker;

import com.fortytwo.fixme.router.Router;

import java.io.DataInputStream;
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
    private long uniqueId = 1;

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
        final int dataLength = 21;
        byte[] data = new byte[dataLength];

        if (socket.isConnected()) {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            OutputStream out = socket.getOutputStream();
            in.read(data, 0, dataLength);

            String message = new String(data);
            System.out.println("Broker received  " + message);
            System.out.println("Broker received an Id.");

            out.flush();
            //in.close();
            //out.close();
        }
        System.out.println("Exiting ackMessage()");
    }

    public void sendBuyRequest(String message) throws IOException {
        System.out.println("Sending request to broker " + message);
        if (socket.isConnected()  && this.uniqueId != -1) {
            System.out.println("We can send a buy request under these circumstances. " + message);
            OutputStream out = socket.getOutputStream();
            out.write(message.getBytes());
            out.flush();
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

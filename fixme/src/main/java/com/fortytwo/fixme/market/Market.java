package com.fortytwo.fixme.market;

import com.fortytwo.fixme.common.Instrument;
import com.fortytwo.fixme.router.Request;
import com.fortytwo.fixme.router.Router;

import java.awt.desktop.OpenURIEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;

public class Market {
    public LinkedList<Instrument> instrumentsList;
    private String name;
    private int dataSizeReceived;
    private byte[] data;
    private Socket socket;

    public Market(String name, LinkedList<Instrument> instrumentsList) throws IOException {
        this.name = name;
        this.instrumentsList = instrumentsList;
    }

    public void accept() throws IOException {
        socket = new Socket("localhost", 5001);
        if (socket.getLocalPort() == 5001) {
            System.out.println("Receiving requests from the Router");
            data = new byte[1024];
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            while ((dataSizeReceived = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, dataSizeReceived);
            }
            String res = handleRequest(Arrays.toString(data));
        } else {
            throw new IOException("Cannot connect unless it's a Router");
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    private String handleRequest(String request) {
        return "";
        /*
        if (request.isValid()) {
            System.out.println("Market is checking the request");
            // check if the requested instrument is traded, available
            // once these checks are done, send back an Executed Message
            // else send a RejectedMessage with a description message
        } else {
            // or maybe a custom exception
            throw new IllegalArgumentException("Invalid request");
        }
         */
    }
}
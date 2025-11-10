package com.fortytwo.fixme.common;

import com.fortytwo.fixme.router.ClientType;

public class RoutingTable {
    private long uniqueId;
    private ClientType clientType;
    private String message;
    private String destination; // will be extracted from the message

    public long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public String getDestinationId() {
        return destination;
    }

    public void setDestinationId(String destination) {
        this.destination = destination;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

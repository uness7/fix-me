package com.fortytwo.fixme.common;

import java.nio.channels.SocketChannel;

public class PendingChange {
    public enum Type {CHANGE_OPS, REGISTER };
    public final int ops;
    public final Type type;
    public final SocketChannel channel;

    public PendingChange(Type type, int ops, SocketChannel channel) {
        this.ops = ops;
        this.channel = channel;
        this.type = type;
    }
}

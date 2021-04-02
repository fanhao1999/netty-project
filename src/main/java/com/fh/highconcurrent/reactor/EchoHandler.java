package com.fh.highconcurrent.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author hao.fan
 * @dateTime 2021/4/2 15:53
 * @description
 */
public class EchoHandler implements Runnable {

    private static final int RECIEVING = 0, SENDING = 1;

    private SocketChannel channel;

    private SelectionKey sk;

    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    private int state = RECIEVING;

    public EchoHandler(Selector selector, SocketChannel channel) throws IOException {
        this.channel = channel;
        this.channel.configureBlocking(false);
        sk = this.channel.register(selector, 0);
        sk.attach(this);
        sk.interestOps()
    }

    @Override
    public void run() {

    }
}

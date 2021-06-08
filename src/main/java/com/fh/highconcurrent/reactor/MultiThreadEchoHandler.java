package com.fh.highconcurrent.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author hao.fan
 * @dateTime 2021/6/8 3:59 下午
 * @description
 */
public class MultiThreadEchoHandler implements Runnable {

    private static final int RECIEVING = 0, SENDING = 1;

    private static final Executor pool = Executors.newFixedThreadPool(4);

    private SocketChannel channel;

    private SelectionKey sk;

    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    private int state = RECIEVING;

    public MultiThreadEchoHandler(Selector selector, SocketChannel channel) throws IOException {
        this.channel = channel;
        this.channel.configureBlocking(false);
        selector.wakeup();
        sk = this.channel.register(selector, 0);
        sk.attach(this);
        sk.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    @Override
    public void run() {
        pool.execute(new AsyncTask());
    }

    private synchronized void asyncRun() {
        try {
            if (state == SENDING) {
                channel.write(buffer);
                buffer.clear();
                sk.interestOps(SelectionKey.OP_READ);
                state = RECIEVING;
            } else if (state == RECIEVING) {
                int length = 0;
                while ((length = channel.read(buffer)) > 0) {
                    System.out.println(new String(buffer.array(), 0, length));
                }
                buffer.flip();
                sk.interestOps(SelectionKey.OP_WRITE);
                state = SENDING;
            }
        } catch (IOException e) {
            e.printStackTrace();
            sk.cancel();
        }
    }

    private class AsyncTask implements Runnable {

        @Override
        public void run() {
            MultiThreadEchoHandler.this.asyncRun();
        }
    }
}

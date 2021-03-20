package com.fh.highconcurrent.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author hao.fan
 * @dateTime 2021/3/20 16:14
 * @description
 */
public class EchoServerReactor implements Runnable {

    private Selector selector;

    private ServerSocketChannel serverChannel;

    public EchoServerReactor() throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(8080));
        SelectionKey key = serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        key.attach(new AcceptorHandler());
    }

    @Override
    public void run() {

    }

    private class AcceptorHandler implements Runnable {

        @Override
        public void run() {
            try {
                SocketChannel socketChannel = serverChannel.accept();
                if (socketChannel != null) {
                    System.out.println("接收到一个连接");
                    // TODO
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

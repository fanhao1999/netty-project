package com.fh.highconcurrent.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author hao.fan
 * @dateTime 2021/5/8 16:35
 * @description
 */
public class EchoClient {

    public void start() throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(8080));
        socketChannel.configureBlocking(false);
        while (!socketChannel.finishConnect()) {
            System.out.println("连接中...");
        }
        System.out.println("客户端连接成功...");
        new Thread(new Processer(socketChannel)).start();
    }

    private static class Processer implements Runnable {

        private final Selector selector;

        private final SocketChannel channel;

        public Processer(SocketChannel channel) throws IOException {
            selector = Selector.open();
            this.channel = channel;
            channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    selector.select();
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> it = keys.iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        SocketChannel channel = (SocketChannel) key.channel();

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

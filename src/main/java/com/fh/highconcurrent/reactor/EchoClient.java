package com.fh.highconcurrent.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Scanner;
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
                        if (key.isWritable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            Scanner scanner = new Scanner(System.in);
                            System.out.println("请输入发送内容:");
                            if (scanner.hasNext()) {
                                String next = scanner.next();
                                String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                buffer.put((now + " >> " + next).getBytes());
                                buffer.flip();
                                channel.write(buffer);
                                buffer.clear();
                            }
                        }
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int length = 0;
                            while ((length = channel.read(buffer)) > 0) {
                                buffer.flip();
                                System.out.println("server echo:" + new String(buffer.array(), 0, length));
                                buffer.clear();
                            }
                        }
                    }
                    keys.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new EchoClient().start();
    }
}

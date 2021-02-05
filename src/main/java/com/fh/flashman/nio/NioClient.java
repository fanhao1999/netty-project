package com.fh.flashman.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioClient implements Runnable {

    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private ExecutorService executorService;
    private volatile boolean turnOff;

    public NioClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.executorService = Executors.newSingleThreadExecutor();

        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        System.out.println("client start");
        try {
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (!turnOff) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey selectionKey = null;
                while (iterator.hasNext()) {
                    selectionKey = iterator.next();
                    iterator.remove();
                    try {
                        handleInput(selectionKey);
                    } catch (Exception e) {
                        if (selectionKey != null) {
                            selectionKey.cancel();
                            if (selectionKey.channel() != null) {
                                selectionKey.channel().close();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        // 多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (executorService != null) {
            executorService.shutdown();
        }
    }

    private void handleInput(SelectionKey selectionKey) throws Exception {
        if (selectionKey.isValid()) {
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            // 渠道可连接
            if (selectionKey.isConnectable()) {
                // 此处注意，需要等连接完成后做后续事情，否则将阻塞在write这一步
                if (channel.finishConnect()) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    System.out.println("请输入内容：");
                    executorService.submit(() -> {
                        while (true) {
                            try {
                                byteBuffer.clear();
                                InputStreamReader inputStreamReader = new InputStreamReader(System.in);
                                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                                String in = bufferedReader.readLine();
                                if ("exit".equals(in)) {
                                    selectionKey.cancel();
                                    channel.close();
                                    this.turnOff = true;
                                    break;
                                }
                                byteBuffer.put(in.getBytes());
                                byteBuffer.flip();
                                channel.write(byteBuffer);
                                System.out.println("请输入内容：");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    channel.register(selector, SelectionKey.OP_READ);
                } else {
                    System.exit(1);
                }
            }
            // 渠道可读
            if (selectionKey.isReadable()) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int readBytes = channel.read(byteBuffer);
                if (readBytes > 0) {
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println(body);
                    if ("exit".equals(body)) {
                        this.turnOff = true;
                    }
                } else if (readBytes < 0) {
                    // 关闭渠道
                    selectionKey.cancel();
                    channel.close();
                }
            }
            // 渠道可写
            if (selectionKey.isWritable()) {
                System.out.println("The key is writable");
            }
        }
    }

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 8080;
        NioClient nioClient = new NioClient(host, port);
        new Thread(nioClient).start();
    }
}

package com.fh.flashman.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer implements Runnable {
    /**
     * 选择器，多路复用器
     */
    private Selector selector;
    /**
     * 渠道
     */
    private ServerSocketChannel serverSocketChannel;
    /**
     * 是否打开
     */
    private volatile boolean turnOff;
    /**
     * 序号，第N个连接
     */
    private int idx = 0;

    /**
     * 初始化服务
     *
     * @param port 监听端口
     */
    public NioServer(int port) {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("监听端口：" + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * 开关
     */
    public void stop() {
        this.turnOff = true;
    }


    @Override
    public void run() {
        // 循环
        while (!turnOff) {
            try {
                selector.select(1000);
                Set<SelectionKey> sks = selector.selectedKeys();
                Iterator<SelectionKey> iterator = sks.iterator();
                SelectionKey key;
                // 遍历可用的key（注册渠道时绑定关系的桥梁）
                while (iterator.hasNext()) {
                    key = iterator.next();
                    // 删除对于关系
                    iterator.remove();
                    try {
                        // 处理输入
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                // 关闭渠道
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 关闭服务
        // 多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭
        if (selector != null)
            try {
                selector.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /**
     * 处理输入信息
     * @param key 渠道
     * @throws IOException
     */
    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            // 新接入的请求
            if (key.isAcceptable()) {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel socketChannel = ssc.accept(); // Non blocking, never null
                socketChannel.configureBlocking(false);
                SelectionKey sk = socketChannel.register(selector, SelectionKey.OP_READ);
                sk.attach(idx++);
            }
            // 可读的请求
            if (key.isReadable()) {
                // 读取数据
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0) {
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("客户端[" + key.attachment() + "]输入： [" + body.trim() + "]！");

                    if (body.trim().equals("Quit")) {
                        System.out.println("关闭客户端[" + key.attachment() + "]......");
                        key.cancel();
                        sc.close();
                    } else {
                        String response = "服务器端响应：" + body;
                        doWrite(sc, response);
                    }
                } else if (readBytes < 0) {
                    // 渠道关闭
                    key.cancel();
                    sc.close();
                }
            }
        }
    }

    private void doWrite(SocketChannel channel, String response) throws IOException {
        if (response != null && response.trim().length() > 0) {
            byte[] bytes = response.getBytes();
            // buffer的操作
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer);
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        NioServer nioServer = new NioServer(port);
        new Thread(nioServer).start();
    }
}

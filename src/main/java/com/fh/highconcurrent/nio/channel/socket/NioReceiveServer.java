package com.fh.highconcurrent.nio.channel.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author hao.fan
 * @dateTime 2021/3/12 15:51
 * @description
 */
public class NioReceiveServer {

    private Map<SelectableChannel, Client> clientMap = new HashMap<>();

    public void startServer() throws IOException {
        Selector selector = Selector.open();

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverChannel.socket();

        serverChannel.configureBlocking(false);

        InetSocketAddress address = new InetSocketAddress(8080);
        serverSocket.bind(address);

        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (selector.select() > 0) {
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = server.accept();
                    if (socketChannel == null) {
                        continue;
                    }
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    Client client = new Client();
                    client.remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
                    clientMap.put()
                }
            }
        }
    }

    /**
     * 服务器端保存的客户端对象，对应一个客户端文件
     */
    static class Client {

        // 文件名称
        String fileName;

        // 长度
        long fileLength;

        // 开始传输的时间
        long startTime;

        // 客户端的地址
        InetSocketAddress remoteAddress;

        // 输出的文件通道
        FileChannel outChannel;

        // 接收长度
        long receiveLength;

        public boolean isFinished() {
            return receiveLength >= fileLength;
        }
    }

    public static void main(String[] args) {
        NioReceiveServer nioReceiveServer = new NioReceiveServer();
        nioReceiveServer.startServer();
    }
}

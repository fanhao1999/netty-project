package com.fh.highconcurrent.nio.channel.socket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author hao.fan
 * @dateTime 2021/3/12 15:51
 * @description
 */
public class NioReceiveServer {

    // 接受文件路径
    private static final String RECEIVE_PATH = "D:/test";

    private static final Charset charset = Charset.forName("UTF-8");

    private Map<SelectableChannel, Client> clientMap = new HashMap<>();

    private ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);

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
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    Client client = new Client();
                    client.remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
                    clientMap.put(socketChannel, client);
                    System.out.println(socketChannel.getRemoteAddress() + "连接成功...");
                } else if (key.isReadable()) {
                    processData(key);
                }
                // NIO的特点只会累加，已选择的键的集合不会删除
                // 如果不删除，下一次又会被select函数选中
                it.remove();
            }
        }
    }

    /**
     * 处理客户端传输过来的数据
     * @param key
     */
    private void processData(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        Client client = clientMap.get(socketChannel);
        int num = 0;

        try {
            buffer.clear();
            while ((num = socketChannel.read(buffer)) > 0) {
                buffer.flip();
                // 客户端发送过来的，首先处理文件名
                if (client.fileName == null) {
                    if (buffer.capacity() < 4) {
                        continue;
                    }
                    int fileNameLen = buffer.getInt();
                    byte[] fileNameBytes = new byte[fileNameLen];
                    buffer.get(fileNameBytes);
                    // 文件名
                    String fileName = new String(fileNameBytes, charset);
                    client.fileName = fileName;

                    File dir = new File(RECEIVE_PATH);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    String fullName = dir.getAbsolutePath() + File.separatorChar + fileName;
                    File file = new File(fullName.trim());
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    FileChannel fileChannel = new FileOutputStream(file).getChannel();
                    client.outChannel = fileChannel;

                    if (buffer.capacity() < 8) {
                        continue;
                    }
                    // 文件长度
                    long fileLength = buffer.getLong();
                    client.fileLength = fileLength;
                    client.startTime = System.currentTimeMillis();

                    client.receiveLength += buffer.capacity();
                    if (buffer.capacity() > 0) {
                        // 写入文件
                        client.outChannel.write(buffer);
                    }
                    if (client.isFinished()) {
                        finished(key, client);
                    }
                } else {
                    // 客户端发送过来的，最后是文件内容
                    client.receiveLength += buffer.capacity();
                    // 写入文件
                    client.outChannel.write(buffer);
                    if (client.isFinished()) {
                        finished(key, client);
                    }
                }
                buffer.clear();
            }
            key.cancel();
        } catch (IOException e) {
            key.cancel();
            e.printStackTrace();
            return;
        }
        // 调用close为-1 到达末尾
        if (num == -1) {
            finished(key, client);
        }
    }

    private void finished(SelectionKey key, Client client) throws IOException {
        client.outChannel.force(true);
        client.outChannel.close();
        System.out.println("上传完毕");
        key.cancel();
        System.out.println("文件接收成功,File Name：" + client.fileName);
        System.out.println("Size：" + client.fileLength);
        long endTime = System.currentTimeMillis();
        System.out.println("NIO IO 传输毫秒数：" + (endTime - client.startTime));
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

    public static void main(String[] args) throws IOException {
        NioReceiveServer nioReceiveServer = new NioReceiveServer();
        nioReceiveServer.startServer();
    }
}

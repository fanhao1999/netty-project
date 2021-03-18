package com.fh.highconcurrent.nio.channel.socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author hao.fan
 * @dateTime 2021/3/16 18:57
 * @description
 */
public class NioSendClient {

    private static final String SOURCE_PATH = "D:/test.xlsx";

    private static final String DEST_FILE = "test.xlsx";

    private static final Charset charset = Charset.forName("UTF-8");

    public void sendFile() {
        try {
            File file = new File(SOURCE_PATH);
            if (!file.exists()) {
                System.out.println("文件不存在");
                return;
            }
            FileChannel fileChannel = new FileInputStream(file).getChannel();

            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(8080));
            while (!socketChannel.finishConnect()) {
                System.out.println("连接中...");
            }

            ByteBuffer fileNameByteBuffer = charset.encode(DEST_FILE);
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            // 发送文件名称长度
            int fileNameLen = fileNameByteBuffer.limit();
            buffer.putInt(fileNameLen);
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
            System.out.println("Client 文件名称长度发送完成:" + fileNameLen);
            // 发送文件名称
            socketChannel.write(fileNameByteBuffer);
            System.out.println("Client 文件名称发送完成:" + DEST_FILE);
            // 发送文件长度
            buffer.putLong(file.length());
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
            System.out.println("Client 文件长度发送完成:" + file.length());
            // 发送文件内容
            System.out.println("开始传输文件...");
            int length = 0;
            long progress = 0;
            while ((length = fileChannel.read(buffer)) > 0) {
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
                progress += length;
                System.out.println("| " + (100 * progress / file.length()) + "% |");
            }
            if (length == -1) {
                socketChannel.shutdownOutput();
                socketChannel.close();
                fileChannel.close();
            }
            System.out.println("======== 文件传输成功 ========");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NioSendClient client = new NioSendClient();
        client.sendFile();
    }
}

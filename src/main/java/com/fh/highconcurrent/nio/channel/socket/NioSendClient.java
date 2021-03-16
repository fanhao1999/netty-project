package com.fh.highconcurrent.nio.channel.socket;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
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
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(8080));
        } catch (IOException e) {

        }

    }
}

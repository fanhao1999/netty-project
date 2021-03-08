package com.fh.highconcurrent.nio.channel;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author hao.fan
 * @dateTime 2021/3/8 20:05
 * @description
 */
public class FileChannelTest {

    public static void main(String[] args) throws IOException {
        FileInputStream fls = new FileInputStream("D:/test.js");
        FileChannel fChannel = fls.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
        int length = -1;
        while ((length = fChannel.read(buffer)) != -1) {
        }

        buffer.flip();


    }
}

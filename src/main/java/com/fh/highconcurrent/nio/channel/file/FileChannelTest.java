package com.fh.highconcurrent.nio.channel.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author hao.fan
 * @dateTime 2021/3/8 20:05
 * @description
 */
public class FileChannelTest {

    public static void main(String[] args) throws IOException {
        FileInputStream fis = new FileInputStream("D:/test.js");
        FileChannel inChannel = fis.getChannel();
        FileOutputStream fos = new FileOutputStream("D:/test2.js");
        FileChannel outChannel = fos.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
        int inLength = -1;
        while ((inLength = inChannel.read(buffer)) != -1) {
            System.out.println("读了" + inLength + "字节");
        }

        buffer.flip();
        int outLength = 0;
        while ((outLength = outChannel.write(buffer)) != 0) {
            System.out.println("写了" + outLength + "字节");
        }

        outChannel.force(true);
        outChannel.close();
        inChannel.close();
    }
}

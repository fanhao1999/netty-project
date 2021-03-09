package com.fh.highconcurrent.nio.channel.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author hao.fan
 * @dateTime 2021/3/9 15:10
 * @description
 */
public class FileNIOCopyDemo {

    public static void main(String[] args) {
        long begin = System.currentTimeMillis();
        String srcPath = "D:/test.xlsx";
        String destPath = "D:/test2.xlsx";
        nioCopyFile(srcPath, destPath);
        System.out.println("复制耗时：" + (System.currentTimeMillis() - begin) / 1000D + "s");
    }

    private static void nioCopyFile(String srcPath, String destPath) {
        File srcFile = new File(srcPath);
        File destFile = new File(destPath);

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            destFile.createNewFile();

            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            int inLength = -1;
            while ((inLength = inChannel.read(buffer)) != -1) {
                buffer.flip();
                int outLength = 0;
                while ((outLength = outChannel.write(buffer)) != 0) {
                    System.out.println("写入的字节数：" + outLength);
                }
                buffer.clear();
            }
            outChannel.force(true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
    }
}

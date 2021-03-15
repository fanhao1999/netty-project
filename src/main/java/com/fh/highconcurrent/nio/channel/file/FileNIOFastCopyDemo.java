package com.fh.highconcurrent.nio.channel.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author hao.fan
 * @dateTime 2021/3/9 16:24
 * @description
 */
public class FileNIOFastCopyDemo {

    public static void main(String[] args) {
        long begin = System.currentTimeMillis();
        String srcPath = "D:/test.xlsx";
        String destPath = "D:/test2.xlsx";
        fastCopyFile(srcPath, destPath);
        System.out.println("复制耗时：" + (System.currentTimeMillis() - begin) / 1000D + "s");
    }

    /**
     * 零拷贝
     * @param srcPath
     * @param destPath
     */
    private static void fastCopyFile(String srcPath, String destPath) {
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

            long size = inChannel.size();
            // 1M
            long transferSize = 1024 * 1024;
            long position = 0L;
            long count;
            while (position < size) {
                count = size - position > transferSize ? transferSize : size - position;
                position += outChannel.transferFrom(inChannel, position, count);
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

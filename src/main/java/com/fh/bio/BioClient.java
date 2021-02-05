package com.fh.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

public class BioClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        new Thread(() -> {
            try {
                Socket socket = new Socket("127.0.0.1", 8080);
                OutputStream outputStream = socket.getOutputStream();
                while (true) {
                    Thread.sleep(10000);
                    outputStream.write((new Date() + ": hello world").getBytes());

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

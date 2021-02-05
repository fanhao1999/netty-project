package com.fh.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);

        new Thread(() -> {
            try {
                while (true) {
                    Socket accept = serverSocket.accept();

                    new Thread(() -> {
                        try {
                            int len = 0;
                            byte[] data = new byte[1024];
                            InputStream inputStream = accept.getInputStream();
                            while ((len = inputStream.read(data)) != -1) {
                                System.out.println("有数据传来");
                                System.out.println(new String(data, 0, len));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println("服务启动");
    }
}

package com.fh.highconcurrent.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author hao.fan
 * @dateTime 2021/12/10 4:16 下午
 * @description
 */
public class JavaFutureTest {

    public static void main(String[] args) {
        FutureTask<Boolean> futureTask = new FutureTask<>(() -> {
            System.out.println("开始执行call方法");
            Thread.sleep(3 * 1000L);
            return Boolean.TRUE;
        });
        Thread thread = new Thread(futureTask);
        thread.start();
        try {
            Boolean result = futureTask.get();
            if (result) {
                System.out.println("call方法执行结束");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}

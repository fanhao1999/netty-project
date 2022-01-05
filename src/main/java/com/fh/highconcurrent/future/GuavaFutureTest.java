package com.fh.highconcurrent.future;

import com.google.common.util.concurrent.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author hao.fan
 * @dateTime 2021/12/10 4:37 下午
 * @description
 */
public class GuavaFutureTest {

    public static void main(String[] args) {
        ExecutorService jPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ListeningExecutorService gPool = MoreExecutors.listeningDecorator(jPool);
        ListenableFuture<Boolean> future = gPool.submit(() -> {
            System.out.println("开始执行call方法");
            Thread.sleep(3 * 1000L);
            return Boolean.TRUE;
        });
        Futures.addCallback(future, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    System.out.println("call方法执行结束");
                }
            }
            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
        System.out.println("main方法执行结束");
    }
}

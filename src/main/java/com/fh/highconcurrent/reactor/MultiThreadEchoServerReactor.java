package com.fh.highconcurrent.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hao.fan
 * @dateTime 2021/6/8 10:06 上午
 * @description
 */
public class MultiThreadEchoServerReactor {

    private ServerSocketChannel serverChannel;

    private AtomicInteger next = new AtomicInteger(0);

    private Selector bossSelector;

    private Selector[] workSelectors = new Selector[2];

    private Reactor bossReactor;

    private Reactor[] workReactors;

    public MultiThreadEchoServerReactor() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(8080));

        bossSelector = Selector.open();
        workSelectors[0] = Selector.open();
        workSelectors[1] = Selector.open();

        bossReactor = new Reactor(bossSelector);
        Reactor subReactor1 = new Reactor(workSelectors[0]);
        Reactor subReactor2 = new Reactor(workSelectors[1]);
        workReactors = new Reactor[]{subReactor1, subReactor2};

        SelectionKey key = serverChannel.register(bossSelector, SelectionKey.OP_ACCEPT);
        key.attach(new AcceptorHandler());
    }

    public void startService() {
        new Thread(bossReactor).start();
        new Thread(workReactors[0]).start();
        new Thread(workReactors[1]).start();
    }

    private static class Reactor implements Runnable {

        private Selector selector;

        public Reactor(Selector selector) {
            this.selector = selector;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    selector.select(1000L);
                    Set<SelectionKey> keys = selector.selectedKeys();
                    if (keys == null || keys.isEmpty()) {
                        continue;
                    }
                    Iterator<SelectionKey> it = keys.iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        dispatch(key);
                    }
                    keys.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void dispatch(SelectionKey key) {
            Runnable handler = (Runnable) key.attachment();
            if (handler != null) {
                handler.run();
            }
        }
    }

    private class AcceptorHandler implements Runnable {

        @Override
        public void run() {
            try {
                SocketChannel channel = serverChannel.accept();
                if (channel != null) {
                    System.out.println("接收到一个连接");
                    int index = next.get();
                    System.out.println("选择器的编号：" + index);
                    Selector workSelector = workSelectors[index];
                    new MultiThreadEchoHandler(workSelector, channel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (next.incrementAndGet() == workSelectors.length) {
                next.set(0);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        MultiThreadEchoServerReactor server = new MultiThreadEchoServerReactor();
        server.startService();
    }
}

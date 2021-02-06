package com.fh.highconcurrent.nio.buffer;

import java.nio.IntBuffer;

/**
 * @author hao.fan
 * @dateTime 2021/2/5 18:33
 * @description
 */
public class NioBufferTest {

    public static void main(String[] args) {
        IntBuffer intBuffer = IntBuffer.allocate(20);

        for (int i = 0; i < 3; i++) {
            intBuffer.put(i);
        }
        System.out.println("w limit: " + intBuffer.limit());
        System.out.println("w cap: " + intBuffer.capacity());
        System.out.println("w pos: " + intBuffer.position());

        intBuffer.flip();
        System.out.println("r limit: " + intBuffer.limit());
        System.out.println("r cap: " + intBuffer.capacity());
        System.out.println("r pos: " + intBuffer.position());

        for (int i = 0; i < 3; i++) {
            if (i == 1) {
                intBuffer.mark();
            }
            System.out.println(intBuffer.get());
        }
        System.out.println("after r limit: " + intBuffer.limit());
        System.out.println("after r cap: " + intBuffer.capacity());
        System.out.println("after r pos: " + intBuffer.position());

        intBuffer.reset();
        System.out.println("reset limit: " + intBuffer.limit());
        System.out.println("reset cap: " + intBuffer.capacity());
        System.out.println("reset pos: " + intBuffer.position());

        intBuffer.rewind();
        for (int i = 0; i < 3; i++) {
            System.out.println(intBuffer.get());
        }
        System.out.println("after r2 limit: " + intBuffer.limit());
        System.out.println("after r2 cap: " + intBuffer.capacity());
        System.out.println("after r2 pos: " + intBuffer.position());

        intBuffer.clear();
//        intBuffer.compact();
        System.out.println("w limit: " + intBuffer.limit());
        System.out.println("w cap: " + intBuffer.capacity());
        System.out.println("w pos: " + intBuffer.position());
    }
}

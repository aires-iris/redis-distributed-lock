package com.aires.test;

import lombok.extern.slf4j.Slf4j;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/26 11:42
 */
public class Test {

    private static int count = 0;

    public static void main(String[] args) throws InterruptedException {


        Thread t1 = new Thread(Test::addSafe,"t1");
        Thread t2 = new Thread(Test::addSafe,"t2");
        t1.start();
        t2.start();

        t1.join();
        t2.join();
        System.out.println(count);
    }
    private static LockBaseOnAQS lock =new LockBaseOnAQS();


    private static void add() {
        for (int i = 0; i < 10000; i++) {
           count++;
        }
    }

    private static void addSafe() {
        lock.lock();
        for (int i = 0; i < 10000; i++) {
            count++;
        }
        lock.unlock();
    }

}

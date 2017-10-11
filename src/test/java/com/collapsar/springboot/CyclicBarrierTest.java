package com.collapsar.springboot;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by chenyong6 on 2017/9/29.
 */
public class CyclicBarrierTest {
    public static void main(String[] args) {

        final CyclicBarrier cb=new CyclicBarrier(3);


        Thread t1=new Thread(){
            public void run(){
                try {
                    Thread.sleep(2000);
                    System.out.println("张三说：我的杯子已经端起来了");
                    cb.await();
                    System.out.println("张三说：干！");
                    Thread.sleep(2000);
                    System.out.println("张三说：喝完了！");
                    cb.await();
                    System.out.println("张三说：好酒！");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread t2=new Thread(){
            public void run(){
                try {
                    Thread.sleep(3000);
                    System.out.println("李四说：我的杯子已经端起来了");
                    cb.await();
                    System.out.println("李四说：干！");
                    Thread.sleep(3000);
                    System.out.println("李四说：喝完了！");
                    cb.await();
                    System.out.println("李四说：好酒！");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread t3=new Thread(){
            public void run(){
                try {
                    Thread.sleep(1000);
                    System.out.println("王五说：我的杯子已经端起来了");
                    cb.await();
                    System.out.println("王五说：干！");
                    Thread.sleep(1000);
                    System.out.println("王五说：喝完了！");
                    cb.await();
                    System.out.println("王五说：好酒！");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        };

        t1.start();
        t2.start();
        t3.start();

    }
}

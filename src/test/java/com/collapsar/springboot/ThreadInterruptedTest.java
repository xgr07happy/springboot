package com.collapsar.springboot;

/**
 * Created by chenyong6 on 2017/9/29.
 */
public class ThreadInterruptedTest {
    public static void main(String[] args) {
        ThreadInterruptedTest main = new ThreadInterruptedTest();
        Thread t = new Thread(main.runnable);
        System.out.println("main");
        t.start();
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        t.interrupt();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int i = 0;
//            while(i<1000)
//                System.out.println(i++);
            try {
                while (i < 1000) {
                    Thread.sleep(500);
                    System.out.println(i++);
                }
            } catch (InterruptedException e) {
                System.out.println("interrupted");
                e.printStackTrace();
            }
        }
    };
}

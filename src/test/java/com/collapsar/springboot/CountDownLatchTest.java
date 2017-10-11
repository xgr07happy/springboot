package com.collapsar.springboot;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * Created by chenyong6 on 2017/9/29.
 */
public class CountDownLatchTest {
    public static void main(String[] args) throws Exception {
        final CyclicBarrier barrier = new CyclicBarrier(3);//final CountDownLatch latch=new CountDownLatch(2);
        ExecutorService  executor = new ThreadPoolExecutor(2, 2, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        System.out.println("我是店主：饭店正在营业，等客人吃完饭之后再打烊...");
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try{
                    System.out.println("第1桌客人进来吃饭...");
                    Thread.sleep(2000);//模拟任务耗时
                    System.out.println("第1桌客人进来吃完了...");
                    barrier.await();//latch.countDown();
                }catch (Exception ex){}

            }
        });
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try{
                    System.out.println("第2桌客人进来吃饭...");
                    Thread.sleep(3000);//模拟任务耗时
                    System.out.println("第2桌客人进来吃完了...");
                    barrier.await();//latch.countDown();
                }catch (Exception ex){}

            }
        });
        barrier.await();//latch.await();
        System.out.println("我是店主：你们已经吃完了，现在该我来收尾工作，然后打烊了...");
        executor.shutdown();
    }
}

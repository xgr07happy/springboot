package com.collapsar.springboot;

import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

/**
 * Created by chenyong6 on 2017/8/25.
 */
public class BackoffPolicyTest {

    public static void main(String[] args){
        MyBackOffPolicy myBackOffPolicy = new MyBackOffPolicy(2000);
        System.out.println("before...");
        myBackOffPolicy.backoff();
        System.out.println("end.");
    }


    public static class MyBackOffPolicy extends FixedBackOffPolicy{

        public MyBackOffPolicy(long interval){
            this.setBackOffPeriod(interval);
        }

        public void backoff(){
            super.doBackOff();
        }
    }
}

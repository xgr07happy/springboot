package com.collapsar.springboot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Created by chenyong6 on 2017/8/24.
 */
@Slf4j
@Service
public class RetryService {



    public Object doSthRetryable2() throws Throwable{
        RetryTemplate retryTemplate = new RetryTemplate();
        RetryPolicy retryPolicy = new SimpleRetryPolicy(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate.execute(new RetryCallback<Object, Throwable>() {

            @Override
            public Object doWithRetry(RetryContext retryContext) throws Throwable {
                System.out.println("retry cnt=" + retryContext.getRetryCount());
                boolean b = new Random().nextBoolean();
                if(b){
                    throw new RuntimeException("AAAAAAAAAAAA");
                }
                return "OK";
            }

        }, new RecoveryCallback<Object>() {

            @Override
            public Object recover(RetryContext retryContext) throws Exception {
                System.out.println("default");
                return null;
            }

        });

    }


}

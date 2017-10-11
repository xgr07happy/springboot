package com.collapsar.springboot.service;

import com.collapsar.springboot.config.MyRocketConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by chenyong6 on 2017/8/30.
 */
@Slf4j
@Service
public class RocketService {
    @Autowired
    private DefaultMQProducer defaultMQProducer;
    @Autowired
    private DefaultMQPushConsumer defaultMQPushConsumer;
    @Value("${spring.rocketmq.mysend.topic}")
    private String topic;
    @Value("${spring.rocketmq.mysend.tag}")
    private String tag;


    public void sendMessage() throws Exception{
        String data = UUID.randomUUID().toString();
        Message msg = new Message(topic, tag, "ORDER001", data.getBytes("utf-8"));
        this.defaultMQProducer.send(msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("@@@@@send success.");
            }

            @Override
            public void onException(Throwable e) {
                log.info("@@@@@send faild.");
            }
        });
    }



    @EventListener(condition = "#event.topic=='mytopic'")
    public void consume(MyRocketConfig.RocketmqEvent event){
        DefaultMQPushConsumer consumer = event.getConsumer();
        try{
            log.info("@@@@@consume msg={}", event.getMsg());
        }catch (Exception ex){
            if(event.getMsg().getReconsumeTimes() <= 3){
                log.error("@@@@@reconsume msg", ex);
                try{
                    consumer.sendMessageBack(event.getMsg(), 2);
                }catch (Exception e){
                    log.error("@@@@@sendMessageBack err.", e);
                }
            }else {
                log.info("@@@@@reconsume msg expried.");
            }
        }
    }


}

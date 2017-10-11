package com.collapsar.springboot.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by chenyong6 on 2017/8/28.
 */
@Slf4j
@Service
public class KafkaService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Value("${spring.kafka.template.defaultTopic}")
    private String topic;


    public void produce(){
        for(int i=0; i<5; i++){
            String data = UUID.randomUUID().toString();
            this.kafkaTemplate.send(topic, data);
            log.info("@@@@@send:"+data);
        }

        this.kafkaTemplate.setProducerListener(new ProducerListener<String, String>() {
            @Override
            public void onSuccess(String topic, Integer partition, String key, String value, RecordMetadata recordMetadata) {
                log.info("succ on send :" + value);
            }

            @Override
            public void onError(String topic, Integer partition, String key, String value, Exception exception) {
                log.info("error on send :" + value);
            }

            @Override
            public boolean isInterestedInSuccess() {
                return true;
            }
        });
    }


    @KafkaListener(topics = {"mytopic"})
    public void consume(ConsumerRecord data){
        log.info("######recv:"+data.toString());
    }

}

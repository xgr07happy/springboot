package com.collapsar.springboot;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.collapsar.springboot.sharding.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenyong6 on 2017/7/31.
 */
@Slf4j
@SpringBootApplication
@PropertySource(ignoreResourceNotFound = true, value = {
        "classpath:properties/datasource.properties",
        "classpath:properties/redis.properties",
        "classpath:properties/rocket.properties",
        "classpath:properties/kafka.properties",
        "classpath:properties/dubbo.properties",
        "classpath:properties/elasticsearch.properties",
        "file:/config/prod.properties"})
public class Application{// implements CommandLineRunner{

    public static void main(String[] args){
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
//        log.info("######{}", context.getBean("configGlobalMapper"));
//        log.info("######{}", context.getBean("testConfig"));
    }

//    @Override
//    public void run(String... args) throws Exception {
//        log.info("##################server is ready for service.");
//    }

}

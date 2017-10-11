package com.collapsar.springboot.controller;

import com.collapsar.springboot.model.Article;
import com.collapsar.springboot.service.*;
import com.collapsar.springboot.sharding.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by chenyong6 on 2017/8/1.
 */
@Slf4j
@RestController
@RequestMapping("test")
public class TestController {
    @Autowired
    private RedisService redisService;
    @Autowired
    private ShardingService shardingService;
    @Autowired
    private RetryService retryService;
    @Autowired
    private KafkaService kafkaService;
    @Autowired
    private RocketService rocketService;
    @Autowired
    private ElasticsearchService elasticSearchService;
    @Autowired
    private DubboService dubboService;



    @RequestMapping("es")
    public Object elasticsearch(){
        Object ret = null;
        try{
            List<Article> list = this.elasticSearchService.listArticles();
            if(null == list || list.size() <= 0){
                log.info("article list is empty.");
            }
            for(Article article : list){
                log.info(article.toString());
            }
        }catch (Throwable ex){
            log.error("eeeeeee=", ex);
        }
        return "OK";
    }


    @RequestMapping("rocket")
    public Object rocket(){
        Object ret = null;
        try{
            this.rocketService.sendMessage();
        }catch (Throwable ex){
            log.error("eeeeeee=", ex);
        }
        return "OK";
    }



    @RequestMapping("kafka")
    public Object kafka(){
        this.kafkaService.produce();
        return "OK";
    }



    @RequestMapping("redis")
    public Object redis(){
        return this.redisService.doSth();
    }



    @RequestMapping("sharding/{bizt}/{key}/{val}")
    public Object sharding(@PathVariable String bizt, @PathVariable String key, @PathVariable String val){
        DataSourceContextHolder.setDataSourceType(bizt);
        this.shardingService.doSth(key, val);
        return "OK";
    }



    @RequestMapping("retry")
    public Object retryDoSth(){
        Object ret = null;
        try{
            ret = retryService.doSthRetryable2();
        }catch (Throwable ex){
            log.error("eeeeeee=", ex);
        }
        return ret;
    }


}

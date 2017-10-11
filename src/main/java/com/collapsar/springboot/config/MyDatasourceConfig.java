package com.collapsar.springboot.config;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.collapsar.springboot.sharding.DataSourceContextHolder;
import com.collapsar.springboot.sharding.DynamicDataSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by chenyong6 on 2017/8/4.
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class MyDatasourceConfig {
    private String driverClassName;
    private List<String> biztypes;
    private List<String> dburls;
    private List<String> usernames;
    private List<String> passwords;


    @PostConstruct
    public void init() throws Exception{
        log.debug("init: biztypes={}, dburls={}", StringUtils.collectionToCommaDelimitedString(biztypes), StringUtils.collectionToCommaDelimitedString(dburls));
        if(null == driverClassName || "".equals(driverClassName.trim())){
            log.error("init: driverClassName is empty.");
            throw new RuntimeException("DATA_SOURCE driverClassName is empty.");
        }
        if(null == biztypes || null == dburls || null == usernames || null == passwords
                || biztypes.size() != dburls.size() || dburls.size() != usernames.size() || usernames.size() != passwords.size()){
            log.error("init: init dataSource fail. size of types、urls、usernames、passwords not match.");
            throw new RuntimeException("DATA_SOURCE INIT FAIL(WRONG CONFIG).");
        }
        Set<String> types = new HashSet<String>(biztypes);
        if(types.size() != biztypes.size()){
            log.error("init: config biztypes repeated.");
            throw new RuntimeException("DATA_SOURCE INIT FAIL(WRONG CONFIG).");
        }
    }

    @Bean
    public AbstractRoutingDataSource dynamicDataSource() throws Exception{
        Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
        for(int i=0; i<biztypes.size(); i++){
            Properties props = new Properties();
            props.put("driverClassName", driverClassName);
            props.put("url", dburls.get(i));
            props.put("username", usernames.get(i));
            props.put("password", passwords.get(i));
            targetDataSources.put(biztypes.get(i), DruidDataSourceFactory.createDataSource(props));
        }
        AbstractRoutingDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);

        DataSourceContextHolder.setSupportdDataSourceTypes(biztypes);

        return dynamicDataSource;
    }


}

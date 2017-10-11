package com.collapsar.springboot.service;

import com.collapsar.springboot.dao.mapper.ConfigGlobalMapper;
import com.collapsar.springboot.model.database.ConfigGlobal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by chenyong6 on 2017/8/28.
 */
@Service
public class ShardingService {
    @Autowired
    private ConfigGlobalMapper configGlobalMapper;


    public void doSth(String key, String val){
        ConfigGlobal configGlobal = new ConfigGlobal();
        configGlobal.setGlobalKey(key);
        configGlobal.setGlobalValue(val);
        this.configGlobalMapper.insertSelective(configGlobal);
    }
}

package com.collapsar.springboot.sharding;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by chenyong6 on 2017/8/4.
 */
public class DataSourceContextHolder {
    private static final Set<String> ALL_DS_TYPES = new CopyOnWriteArraySet<String>();
    private static final ThreadLocal<String> DS_TYPE = new ThreadLocal<String>();

    public static void setDataSourceType(String type){
        if(ALL_DS_TYPES.contains(type)){
            DS_TYPE.set(type);
        }else {
            throw new RuntimeException("DS_TYPE NO SUPPORTED");
        }
    }

    public static String getDataSourceType(){
        return DS_TYPE.get();
    }

    public static void setSupportdDataSourceTypes(Collection<String> types){
        ALL_DS_TYPES.addAll(types);
    }

    public static Set<String> getSupportedDataSourceTypes(){
        return ALL_DS_TYPES;
    }
}

package com.collapsar.springboot.model;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

/**
 * Created by chenyong6 on 2017/9/12.
 */
@Data
@Document(indexName = "idx_article", type = "article", shards = 5, replicas = 1, indexStoreType = "fs", refreshInterval = "-1")
public class Article implements Serializable{
    private Long id;
    private String title;
    private String content;
    private Long postTime;
}

package com.collapsar.springboot.service;


import com.collapsar.springboot.model.Article;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by chenyong6 on 2017/9/12.
 */
@Slf4j
@Service
public class ElasticsearchService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;



    public void addArticle(Article article){

    }

    public List<Article> listArticles(){
        log.info("listArticles: start.");
        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("spring OR redis");
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(queryBuilder).build();
        return this.elasticsearchTemplate.queryForList(searchQuery, Article.class);
    }
}

/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.service.es;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.helioscloud.atlantis.domain.FrontKey;
import com.helioscloud.atlantis.domain.enumeration.ElasticSearchConstants;
import com.helioscloud.atlantis.persistence.FrontKeyMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 界面Title的ES服务器
 */
@Service
@Slf4j
public class EsFrontKeyInfoSerivce {
    private static final int INDEX_LIMIT = 10000;
    @Autowired
    private FrontKeyMapper frontKeyMapper;
    @Autowired
    ElasticsearchService elasticsearchService;

    public void removeAll() throws IOException {
        elasticsearchService.deleteIndex(ElasticSearchConstants.AUTH_FRONT_KEY);
    }

    //建立索引数据
    public void doIndexTransaction() {
        elasticsearchService.createIndex(ElasticSearchConstants.AUTH_FRONT_KEY, this.getSetting(), this.getMapping());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            this.initAllFrontKey();
            return null;
        });
        executor.execute(futureTask);
        ;
    }

    public void initAllFrontKey() {
        int total = 0;
        int pageCnt = 0;
        Page<FrontKey> page = new Page<FrontKey>();
        page.setCurrent(pageCnt);
        page.setSize(INDEX_LIMIT);
        Long start = System.currentTimeMillis();
        log.info("执行分页查询，到第{}页", pageCnt);
        Page<FrontKey> frontKeyPage = this.selectFrontKeyFromDB(page);
        while (frontKeyPage.getRecords().size() > 0) {
            total = total + frontKeyPage.getRecords().size();
            this.batchIndex(frontKeyPage.getRecords());
            pageCnt++;
            page.setCurrent(pageCnt);
            frontKeyPage = this.selectFrontKeyFromDB(page);
            log.info("执行分页查询，到第{}页", pageCnt);
            if (frontKeyPage.getRecords().size() < INDEX_LIMIT) {
                break;
            }
        }
        log.info("完成界面Title信息索引,耗时:{}ms", System.currentTimeMillis() - start);
    }

    public Page<FrontKey> selectFrontKeyFromDB(Page<FrontKey> page) {
        page.setSearchCount(false);
        Long start = System.currentTimeMillis();
        List<FrontKey> frontKeys = frontKeyMapper.selectPage(page, new EntityWrapper<FrontKey>()
                .eq("is_deleted", false)
                .eq("is_enabled", true));
        log.info("查询界面Title耗时:{}ms,一共获得:{}条数据", System.currentTimeMillis() - start, frontKeys.size());
        page.setRecords(frontKeys);
        return page;
    }

    public void batchIndex(List<FrontKey> frontKeyList) {
        List<FrontKey> esFrontKey = new ArrayList<>();
        log.info("批量创建ES索引，集合共{}条数据", frontKeyList == null ? 0 : frontKeyList.size());
        int i = 0;
        for (FrontKey key : frontKeyList) {
            esFrontKey.add(key);
            i++;
            if (i % INDEX_LIMIT == 0) {
                elasticsearchService.saveIndex(esFrontKey, ElasticSearchConstants.AUTH_FRONT_KEY);
                esFrontKey.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(esFrontKey)) {
            elasticsearchService.saveIndex(esFrontKey, ElasticSearchConstants.AUTH_FRONT_KEY);
        }
    }

    public void saveEsFrontKeyIndex(FrontKey key) {
        log.info("更新ES索引，FrontKey信息为：{}", key.toString());
        elasticsearchService.saveIndex(Arrays.asList(key), ElasticSearchConstants.AUTH_FRONT_KEY);
    }

    public void deleteEsFrontKeyIndex(Long id) throws Exception {
        log.info("删除ES索引，FrontKey id为：{}", id);
        elasticsearchService.deleteDataIndex(Arrays.asList(id), ElasticSearchConstants.AUTH_FRONT_KEY);
    }

    public boolean isElasticSearchEnable() {
        return elasticsearchService.isElasticSearchEnable();
    }


    private XContentBuilder getMapping() {
        XContentBuilder builder = null;
        try {
            builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("properties")
                    .startObject("id").field("type", "keyword").endObject()
                    .startObject("keyCode").field("type", "keyword").endObject()
                    .startObject("lang").field("type", "keyword").endObject()
                    .startObject("descriptions").field("type", "keyword").endObject()
                    .startObject("moduleId").field("type", "keyword").endObject()
                    .startObject("versionNumber").field("type", "keyword").endObject()
                    .startObject("isEnabled").field("type", "keyword").endObject()
                    .startObject("isDeleted").field("type", "keyword").endObject()
                    .endObject()
                    .endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder;
    }

    private XContentBuilder getSetting() {
        XContentBuilder builder = null;
        try {
            builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("analysis")
                    .startObject("analyzer")
                    .startObject("keywordAnalyzer")
                    .field("type", "pattern").field("pattern", " ").field("lowercase", true)
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder;
    }

    /**
     * 判断menuCode是否已经存在
     *
     * @param key
     * @param lang
     * @return
     */
    public Long getFrontKeyByKeyAndLangFrontES(String key, String lang) {
        log.info("从ES中，判断key值({})在语言{}是否已经存在", key,lang);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("keyCode", key));
        queryBuilder.must(QueryBuilders.termQuery("lang", lang));
        return elasticsearchService.searchCount(queryBuilder, ElasticSearchConstants.AUTH_FRONT_KEY);
    }

    /**
     * 根据模块，取所有前端Title 分页
     * @param moduleId  模块Id
     * @param pageable
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<FrontKey> getFrontKeysByModuleIdFromES(Long moduleId, Boolean isEnabled, Pageable pageable) {
        log.info("从ES中，根据moduleId({})，启用标识({}),返回界面Title，分页", moduleId,isEnabled);
        List<FrontKey> results = null;
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (moduleId != null && moduleId > 0) {
            queryBuilder.must(QueryBuilders.termQuery("moduleId", moduleId));
        }
        if(isEnabled != null){
            queryBuilder.must(QueryBuilders.termQuery("isEnabled", isEnabled));
        }
        SortBuilder sortBuilder = SortBuilders.fieldSort("keyCode")
                .order(SortOrder.ASC);
        org.springframework.data.domain.Page<JSONObject> result = elasticsearchService.search(queryBuilder, sortBuilder, ElasticSearchConstants.AUTH_FRONT_KEY, pageable);
        results = JSON.parseArray(JSON.toJSONString(result.getContent()), FrontKey.class);
        return results;
    }

    /**
     * 取所有前端Title 分页
     * @param pageable
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<FrontKey> getFrontKeysFromES(Boolean isEnabled, Pageable pageable) {
        log.info("从ES中，根据启用标识({}),返回界面Title，分页",isEnabled);
        List<FrontKey> results = null;
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if(isEnabled != null){
            queryBuilder.must(QueryBuilders.termQuery("isEnabled", isEnabled));
        }
        SortBuilder sortBuilder = SortBuilders.fieldSort("keyCode")
                .order(SortOrder.ASC);
        org.springframework.data.domain.Page<JSONObject> result = elasticsearchService.search(queryBuilder, sortBuilder, ElasticSearchConstants.AUTH_FRONT_KEY, pageable);
        results = JSON.parseArray(JSON.toJSONString(result.getContent()), FrontKey.class);
        return results;
    }

    /**
     * 根据ID，取FrontKey对象
     * @param id
     * @return
     */
    public FrontKey getFrontKeyByIdFromES(Long id) {
        log.info("从ES中，根据ID({})，取FrontKey对象信息", id);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (id != null && id > 0) {
            queryBuilder.must(QueryBuilders.idsQuery().addIds(id.toString()));
            //queryBuilder.must( QueryBuilders.termQuery("id", id));
        } else {
            return null;
        }
        SearchHits hits = elasticsearchService.searchById(queryBuilder, ElasticSearchConstants.AUTH_FRONT_KEY);
        if (hits.totalHits > 0) {
            FrontKey menu = JSON.parseObject(hits.getAt(0).getSourceAsString(), FrontKey.class);
            return menu;
        }
        return null;
    }
    /**
     * 根据ID，取FrontKey对象
     * @param ids
     * @return
     */
    public List<FrontKey> getFrontKeyByIdsFromES(List<Long> ids) {
        log.info("从ES中，根据IDs({})，取FrontKey对象信息", ids);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        List<FrontKey> results = null;
        if (ids != null && ids.size() >  0) {
            queryBuilder.must(QueryBuilders.idsQuery().addIds(ids.toString()));
            //queryBuilder.must( QueryBuilders.termQuery("id", id));
        } else {
            return null;
        }
        SearchHits hits = elasticsearchService.searchById(queryBuilder, ElasticSearchConstants.AUTH_FRONT_KEY);
        if (hits.totalHits > 0) {
            hits.forEach(e->{
                FrontKey key = JSON.parseObject(e.getSourceAsString(), FrontKey.class);
                results.add(key);
            });
        }
        return results;
    }
    /**
     * 根据模块和Lang，取所有前端Title 分页
     *
     * @param moduleId  模块Id
     * @param lang      语言类型
     * @param pageable
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<FrontKey> getFrontKeysByModuleIdAndLangFromES(Long moduleId, String lang, Boolean isEnabled, Pageable pageable) {
        log.info("从ES中，根据moduleId({})，lang({})，启用标识({}),返回界面Title，分页",moduleId,lang,isEnabled);
        List<FrontKey> results = null;
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("moduleId", moduleId));
        queryBuilder.must(QueryBuilders.termQuery("lang", lang));
        if(isEnabled != null){
            queryBuilder.must(QueryBuilders.termQuery("isEnabled", isEnabled));
        }
        SortBuilder sortBuilder = SortBuilders.fieldSort("keyCode")
                .order(SortOrder.ASC);
        org.springframework.data.domain.Page<JSONObject> result = elasticsearchService.search(queryBuilder, sortBuilder, ElasticSearchConstants.AUTH_FRONT_KEY, pageable);
        results = JSON.parseArray(JSON.toJSONString(result.getContent()), FrontKey.class);
        return results;
    }
    /**
     * 根据模块和Lang，取所有前端Title 不分页
     * @param lang      语言类型
     * @return
     */
    public List<FrontKey> getFrontKeysByLangFromES(String lang) {
        log.info("从ES中，根据lang({}),取所有界面Title",lang);
        List<FrontKey> list = new ArrayList<>();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("lang", lang));
        SearchHits hits = elasticsearchService.searchHits(queryBuilder, ElasticSearchConstants.AUTH_FRONT_KEY);
        if (hits.totalHits > 0) {
            Arrays.stream(hits.getHits()).forEach(e -> {
                FrontKey frontKey = JSON.parseObject(e.getSourceAsString(), FrontKey.class);
                list.add(frontKey);
            });
            return list;
        }
        return list;
    }
}

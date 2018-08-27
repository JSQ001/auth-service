/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.service.es;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloudhelios.atlantis.domain.enumeration.ElasticSearchConstants;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Project Name:contract
 * Package Name:com.cloudhelios.atlantis.contract.service.es
 * Date:2018/5/4
 * Create By:zhiyu.liu@hand-china.com
 */
@Service
@Slf4j
public class ElasticsearchService {
    @Autowired
    TransportClient transportClient;
    @Value("${elasticsearch.enable:false}")
    private boolean enable;
    private static final int MAX_ES_HITS = 10000;
    public Page<JSONObject> search(QueryBuilder builder, SortBuilder sortBuilder, String index, Pageable pageable) {
        int current = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int from = current * size;
        if (from > MAX_ES_HITS) {
            from = MAX_ES_HITS - size;
        }
        SearchResponse scrollResp = transportClient.prepareSearch(index).setFrom(from).setSize(size).setQuery(builder).setTypes(ElasticSearchConstants.DEFAULT_INDEX_TYPE).addSort(sortBuilder).get();
        List<JSONObject> result = new ArrayList<JSONObject>();
        for (SearchHit hit : scrollResp.getHits().getHits()) {
            result.add(JSONObject.parseObject(hit.getSourceAsString()));
        }
        Page<JSONObject> pageableResult = new PageImpl<JSONObject>(result, pageable, scrollResp.getHits().getTotalHits());
        return pageableResult;
    }


    public void saveIndex(Collection<?> objectList, String index) {
        if (enable) {
            BulkRequestBuilder bulkRequest = transportClient.prepareBulk().setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
            JSONArray elDataList = JSONArray.parseArray(JSONObject.toJSONString(objectList));

            for (int i = 0; i < elDataList.size(); i++) {
                bulkRequest.add(transportClient.prepareIndex(index, ElasticSearchConstants.DEFAULT_INDEX_TYPE, elDataList.getJSONObject(i).getString("id"))
                        .setSource(elDataList.getJSONObject(i))
                );
            }
            BulkResponse bulkResponse = bulkRequest.get();
            if (bulkResponse.hasFailures()) {
                // process failures by iterating through each bulk response item
                log.error(bulkResponse.buildFailureMessage());
            }

        } else {
            log.warn("未启用elasticsearch");
        }
    }

    public void createIndex(String index, XContentBuilder setting, XContentBuilder source) {
        if(enable){
            IndicesAdminClient indicesAdminClient = transportClient.admin().indices();
            if (!indicesAdminClient.prepareExists(index).get().isExists()) {
                log.info("开始创建索引");
                indicesAdminClient.prepareCreate(index).setSettings(setting).addMapping(ElasticSearchConstants.DEFAULT_INDEX_TYPE,
                        source
                ).get();
            }
        }
        else{
            log.warn("未启用elasticsearch");
        }

    }

    public void deleteDataIndex(Collection<?> objectList, String index) throws IOException {
        BulkRequestBuilder bulkRequest = transportClient.prepareBulk().setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        JSONArray elDataList = JSONArray.parseArray(JSONObject.toJSONString(objectList));
        for (int i = 0; i < elDataList.size(); i++) {
            bulkRequest.add(transportClient.prepareDelete(index, ElasticSearchConstants.DEFAULT_INDEX_TYPE, elDataList.get(i).toString()));
            //  transportClient.prepareDelete(index,ElasticSearchConstants.DEFAULT_INDEX_TYPE, elDataList.getJSONObject(i).getString("id"));
        }
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            // process failures by iterating through each bulk response item
            log.error(bulkResponse.buildFailureMessage());
        }

    }
    public void deleteIndex(String index) throws IOException {
        if(enable){
            IndicesExistsRequest inExistsRequest = new IndicesExistsRequest(index);
            IndicesExistsResponse inExistsResponse = transportClient.admin().indices()
                    .exists(inExistsRequest).actionGet();
            if (inExistsResponse.isExists()) {
                log.info("索引库:{}，存在，执行删除", index);
                DeleteIndexResponse dResponse = transportClient.admin().indices().prepareDelete(index)
                        .execute().actionGet();
                if (dResponse.isAcknowledged()) {
                    log.info("删除索引库成功");
                }
            }
            else{
                log.info("索引:{}不存在",index);
            }
        }
        else{
            log.warn("未启用elasticsearch");
        }
    }
    public boolean isElasticSearchEnable() {
        return enable;
    }
}

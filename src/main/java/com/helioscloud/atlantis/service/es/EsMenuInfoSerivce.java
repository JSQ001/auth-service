/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.cloudhelios.atlantis.payment.service.es;



import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.payment.EsCashTransactionClassDTO;
import com.cloudhelios.atlantis.payment.domain.enumeration.ElasticSearchConstants;
import com.cloudhelios.atlantis.payment.persistence.EsCashTransactionClassMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Project Name:artemis
 * Package Name:com.cloudhelios.atlantis.payment.service.es
 * Date:2018/6/4
 * Create By:zhu.zhao@hand-china.com
 */
@Service
@Slf4j
public class EsCashTransactionClassInfoSerivce {
    private static final int INDEX_LIMIT = 10000;
    @Autowired
    private EsCashTransactionClassMapper esCashTransactionClassMapper;
    @Autowired
    ElasticsearchService elasticsearchService;

    public void removeAll() throws IOException {
        elasticsearchService.deleteIndex(ElasticSearchConstants.CASHTRANSACTIONCLASS_INDEX);
    }
    //建立索引数据
    public void doIndexTransaction() {
        elasticsearchService.createIndex(ElasticSearchConstants.CASHTRANSACTIONCLASS_INDEX,this.getSetting(),this.getMapping());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            this.initAllCashTransactionClass();
            return null;
        });
        executor.execute(futureTask);;
    }

    public void initAllCashTransactionClass() {
        int total = 0;
        int pageCnt = 0;
        Page<EsCashTransactionClassDTO> page = new Page<EsCashTransactionClassDTO>();
        page.setCurrent(pageCnt);
        page.setSize(INDEX_LIMIT);
        Long start = System.currentTimeMillis();
        log.info("执行分页查询，到第{}页", pageCnt);
        Page<EsCashTransactionClassDTO> allCashTransactionClass = this.selectCashTransactionClassFromDB(page);
        while (allCashTransactionClass.getRecords().size() > 0) {
            total = total + allCashTransactionClass.getRecords().size();
            this.batchIndex(allCashTransactionClass.getRecords());
            pageCnt++;
            page.setCurrent(pageCnt);
            allCashTransactionClass = this.selectCashTransactionClassFromDB(page);
            log.info("执行分页查询，到第{}页", pageCnt);
            if (allCashTransactionClass.getRecords().size() < INDEX_LIMIT) {
                this.batchIndex(allCashTransactionClass.getRecords());
                break;
            }
        }
        log.info("完成现金事务分类信息索引,耗时:{}ms", System.currentTimeMillis() - start);
    }

    public Page<EsCashTransactionClassDTO> selectCashTransactionClassFromDB(Page<EsCashTransactionClassDTO> page) {
        page.setSearchCount(false);
        Long start = System.currentTimeMillis();
        List<EsCashTransactionClassDTO> allCashTransactionClass =esCashTransactionClassMapper.selectAllCashTransactionClass(page); //
        log.info("查询现金事务分类耗时:{}ms,一共获得:{}条数据", System.currentTimeMillis() - start, allCashTransactionClass.size());
        page.setRecords(allCashTransactionClass);
        return page;
    }

    private void batchIndex(List<EsCashTransactionClassDTO> allCashTransactionClass) {
        List<EsCashTransactionClassDTO> esCashTransactionClassDTO = new ArrayList<>();
        int i = 0;
        for (EsCashTransactionClassDTO cashTransactionClass : allCashTransactionClass) {
            esCashTransactionClassDTO.add(cashTransactionClass);
            i++;
            if (i % INDEX_LIMIT == 0) {
                elasticsearchService.saveIndex(esCashTransactionClassDTO, ElasticSearchConstants.CASHTRANSACTIONCLASS_INDEX);
                esCashTransactionClassDTO.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(esCashTransactionClassDTO)) {
            elasticsearchService.saveIndex(esCashTransactionClassDTO, ElasticSearchConstants.CASHTRANSACTIONCLASS_INDEX);
        }
    }

    public void saveEsCashTransactionClassDTOIndex(EsCashTransactionClassDTO cashTransactionClass) {
        elasticsearchService.saveIndex(Arrays.asList(cashTransactionClass), ElasticSearchConstants.CASHTRANSACTIONCLASS_INDEX);
    }
    public void deleteEsCashTransactionClassDTOIndex(Long  id)throws Exception {
        elasticsearchService.deleteDataIndex(Arrays.asList(id), ElasticSearchConstants.CASHTRANSACTIONCLASS_INDEX);
    }
    public boolean isElasticSearchEnable(){
        return elasticsearchService.isElasticSearchEnable();
    }




    private XContentBuilder getMapping(){
        XContentBuilder builder = null;
        try {
            builder = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties")
                .startObject("id").field("type", "keyword").endObject()
                .startObject("setOfBookId").field("type", "keyword").endObject()
                .startObject("typeCode").field("type", "keyword").endObject()
                .startObject("classCode").field("type", "keyword").endObject()
                .startObject("description").field("type", "keyword").endObject()
                .startObject("isEnabled").field("type", "keyword").endObject()
                .startObject("isDeleted").field("type", "keyword").endObject()
                .endObject()
                .endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder;
    }
    private XContentBuilder getSetting(){
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
}

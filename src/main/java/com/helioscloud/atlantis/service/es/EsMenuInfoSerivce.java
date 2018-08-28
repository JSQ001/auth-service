/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.service.es;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.helioscloud.atlantis.domain.Menu;
import com.helioscloud.atlantis.domain.enumeration.ElasticSearchConstants;
import com.helioscloud.atlantis.persistence.MenuMapper;
import lombok.extern.slf4j.Slf4j;
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

@Service
@Slf4j
public class EsMenuInfoSerivce {
    private static final int INDEX_LIMIT = 10000;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    ElasticsearchService elasticsearchService;

    public void removeAll() throws IOException {
        elasticsearchService.deleteIndex(ElasticSearchConstants.AUTH_MENU);
    }

    //建立索引数据
    public void doIndexTransaction() {
        elasticsearchService.createIndex(ElasticSearchConstants.AUTH_MENU, this.getSetting(), this.getMapping());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            this.initAllMenu();
            return null;
        });
        executor.execute(futureTask);
        ;
    }

    public void initAllMenu() {
        int total = 0;
        int pageCnt = 0;
        Page<Menu> page = new Page<Menu>();
        page.setCurrent(pageCnt);
        page.setSize(INDEX_LIMIT);
        Long start = System.currentTimeMillis();
        log.info("执行分页查询，到第{}页", pageCnt);
        Page<Menu> menuPage = this.selectMenuFromDB(page);
        while (menuPage.getRecords().size() > 0) {
            total = total + menuPage.getRecords().size();
            this.batchIndex(menuPage.getRecords());
            pageCnt++;
            page.setCurrent(pageCnt);
            menuPage = this.selectMenuFromDB(page);
            log.info("执行分页查询，到第{}页", pageCnt);
            if (menuPage.getRecords().size() < INDEX_LIMIT) {
                //   this.batchIndex(allCashTransactionClass.getRecords());
                break;
            }
        }
        log.info("完成菜单信息索引,耗时:{}ms", System.currentTimeMillis() - start);
    }

    public Page<Menu> selectMenuFromDB(Page<Menu> page) {
        page.setSearchCount(false);
        Long start = System.currentTimeMillis();
        List<Menu> menuList = menuMapper.selectPage(page, new EntityWrapper<Menu>()
                .eq("is_deleted", false)
                .eq("is_enabled", true));
        log.info("查询菜单耗时:{}ms,一共获得:{}条数据", System.currentTimeMillis() - start, menuList.size());
        page.setRecords(menuList);
        return page;
    }

    private void batchIndex(List<Menu> menuList) {
        List<Menu> esMenuList = new ArrayList<>();
        int i = 0;
        for (Menu menu : menuList) {
            esMenuList.add(menu);
            i++;
            if (i % INDEX_LIMIT == 0) {
                elasticsearchService.saveIndex(esMenuList, ElasticSearchConstants.AUTH_MENU);
                esMenuList.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(esMenuList)) {
            elasticsearchService.saveIndex(esMenuList, ElasticSearchConstants.AUTH_MENU);
        }
    }

    public void saveEsMenuIndex(Menu menu) {
        elasticsearchService.saveIndex(Arrays.asList(menu), ElasticSearchConstants.AUTH_MENU);
    }

    public void deleteEsMenuIndex(Long id) throws Exception {
        elasticsearchService.deleteDataIndex(Arrays.asList(id), ElasticSearchConstants.AUTH_MENU);
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
                    .startObject("menuCode").field("type", "keyword").endObject()
                    .startObject("menuName").field("type", "keyword").endObject()
                    .startObject("menuTypeEnum").field("type", "keyword").endObject()
                    .startObject("seqNumber").field("type", "keyword").endObject()
                    .startObject("parentMenuId").field("type", "keyword").endObject()
                    .startObject("menuIcon").field("type", "keyword").endObject()
                    .startObject("menuUrl").field("type", "keyword").endObject()
                    .startObject("hasChildCatalog").field("type", "keyword").endObject()
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

   /* public Integer getMenuCountByMenuCodeFromES(String menuCode){
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(menuCode)) {
            queryBuilder.must(QueryBuilders.termQuery("menuCode", menuCode));
        } else {
            return null;
        }
        SortBuilder sortBuilder = SortBuilders.fieldSort("id")
                .order(SortOrder.ASC);
        Pageable pageable = pa
        org.springframework.data.domain.Page<JSONObject> result = elasticsearchService.search(queryBuilder, sortBuilder, ElasticSearchConstants.AUTH_MENU, null);
        results = JSON.parseArray(JSON.toJSONString(result.getContent()), CompanyInfoDTO.class);
        org.springframework.data.domain.Page<CompanyInfoDTO> page = new PageImpl<CompanyInfoDTO>(results);
    }*/
}

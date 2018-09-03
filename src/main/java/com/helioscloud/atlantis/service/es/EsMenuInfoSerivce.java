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
import com.helioscloud.atlantis.domain.Menu;
import com.helioscloud.atlantis.domain.enumeration.ElasticSearchConstants;
import com.helioscloud.atlantis.persistence.MenuMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
                .eq("deleted", false)
                .eq("enabled", true));
        log.info("查询菜单耗时:{}ms,一共获得:{}条数据", System.currentTimeMillis() - start, menuList.size());
        page.setRecords(menuList);
        return page;
    }

    public void batchIndex(List<Menu> menuList) {
        List<Menu> esMenuList = new ArrayList<>();
        log.info("批量创建ES索引，集合共{}条数据", menuList == null ? 0 : menuList.size());
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
        log.info("更新ES索引，menu信息为：{}", menu.toString());
        elasticsearchService.saveIndex(Arrays.asList(menu), ElasticSearchConstants.AUTH_MENU);
    }

    public void deleteEsMenuIndex(Long id) throws Exception {
        log.info("删除ES索引，menu id为：{}", id);
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
                    .startObject("enabled").field("type", "keyword").endObject()
                    .startObject("isDeleted").field("type", "keyword").endObject()
                    .startObject("fromSource").field("type", "keyword").endObject()
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
     * @param menuCode
     * @return
     */
    public Long getMenuCountByMenuCodeFromES(String menuCode) {
        log.info("从ES中，判断menuCode({})是否已经存在", menuCode);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(menuCode)) {
            queryBuilder.must(QueryBuilders.termQuery("menuCode", menuCode));
        } else {
            return null;
        }
        return elasticsearchService.searchCount(queryBuilder, ElasticSearchConstants.AUTH_MENU);
    }

    /**
     * 根据父ID，menuType，取子menu的数量
     *
     * @param parentMenuId
     * @param menuType
     * @return
     */
    public Long getMenuCountByParentIdAndTypeFromES(Long parentMenuId, Integer menuType) {
        log.info("从ES中，根据父ID({})，menuType({}),取子menu的数量", parentMenuId,menuType);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (parentMenuId != null && parentMenuId > 0) {
            queryBuilder.must(QueryBuilders.termQuery("parentMenuId", parentMenuId));
        }
        if (menuType != null && menuType > 0) {
            queryBuilder.must(QueryBuilders.termQuery("menuTypeEnum", menuType));
        }
        return elasticsearchService.searchCount(queryBuilder, ElasticSearchConstants.AUTH_MENU);
    }

    /**
     * 根据ID，取Menu对象
     *
     * @param id
     * @return
     */
    public Menu getMenuByIdFromES(Long id) {
        log.info("从ES中，根据ID({})，取menu对象信息", id);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (id != null && id > 0) {
            queryBuilder.must(QueryBuilders.termQuery("id", id));
        } else {
            return null;
        }
        SearchHits hits = elasticsearchService.searchById(queryBuilder, ElasticSearchConstants.AUTH_MENU);
        if (hits.totalHits > 0) {
            Menu menu = JSON.parseObject(hits.getAt(0).getSourceAsString(), Menu.class);
            return menu;
        }
        return null;
    }

    /**
     * 根据parentMenuId，返回启用的子菜单，不分页
     *
     * @param parentMenuId
     * @return
     */
    public List<Menu> getMenuListByParentMenuIdFromES(Long parentMenuId) {
        log.info("从ES中，根据parentMenuId({})，返回启用的子菜单，不分页", parentMenuId);
        List<Menu> list = new ArrayList<>();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (parentMenuId != null && parentMenuId > 0) {
            queryBuilder.must(QueryBuilders.termQuery("parentMenuId", parentMenuId));
            queryBuilder.must(QueryBuilders.termQuery("enabled", true));
        } else {
            return list;
        }
        SearchHits hits = elasticsearchService.searchHits(queryBuilder, ElasticSearchConstants.AUTH_MENU);
        if (hits.totalHits > 0) {
            Arrays.stream(hits.getHits()).forEach(e -> {
                Menu menu = JSON.parseObject(e.getSourceAsString(), Menu.class);
                list.add(menu);
            });
            return list;
        }
        return list;
    }

    /**
     * 根据parentMenuId，返回启用的子菜单，分页
     *
     * @param parentMenuId
     * @return
     */
    public List<Menu> getMenuPageByParentMenuIdFromES(Long parentMenuId,Boolean enabled, Pageable pageable) {
        log.info("从ES中，根据parentMenuId({})，启用标识({}),返回子菜单，分页", parentMenuId,enabled);
        List<Menu> results = null;
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (parentMenuId != null && parentMenuId > 0) {
            queryBuilder.must(QueryBuilders.termQuery("parentMenuId", parentMenuId));
        }
        if(enabled != null){
            queryBuilder.must(QueryBuilders.termQuery("enabled", enabled));
        }
        SortBuilder sortBuilder = SortBuilders.fieldSort("menuCode")
                .order(SortOrder.ASC);
        org.springframework.data.domain.Page<JSONObject> result = elasticsearchService.search(queryBuilder, sortBuilder, ElasticSearchConstants.AUTH_MENU, pageable);
        results = JSON.parseArray(JSON.toJSONString(result.getContent()), Menu.class);
        return results;
    }

    /**
     * 所有菜单 分页
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Menu> getMenuPagesFromES(Boolean enabled, Pageable pageable) {
        log.info("从ES中，取所有菜单 分页");
        List<Menu> results = null;
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (enabled != null) {
            queryBuilder.must(QueryBuilders.termQuery("enabled", true));
        }
        SortBuilder sortBuilder = SortBuilders.fieldSort("seqNumber")
                .order(SortOrder.ASC);
        org.springframework.data.domain.Page<JSONObject> result = elasticsearchService.search(queryBuilder, sortBuilder, ElasticSearchConstants.AUTH_MENU, pageable);
        results = JSON.parseArray(JSON.toJSONString(result.getContent()), Menu.class);
        return results;
    }
}

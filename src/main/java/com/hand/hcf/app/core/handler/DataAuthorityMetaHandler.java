package com.hand.hcf.app.core.handler;


import com.hand.hcf.app.core.web.dto.DataAuthValuePropertyDTO;

import java.util.List;
import java.util.Map;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/17 11:34
 * @remark 数据权限配置信息获取
 */
public interface DataAuthorityMetaHandler {

    /**
     * 判断是否启用数据权限，以及该功能是否使用数据权限
     * @return
     */
    boolean checkEnabledDataAuthority();

    /**
     * 更新数据权限表信息，当数据库中维护了该信息，以数据库中信息为准
     * @param dataAuthMap
     * @return
     */
    Map<String,String> updateDataAuthMap(Map<String, String> dataAuthMap);

    /**
     * 获取数据权限筛选值配置
     * key为数据权限规则名称，value为具体属性
     * 当某个规则下，所有列取值范围均为全部时，对应规则明细，直接返回空数组
     * @return
     */
    List<Map<String,List<DataAuthValuePropertyDTO>>> getDataAuthValueProperties();
}

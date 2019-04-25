package com.hand.hcf.app.mdata.data;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.hand.hcf.app.common.co.DataAuthTablePropertyCO;
import com.hand.hcf.app.core.util.DataAuthorityUtil;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.web.dto.DataAuthValuePropertyDTO;
import com.hand.hcf.app.mdata.implement.web.DataAuthControllerImpl;
import com.hand.hcf.app.mdata.implement.web.ParameterControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/29 16:33
 * @remark 数据权限配置信息具体实现
 */
@Component
public class DataAuthMetaRealization {

    @Autowired
    private DataAuthControllerImpl dataAuthClient;

    @Autowired
    private ParameterControllerImpl parameterClient;

    /**
     * 判断是否启用数据权限，以及该功能是否使用数据权限
     * @return
     */
    public boolean checkEnabledDataAuthority() {
        //jiu.zhao 修改三方接口
        //String dataAuthority = parameterClient.getParameterValueByParameterCode("DATA_AUTHORITY", null, null);
        String dataAuthority = parameterClient.getParameterValueByParameterCode("DATA_AUTHORITY", LoginInformationUtil.getCurrentTenantId(), null, null);
        if(StringUtils.isNotEmpty(dataAuthority)){
            return "Y".equals(dataAuthority);
        }
        return false;
    }

    /**
     * 更新数据权限表信息，当标识中维护了该信息，以标识信息为准
     * @param dataAuthMap
     * @return
     */
    public Map<String, String> updateDataAuthMap(Map<String, String> dataAuthMap) {
        if(dataAuthClient != null){
            String tableName = DataAuthorityUtil.getMapValue(dataAuthMap,DataAuthorityUtil.TABLE_NAME);
            List<DataAuthTablePropertyCO> dataAuthTablePropertiesByTableName = dataAuthClient.getDataAuthTablePropertiesByTableName(tableName);
            if(CollectionUtils.isNotEmpty(dataAuthTablePropertiesByTableName)){
                dataAuthTablePropertiesByTableName.stream().forEach(dataAuthTablePropertyCO -> {
                    if(! DataAuthorityUtil.getMapContainsKey(dataAuthMap,dataAuthTablePropertyCO.getDataType())){
                        DataAuthorityUtil.setMapEntry(dataAuthMap,dataAuthTablePropertyCO.getDataType(),
                                DataAuthorityUtil.getColumnDataAuthTypeLabelValue(dataAuthTablePropertyCO.getColumnName(),
                                        dataAuthTablePropertyCO.getFilterMethod(),
                                        dataAuthTablePropertyCO.getCustomSql()));
                    }
                });
            }
        }
        return dataAuthMap;
    }

    /**
     * 获取数据权限筛选值配置
     * key为数据权限规则名称，value为具体属性
     * 当某个规则下，所有列取值范围均为全部时，对应规则明细，直接返回空数组
     * @return
     */
    public List<Map<String, List<DataAuthValuePropertyDTO>>> getDataAuthValueProperties() {
        if(dataAuthClient != null) {
            return dataAuthClient.getDataAuthValuePropertiesByRequest();
        }
        return new ArrayList<>();
    }
}

package com.hand.hcf.app.base.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.app.base.domain.DataAuthTableProperty;
import com.hand.hcf.app.base.persistence.DataAuthTablePropertyMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/29 17:31
 * @remark
 */
@Service
@AllArgsConstructor
public class DataAuthTablePropertyService extends BaseService<DataAuthTablePropertyMapper,DataAuthTableProperty>{

    private final DataAuthTablePropertyMapper dataAuthTablePropertyMapper;

    public List<DataAuthTableProperty> getDataAuthTablePropertiesByTableName(String tableName){
        return dataAuthTablePropertyMapper.selectList(new EntityWrapper<DataAuthTableProperty>().eq("table_name", tableName)
                .eq("enabled", true));
    }
}

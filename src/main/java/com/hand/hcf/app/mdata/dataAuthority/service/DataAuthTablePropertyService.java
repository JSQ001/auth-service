package com.hand.hcf.app.mdata.dataAuthority.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.mdata.dataAuthority.domain.DataAuthTableProperty;
import com.hand.hcf.app.mdata.dataAuthority.persistence.DataAuthTablePropertyMapper;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/29 17:31
 * @remark
 */
@Service
@AllArgsConstructor
public class DataAuthTablePropertyService extends BaseService<DataAuthTablePropertyMapper,DataAuthTableProperty> {

    private final DataAuthTablePropertyMapper dataAuthTablePropertyMapper;

    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;

    public List<DataAuthTableProperty> getDataAuthTablePropertiesByTableName(String tableName){
        String tableNameLower = tableName.toLowerCase();
        return dataAuthTablePropertyMapper.selectList(new EntityWrapper<DataAuthTableProperty>().eq("table_name", tableNameLower).and()
                .eq("enabled", true));
    }

    /**
     * 新建 参数配置
     * @param dataAuthTableProperty
     * @return
     */
    public DataAuthTableProperty createDataAuthTableProperty(DataAuthTableProperty dataAuthTableProperty){
        String tableNameLower = dataAuthTableProperty.getTableName().toLowerCase();
        dataAuthTableProperty.setTableName(tableNameLower);
        if (dataAuthTablePropertyMapper.selectList(
                new EntityWrapper<DataAuthTableProperty>()
                .eq("table_name",dataAuthTableProperty.getTableName())
                .eq("data_type",dataAuthTableProperty.getDataType())
        ).size() > 0 ){
            throw new BizException(RespCode.AUTH_DATA_AUTH_TABLE_PROPERTY_DATA_TYPE_EXISTS);
        }
        if (dataAuthTableProperty.getFilterMethod().equals("TABLE_COLUMN")){
            if (dataAuthTablePropertyMapper.selectList(
                    new EntityWrapper<DataAuthTableProperty>()
                    .eq("table_name",dataAuthTableProperty.getTableName())
                    .eq("column_name",dataAuthTableProperty.getColumnName())
            ).size() > 0 ){
                throw new BizException(RespCode.AUTH_DATA_AUTH_TABLE_PROPERTY_COLUMN_NAME_EXISTS);
            }
        }
        dataAuthTablePropertyMapper.insert(dataAuthTableProperty);
        return dataAuthTableProperty;
    }

    /**
     * 编辑 参数配置
     * @param dataAuthTableProperty
     * @return
     */
    public DataAuthTableProperty updateDataAuthTableProperty(DataAuthTableProperty dataAuthTableProperty){
        String tableNameLower = dataAuthTableProperty.getTableName().toLowerCase();
        dataAuthTableProperty.setTableName(tableNameLower);
        DataAuthTableProperty oldDataAuthTableProperty = dataAuthTablePropertyMapper.selectById(dataAuthTableProperty.getId());
//        if (!oldDataAuthTableProperty.getTableName().equals(dataAuthTableProperty.getTableName()) &&
//                !oldDataAuthTableProperty.getDataType().equals(dataAuthTableProperty.getDataType())) {
//            if (dataAuthTablePropertyMapper.selectList(
//                    new EntityWrapper<DataAuthTableProperty>()
//                            .eq("table_name", dataAuthTableProperty.getTableName())
//                            .eq("data_type", dataAuthTableProperty.getDataType())
//            ).size() > 0) {
//                throw new BizException(RespCode.AUTH_DATA_AUTH_TABLE_PROPERTY_DATA_TYPE_EXISTS);
//            }
//        }
        if (dataAuthTableProperty.getFilterMethod().equals("TABLE_COLUMN")){
            if (!oldDataAuthTableProperty.getColumnName().equals(dataAuthTableProperty.getColumnName())) {
                if (dataAuthTablePropertyMapper.selectList(
                        new EntityWrapper<DataAuthTableProperty>()
                                .eq("table_name", dataAuthTableProperty.getTableName())
                                .eq("column_name", dataAuthTableProperty.getColumnName())
                ).size() > 0) {
                    throw new BizException(RespCode.AUTH_DATA_AUTH_TABLE_PROPERTY_COLUMN_NAME_EXISTS);
                }
            }
        }
        dataAuthTablePropertyMapper.updateById(dataAuthTableProperty);
        return dataAuthTableProperty;
    }

    /**
     * 根据条件 分页查询参数配置
     * @param tableName
     * @param dataType
     * @param filterMethod
     * @param columnName
     * @param page
     * @return
     */
    public List<DataAuthTableProperty> getDataAuthTablePropertyByCond(String tableName, String dataType, String filterMethod, String columnName, Page page) {
        if (!StringUtils.isEmpty(tableName)) {
            tableName = tableName.toLowerCase();
        }
        List<DataAuthTableProperty> list = new ArrayList<>();
        list = dataAuthTablePropertyMapper.selectPage(page,
                    new EntityWrapper<DataAuthTableProperty>()
                    .like(tableName != null,"table_name",tableName)
                    .eq(dataType != null,"data_type",dataType)
                    .eq(filterMethod != null,"filter_method",filterMethod)
                    .like(columnName != null,"column_name",columnName)
                    .orderBy("table_name",true)
                );
        List<SysCodeValueCO> dataTypeList = hcfOrganizationInterface.listAllSysCodeValueByCode("3101");
        List<SysCodeValueCO> filterMethodList = hcfOrganizationInterface.listAllSysCodeValueByCode("3104");
        list.stream().forEach(dataAuthTableProperty -> {
            dataTypeList.stream().forEach(customEnumerationItemDTO -> {
                if (dataAuthTableProperty.getDataType().equals(customEnumerationItemDTO.getValue())){
                    dataAuthTableProperty.setDataTypeName(customEnumerationItemDTO.getName());
                }
            });
            filterMethodList.stream().forEach(customEnumerationItemDTO -> {
                if (dataAuthTableProperty.getFilterMethod().equals(customEnumerationItemDTO.getValue())){
                    dataAuthTableProperty.setFilterMethodName(customEnumerationItemDTO.getName());
                }
            });
        });
        return list;
    }
}

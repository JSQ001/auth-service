package com.hand.hcf.app.expense.type.web.mapper;


import com.hand.hcf.app.expense.type.domain.enums.FieldDataColumn;
import com.hand.hcf.app.expense.type.domain.enums.FieldDataTypeEnum;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by lichao on 18/5/15.
 */
public class FieldMappedColumn {
    /**
     * 维护当前映射项的指针
     * 有效指针从0开始
     */
    private Map<FieldDataTypeEnum,Integer> indexMap = new HashedMap();
    private Map<FieldDataTypeEnum,List<FieldDataColumn>> fieldDataColumnMap = FieldDataColumn.getFieldDataColumnMap();

    /**
     * 构造方法
     * @param usedFieldDataColumns 已经使用过的字段映射
     */
    public FieldMappedColumn(List<FieldDataColumn> usedFieldDataColumns){
        super();
        //初始化映射指针
        for (FieldDataTypeEnum fieldDataType : FieldDataTypeEnum.values()) {
            indexMap.put(fieldDataType,-1);
        }
        if(!CollectionUtils.isEmpty(usedFieldDataColumns)){
            for (Map.Entry<FieldDataTypeEnum, List<FieldDataColumn>> fieldDataTypeListEntry : fieldDataColumnMap.entrySet()) {
                FieldDataTypeEnum fieldDataType = fieldDataTypeListEntry.getKey();
                List<FieldDataColumn> invoiceDataColumns = fieldDataTypeListEntry.getValue();
                int tmpIndex = -1;
                for (FieldDataColumn usedFieldDataColumn : usedFieldDataColumns) {
                    if(invoiceDataColumns.contains(usedFieldDataColumn)){
                        int index = invoiceDataColumns.indexOf(usedFieldDataColumn);
                        FieldDataColumn tmp = invoiceDataColumns.get(index);
                        //先删除
                        invoiceDataColumns.remove(tmp);
                        //再置顶
                        invoiceDataColumns.add(0,tmp);
                        //index ++
                        tmpIndex ++;
                    }
                }
                indexMap.put(fieldDataType,tmpIndex);
            }
        }
    }
    /**
     * 获取字段映射列id
     * @param fieldDataType
     * @return
     */
    public Integer getNextColumnId(FieldDataTypeEnum fieldDataType){
        Integer mappedColumnId = -1;
        if(indexMap.containsKey(fieldDataType)){
            int index = indexMap.get(fieldDataType);
            index ++;
            indexMap.put(fieldDataType,index);
            List<FieldDataColumn> invoiceDataColumns = fieldDataColumnMap.get(fieldDataType);
            //最大index 比 size 小1
            if(index + 1 >= invoiceDataColumns.size()) {
                throw new ValidationException(new ValidationError(fieldDataType.getKey(),"自定义字段映射列错误"));
            }
            mappedColumnId = invoiceDataColumns.get(index).getId();
        }
        return mappedColumnId;
    }
}

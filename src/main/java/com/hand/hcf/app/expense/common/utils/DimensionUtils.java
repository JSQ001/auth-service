package com.hand.hcf.app.expense.common.utils;


import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.mdata.client.dimension.DimensionItemCO;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.util.ReflectUtil;
import com.hand.hcf.core.util.TypeConversionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/27
 */
@Slf4j
public class DimensionUtils {

    private final static int DIMENSION_ID_SIZE = 20;


    /**
     * 根据对象获取维度Id的值集合
     * @param source
     * @param clazz
     * @return
     */
    public static List<Long> getDimensionId(Object source, Class<?> clazz){
        List<Long> dimensionIds = new ArrayList<>();
        for (int i = 1; i <= DIMENSION_ID_SIZE; i++) {
            Field field = ReflectionUtils.findField(clazz, "dimension" + i + "Id");
            if (field == null){
                log.error("属性{}不存在", "dimension" + i + "Id");
                throw new BizException(RespCode.EXPENSE_SET_DIMENSION_ID_ERROR);
            }
            field.setAccessible(true);
            try {
                Object dimensionId = field.get(source);
                if (dimensionId == null){
                    continue;
                }
                dimensionIds.add(TypeConversionUtils.parseLong(dimensionId));
            } catch (IllegalAccessException e1) {
                throw new BizException(RespCode.EXPENSE_SET_DIMENSION_ID_ERROR);
            }
        }
        return dimensionIds;
    }

    /**
     * 根据维度的值ID给对象的维度的Code 或者 Name赋值
     * @param fieldName Code 或者 Name
     * @param target 对象
     * @param clazz 对象类型
     * @param valueMap 维度值相关信息
     */
    public static  void  setDimensionCodeOrName(String fieldName, Object target, Class<?> clazz, Map<Long, String> valueMap){
        if (!CollectionUtils.isEmpty(valueMap)){
            for (int i = 1; i <= DIMENSION_ID_SIZE; i++) {
                Field idField = ReflectionUtils.findField(clazz, "dimension" + i + "Id");
                Field nameField = ReflectionUtils.findField(clazz, "dimension" + i + fieldName);
                if (idField == null || nameField == null) {
                    log.error("属性{}不存在,或者属性{}不存在", "dimension" + i + "Id", "dimension" + i + fieldName);
                    throw new BizException(RespCode.EXPENSE_SET_DIMENSION_ID_ERROR);
                }
                idField.setAccessible(true);
                nameField.setAccessible(true);
                try {
                    Object dimensionId = idField.get(target);
                    if (dimensionId == null) {
                        continue;
                    }
                    if (valueMap.containsKey(TypeConversionUtils.parseLong(dimensionId))) {
                        nameField.set(target, valueMap.get(TypeConversionUtils.parseLong(dimensionId)));
                    }
                } catch (IllegalAccessException e1) {
                    throw new BizException(RespCode.EXPENSE_SET_DIMENSION_ID_ERROR);
                }
            }
        }
    }

    /**
     * 设置维值信息
     * @param source               数据来源(当copyIdFlag为true时使用)
     * @param target               目标数据
     * @param organizationService
     * @param copyIdFlag           是否拷贝维值ID
     * @param setCodeFlag          是否设置维值代码
     * @param setNameFlag          是否设置维值名称
     */
    public static void setDimensionMessage(Object source,
                                        Object target,
                                        OrganizationService organizationService,
                                        boolean copyIdFlag,
                                        boolean setCodeFlag,
                                        boolean setNameFlag){
        for (int i = 1; i <= DIMENSION_ID_SIZE; i++) {
            String idFieldName = "dimension" + i + "Id";
            String codeFieldName = "dimension" + i + "Code";
            String nameFieldName = "dimension" + i + "Name";
            Object fieldValue = null;
            if(copyIdFlag){
                fieldValue = ReflectUtil.executeFieldGetter(source, idFieldName);
                if(fieldValue != null){
                    ReflectUtil.executeFieldSetter(target,idFieldName,fieldValue,false,null,null);
                }
            }
            if(!copyIdFlag){
                fieldValue = ReflectUtil.executeFieldGetter(target, idFieldName);
            }
            if(fieldValue != null){
                DimensionItemCO dimensionItemCO = organizationService.getDimensionItemById(TypeConversionUtils.parseLong(fieldValue));
                if (dimensionItemCO != null) {
                    if(setCodeFlag){
                        ReflectUtil.executeFieldSetter(target, codeFieldName, dimensionItemCO.getDimensionItemName(), false, null, null);
                    }
                    if(setNameFlag){
                        ReflectUtil.executeFieldSetter(target, nameFieldName, dimensionItemCO.getDimensionItemName(), false, null, null);
                    }
                }
            }
        }
    }

    /**
     * 设置维值信息
     * 考虑到后续添加缓存，所以采用单个查询的方法
     * @param target
     * @param organizationService
     */
    public static void setDimensionName(Object target, OrganizationService organizationService){
        setDimensionMessage(null,target,organizationService,false,false,true);
    }

    /**
     * 根据维度信息集合给对象的维度Id赋值
     * @param dimensionList
     * @param target
     * @param clazz
     */
    public static void setDimensionId(List<ExpenseDimension> dimensionList, Object target,  Class<?> clazz, boolean objectIsNew) {
        if (!CollectionUtils.isEmpty(dimensionList)) {
            if (!objectIsNew) {
                for (int i = 1; i <= DIMENSION_ID_SIZE; i++) {
                    Field field = ReflectionUtils.findField(clazz, "dimension" + i + "Id");
                    if (field == null) {
                        log.error("属性{}不存在", "dimension" + i + "Id");
                        throw new BizException(RespCode.EXPENSE_SET_DIMENSION_ID_ERROR);
                    }
                    field.setAccessible(true);
                    try {
                        field.set(target, null);
                    } catch (IllegalAccessException e1) {
                        throw new BizException(RespCode.EXPENSE_SET_DIMENSION_ID_ERROR);
                    }
                }
            }
            dimensionList.forEach(e -> {
                Field field = ReflectionUtils.findField(clazz, e.getDimensionField());
                if (field == null) {
                    log.error("属性{}不存在", e.getDimensionField());
                    throw new BizException(RespCode.EXPENSE_DIMENSIONS_IS_NULL);
                }
                field.setAccessible(true);
                try {
                    field.set(target, e.getValue());
                } catch (IllegalAccessException e1) {
                    throw new BizException(RespCode.EXPENSE_SET_DIMENSION_ID_ERROR);
                }
            });
        }
    }

    public static <S, D> void setDimensionIdByObject(Object source, Object target, Class<S> sClass, Class<D> dClass){
        for (int i = 1; i <= DIMENSION_ID_SIZE; i++) {
            Field sfield = ReflectionUtils.findField(sClass, "dimension" + i + "Id");
            Field dfield = ReflectionUtils.findField(dClass, "dimension" + i + "Id");
            if (sfield == null || dfield == null){
                log.error("属性{}不存在", "dimension" + i + "Id");
                throw new BizException(RespCode.EXPENSE_SET_DIMENSION_ID_ERROR);
            }
            sfield.setAccessible(true);
            dfield.setAccessible(true);
            try {
                dfield.set(target, sfield.get(source));
            } catch (IllegalAccessException e1) {
                throw new BizException(RespCode.EXPENSE_SET_DIMENSION_ID_ERROR);
            }
        }
    }

    public  static void setNullToDimensionId(int start, Object target, Class<?> clazz){
        for (int i = start + 1; i <= DIMENSION_ID_SIZE; i++) {
            Field field = ReflectionUtils.findField(clazz, "dimension" + i + "Id");
            if (field == null){
                log.error("属性{}不存在", "dimension" + i + "Id");
                continue;
            }
            field.setAccessible(true);
            try {
                field.set(target, null);
            } catch (IllegalAccessException e1) {
                throw new BizException(RespCode.EXPENSE_SET_DIMENSION_ID_ERROR);
            }
        }
    }
}

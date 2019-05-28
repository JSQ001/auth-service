package com.hand.hcf.app.core.service;

import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;
import com.hand.hcf.app.core.annotation.UniqueField;
import com.hand.hcf.app.core.domain.Domain;
import com.hand.hcf.app.core.domain.DomainI18n;
import com.hand.hcf.app.core.domain.DomainLogic;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


abstract public class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {
    private static final Logger log = LoggerFactory.getLogger(BaseService.class);
    @Autowired
    protected DataSource dataSource;


    public EntityWrapper<T> getWrapper(){
        return new EntityWrapper<>();
    }

    public EntityWrapper<T> getWrapper(T entity){
        return new EntityWrapper<>(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(T entity) {
        return checkUpdate(super.updateById(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAllColumnById(T entity) {
        setMetaWhenUpdateAllColumn(entity, null);
        return checkUpdate(super.updateAllColumnById(entity));

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(List<T> entityList) {
        try {
            return checkUpdate(super.updateBatchById(entityList));
        } catch (RuntimeException e) {
            throw (RuntimeException) ExceptionUtil.sqlExceptionTrans(dataSource, e);
        }


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAllColumnBatchById(List<T> entityList) {
        setMetaWhenUpdateAllColumnBatch(entityList);
        try {
            return checkUpdate(super.updateAllColumnBatchById(entityList));
        } catch (RuntimeException e) {
            throw (RuntimeException) ExceptionUtil.sqlExceptionTrans(dataSource, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(List<T> entityList) {
        try {
            return super.insertBatch(entityList);
        } catch (RuntimeException e) {
            throw (RuntimeException) ExceptionUtil.sqlExceptionTrans(dataSource, e);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(List<T> entityList, int batchSize) {
        try {
            return super.insertBatch(entityList, batchSize);
        } catch (RuntimeException e) {
            throw (RuntimeException) ExceptionUtil.sqlExceptionTrans(dataSource, e);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertOrUpdateBatch(List<T> entityList) {
        try {
            return super.insertOrUpdateBatch(entityList);
        } catch (RuntimeException e) {
            throw (RuntimeException) ExceptionUtil.sqlExceptionTrans(dataSource, e);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertOrUpdateBatch(List<T> entityList, int batchSize) {
        try {
            return super.insertOrUpdateBatch(entityList, batchSize);
        } catch (RuntimeException e) {
            throw (RuntimeException) ExceptionUtil.sqlExceptionTrans(dataSource, e);
        }

    }

    boolean checkUpdate(boolean res) {
        if (!res) {
            throw new BizException(RespCode.SYS_VERSION_NUMBER_CHANGED);
        }
        return res;
    }

    /**
     * 更新所有列时，为防止前端少传部分公用字段数据，在此查询一次，更新到实体中
     *
     * @param entity
     */
    public void setMetaWhenUpdateAllColumn(T entity, T source) {
        if (entity != null) {
            Class<?> entityClass = entity.getClass();
            if (Domain.class.isAssignableFrom(entityClass) || DomainI18n.class.isAssignableFrom(entityClass) || entity instanceof Domain || entity instanceof DomainI18n) {
                Reflector reflector = new Reflector(entityClass);
                // 暂时只需要设置这几个属性，最后更新人、日期，公用字段填充会自动更新，版本号用来控制版本，不应该在此设置值
                List<String> strings = Arrays.asList("createdDate", "createdBy", "deleted", "enabled");
                for (String fieldName : strings) {
                    if (reflector.hasGetter(fieldName)) {
                        if (ReflectUtil.executeFieldGetter(entity, fieldName) == null) {
                            if (source == null) {
                                TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
                                String keyColumn = tableInfo.getKeyColumn();
                                Serializable id = (Serializable) ReflectUtil.executeFieldGetter(entity, keyColumn);
                                source = super.selectById(id);
                            }
                            ReflectUtil.executeFieldSetter(entity, fieldName, ReflectUtil.executeFieldGetter(source, fieldName), false, "", null);
                        }
                    }
                }
            }
        }
    }

    public void setMetaWhenUpdateAllColumnBatch(List<T> entityList) {
        if (CollectionUtils.isNotEmpty(entityList)) {
            entityList.stream().forEach(entity -> setMetaWhenUpdateAllColumn(entity, null));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteById(Serializable id) {
        Class domainClass = getDomainClass();
        if (DomainI18n.class.isAssignableFrom(domainClass) || DomainLogic.class.isAssignableFrom(domainClass) ) {
            Field field = ReflectionUtils.findField(domainClass, "deleted");
            if (field == null) {
                return super.deleteById(id);
            }
            List<Field> fieldList = ReflectionUtil.getFieldList(domainClass);
            if (CollectionUtils.isEmpty(fieldList)){
                return super.deleteById(id);
            }
            List<Field> uniqueFieldList = fieldList.stream().filter(v -> {
                UniqueField uniqueField = v.getAnnotation(UniqueField.class);
                return uniqueField != null;
            }).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(uniqueFieldList)){
                return super.deleteById(id);
            }
            T entity = baseMapper.selectById(id);
            if (entity == null){
                return true;
            }
            setEntityDeletedField(uniqueFieldList, domainClass, entity);
            return this.updateById(entity);
        }else {
            return super.deleteById(id);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatchIds(Collection<? extends Serializable> idList) {
        Class domainClass = getDomainClass();
        if (DomainI18n.class.isAssignableFrom(domainClass) || DomainLogic.class.isAssignableFrom(domainClass) ) {
            Field field = ReflectionUtils.findField(domainClass, "deleted");
            if (field == null) {
                return super.deleteBatchIds(idList);
            }
            List<Field> fieldList = ReflectionUtil.getFieldList(domainClass);
            if (CollectionUtils.isEmpty(fieldList)){
                return super.deleteBatchIds(idList);
            }
            List<Field> uniqueFieldList = fieldList.stream().filter(v -> {
                UniqueField uniqueField = v.getAnnotation(UniqueField.class);
                return uniqueField != null;
            }).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(uniqueFieldList)){
                return super.deleteBatchIds(idList);
            }
            List<T> entityList = baseMapper.selectBatchIds(idList);
            return updateDeletedEntity(entityList, uniqueFieldList, domainClass);
        }else {
            return super.deleteBatchIds(idList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteByMap(Map<String, Object> columnMap) {
        if (MapUtils.isEmpty(columnMap)) {
            throw new MybatisPlusException("deleteByMap columnMap is empty.");
        }
        Class domainClass = getDomainClass();
        if (DomainI18n.class.isAssignableFrom(domainClass) || DomainLogic.class.isAssignableFrom(domainClass) ) {
            Field field = ReflectionUtils.findField(domainClass, "deleted");
            if (field == null) {
                return super.deleteByMap(columnMap);
            }
            List<Field> fieldList = ReflectionUtil.getFieldList(domainClass);
            if (CollectionUtils.isEmpty(fieldList)){
                return super.deleteByMap(columnMap);
            }
            List<Field> uniqueFieldList = fieldList.stream().filter(v -> {
                UniqueField uniqueField = v.getAnnotation(UniqueField.class);
                return uniqueField != null;
            }).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(uniqueFieldList)){
                return super.deleteByMap(columnMap);
            }
            List<T> entityList = baseMapper.selectByMap(columnMap);
            return updateDeletedEntity(entityList, uniqueFieldList, domainClass);
        }else {
            return super.deleteByMap(columnMap);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(Wrapper<T> wrapper) {
        Class domainClass = getDomainClass();
        if (DomainI18n.class.isAssignableFrom(domainClass) || DomainLogic.class.isAssignableFrom(domainClass) ) {
            Field field = ReflectionUtils.findField(domainClass, "deleted");
            if (field == null) {
                return super.delete(wrapper);
            }
            List<Field> fieldList = ReflectionUtil.getFieldList(domainClass);
            if (CollectionUtils.isEmpty(fieldList)){
                return super.delete(wrapper);
            }
            List<Field> uniqueFieldList = fieldList.stream().filter(v -> {
                UniqueField uniqueField = v.getAnnotation(UniqueField.class);
                return uniqueField != null;
            }).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(uniqueFieldList)){
                return super.delete(wrapper);
            }
            List<T> entityList = baseMapper.selectList(wrapper);
            return updateDeletedEntity(entityList, uniqueFieldList, domainClass);
        }else {
            return super.delete(wrapper);
        }
    }
    /**
     * 更新逻辑删除的数据
     *
     * @param entityList 需要跟新的数据
     * @param uniqueFieldList 需要更新的字段(唯一性字段属性)
     * @param domainClass class
     */
    private boolean updateDeletedEntity(List<T> entityList, List<Field> uniqueFieldList,Class domainClass){
        if (CollectionUtils.isEmpty(entityList)){
            return true;
        }
        entityList.forEach(entity -> setEntityDeletedField(uniqueFieldList, domainClass, entity));
        return this.updateBatchById(entityList);
    }

    private void setEntityDeletedField(List<Field> uniqueFieldList, Class domainClass, T entity){
        try {
            // 先设置deleted字段，如果 uniqueField不是String类型，抛出异常，但是delete已经设置为true
            Reflector reflector = new Reflector(domainClass);
            Invoker deleted = reflector.getSetInvoker("deleted");
            deleted.invoke(entity, new Object[]{Boolean.TRUE});
            String randomString = RandomStringUtils.randomNumeric(6);
            for (Field uniqueField : uniqueFieldList){
                Invoker getInvoker = reflector.getGetInvoker(uniqueField.getName());
                Object value = getInvoker.invoke(entity, new Object[]{});
                if (value != null) {
                    Invoker setInvoker = reflector.getSetInvoker(uniqueField.getName());
                    setInvoker.invoke(entity, new Object[]{TypeConversionUtils.parseString(value) + "_DELETED_" + randomString});
                }
            }
        } catch (IllegalAccessException  e1) {
            e1.printStackTrace();
            log.error("设置逻辑删除唯一性索引字段时发生异常：原因：{}", e1.getCause());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            log.error("设置逻辑删除唯一性索引字段时发生异常：原因：{}", e.getCause());
        }
    }
    /**
     * 获取当前对象domain类型
     */
    private Class getDomainClass(){
        Class clz = this.getClass();
        Type type = clz.getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType)type;
        return (Class)pt.getActualTypeArguments()[1];
    }
}

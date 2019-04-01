package com.hand.hcf.app.common.annotation;

import com.hand.hcf.app.common.enums.SourceTransactionTypeDataStructure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kai.zhang on 2017-12-26.
 * 核算接口数据结构
 * 若添加字段，需要在AccountingSourceTransactionTypeRegistrar类中做出相应调整
 * 若需要使用到集合或数组，必须要加上泛型
 * 关联关系 需要双向维护
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InterfaceDataStructure {

    int sequence();                                     //序号

    int genericIndex() default 0;                     //容器泛型默认下标

    SourceTransactionTypeDataStructure type();          //类型 一般为单据头、行等 (每个类中的类型名称必须唯一)

    String msgCode() default "";                      //描述-多语言代码(默认为类名 + 属性名)

    String message() default "";                      //默认描述信息

    String[] args() default {};                       //替换符的替换信息

    SourceTransactionTypeDataStructure[] relateDataSource() default {};           //关联的数据结构

    String[] relateField() default {};                //此数据结构的关联字段，关联对方数据的ID， 与关联的数据结构一一对应
}

package com.hand.hcf.app.common.annotation;

import com.hand.hcf.app.common.enums.SceneElementFieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kai.zhang on 2017-12-25.
 * 核算接口属性设置
 * 若添加字段，需要在AccountingSourceTransactionTypeRegistrar类中做出相应调整
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InterfaceFieldAttribute {

    int sequence();                                     //序号

    boolean display() default true;                  //前台界面是否展示

    boolean dimension() default false;               //是否为维度 (维度字段特殊处理，页面展示信息需根据定义信息获取)

    String msgCode() default "";                      //描述-多语言代码(默认为类名 + 属性名)

    String message() default "";                      //默认描述信息

    String[] args() default {};                       //替换符的替换信息

    SceneElementFieldType[] elementFiled() default {}; //接口对应默认核算要素
}

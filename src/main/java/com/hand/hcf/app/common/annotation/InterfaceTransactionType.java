package com.hand.hcf.app.common.annotation;

import com.hand.hcf.app.common.enums.SourceTransactionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kai.zhang on 2017-12-26.
 * 来源事务类 配置
 * 若添加字段，需要在AccountingSourceTransactionTypeRegistrar类中做出相应调整
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InterfaceTransactionType {

    SourceTransactionType value();
}

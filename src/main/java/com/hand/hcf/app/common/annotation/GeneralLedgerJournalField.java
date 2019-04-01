package com.hand.hcf.app.common.annotation;

import com.hand.hcf.app.common.enums.GeneralLedgerJournalFieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018-01-04
 * remark: 核算分录段相关配置
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneralLedgerJournalField {

    int sequence();                                     //序号

    GeneralLedgerJournalFieldType type();               //字段类型 SEGMENT为科目段，ACCOUNT为核算段，ACCOUNT_ELEMENT为核算要素

    String displayValue();                              //界面显示的值

    boolean display() default true;                  //前台界面是否展示

    String msgCode() default "";                      //描述-多语言代码(默认为类名 + 属性名)

    String message() default "";                      //默认描述信息

    String[] args() default {};                       //替换符的替换信息

    String glSegmentField() default "";               //科目段对应的总账科目段字段名，若为空，则默认为segment + 科目段字段名中的数字

    boolean dimension() default false;              //是否为维度
}

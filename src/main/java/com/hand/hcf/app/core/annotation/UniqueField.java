package com.hand.hcf.app.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *  逻辑删除需要修改的唯一性属性
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/3/2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UniqueField {
}

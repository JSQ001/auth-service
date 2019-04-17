package com.hand.hcf.app.core.redisLock.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LockedObject {

    @AliasFor("lockKeyField")
    String[] value() default {};

    //为了区别唯一性所指定的对象的field,需确保对象有其get方法
    @AliasFor("value")
    String[] lockKeyField() default {};
}

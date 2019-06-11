package com.hand.hcf.app.core.annotation;


import java.lang.annotation.*;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelDomainField {


    String dataFormat() default "@";
    /**
     * 对齐方式
     */
    String align() default "left";

    /**
     * 宽度
     */
    int width() default 80;

    String title() default "";

}

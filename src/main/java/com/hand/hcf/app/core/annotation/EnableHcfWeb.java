

package com.hand.hcf.app.core.annotation;

import com.hand.hcf.app.core.config.RestControllerAdvice;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RestControllerAdvice.class})
public @interface EnableHcfWeb {
}

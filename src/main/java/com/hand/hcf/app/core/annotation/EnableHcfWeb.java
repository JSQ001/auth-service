

package com.hand.hcf.app.core.annotation;

import com.hand.hcf.app.core.config.RestControllerAdvice;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RestControllerAdvice.class})
public @interface EnableHcfWeb {
}

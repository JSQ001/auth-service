package com.hand.hcf.app.core.annotation;

import com.hand.hcf.app.core.service.BaseI18nService;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(BaseI18nService.class)
public @interface EnableBaseI18nService {

}

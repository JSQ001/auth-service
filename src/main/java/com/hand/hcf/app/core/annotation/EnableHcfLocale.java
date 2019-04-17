package com.hand.hcf.app.core.annotation;

import com.hand.hcf.app.core.locale.HcfLocaleConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(HcfLocaleConfiguration.class)
public @interface EnableHcfLocale {
}

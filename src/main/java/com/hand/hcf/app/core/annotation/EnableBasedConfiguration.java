

package com.hand.hcf.app.core.annotation;

import com.hand.hcf.app.core.component.ApplicationContextProvider;
import com.hand.hcf.app.core.config.DateTimeFormatConfiguration;
import com.hand.hcf.app.core.config.MessageConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DateTimeFormatConfiguration.class, MessageConfiguration.class, ApplicationContextProvider.class})
public @interface EnableBasedConfiguration {
}

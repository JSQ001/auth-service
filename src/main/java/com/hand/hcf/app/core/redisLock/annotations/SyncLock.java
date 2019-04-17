package com.hand.hcf.app.core.redisLock.annotations;


import com.hand.hcf.app.core.redisLock.enums.CredentialTypeEnum;
import com.hand.hcf.app.core.util.RespCode;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SyncLock {

    // redis锁key的前缀,防止key值冲突
    String lockPrefix() default "DEFAULT";

    // redis锁key的凭证标记
    CredentialTypeEnum credential() default CredentialTypeEnum.NULL;

    // 尝试获取锁时间(缺省值500ms),此时间根据业务需要而定,就目前大多数使用场景需要尽量小的尝试获取锁的时间,单位毫秒
    int timeOut() default 500;

    // 锁失效时间,单位毫秒
    int expireTime() default 60000;

    // 错误提示信息,缺省值请求速度过快
    String errorMessage() default RespCode.SYS_REQUEST_SPEED_IS_TOO_FAST;

    // 是否等待资源锁(该功能类似于自旋锁，比较消耗资源，酌情使用)
    boolean waiting() default false;

    // 尝试获取锁期间，获取锁间隔时间
    int intervalTime() default 100;
}

package com.hand.hcf.app.workflow.util;

/**
 * 检查参数工具类
 * @author mh.z
 * @date 2019/04/26
 */
public class CheckUtil {

    /**
     * 参数value不为null则返回value，否则抛出异常
     * @version 1.0
     * @author mh.z
     * @date 2019/04/26
     *
     * @param value
     * @param message
     * @param <T>
     * @return
     */
    public static <T> T notNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }

        return value;
    }

}

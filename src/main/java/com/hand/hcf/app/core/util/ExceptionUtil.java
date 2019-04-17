package com.hand.hcf.app.core.util;

import org.apache.ibatis.exceptions.PersistenceException;
import org.mybatis.spring.MyBatisExceptionTranslator;

import javax.sql.DataSource;

/**
 * Created by polus
 * 2018/12/27 上午8:49
 */
public class ExceptionUtil {

    public static Throwable sqlExceptionTrans(DataSource dataSource, RuntimeException ex) {
        if (ex instanceof PersistenceException) {
            return (new MyBatisExceptionTranslator(dataSource, true)).translateExceptionIfPossible(ex);
        }
        return ex;
    }
}

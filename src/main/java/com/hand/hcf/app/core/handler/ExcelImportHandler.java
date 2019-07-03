package com.hand.hcf.app.core.handler;

import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.RespCode;

import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/11/19 09:33
 * @remark 导入相关处理
 */
public interface ExcelImportHandler<T> {

    // 逐条校验
    int ONE_RECORD = 1;
    // 批量校验
    int BATCHES = 2;

    /**
     * 获取校验频率
     * @return Integer
     * 1 -> 逐条校验； 2 -> 批量校验；不支持一次性全部校验，若数据量较小，可适当调整checkBatchAndPersistenceSize
     */
    default int getCheckFrequency(){
        return BATCHES;
    }

    /**
     * 批量校验容量,以及批量持久化容量
     * @return
     */
    default int checkBatchAndPersistenceSize(){
        //同步产品代码---20190619 by cx --这个类只修改了此处
        return 3000;
    }

    /**
     * 获取序号列名称
     * @return
     */
    default String getRowNumberColumnName(){
        return "rowNumber";
    }

    /**
     * 由于开启了多线程，线程变量需要在子线程中设置，可通过此方法设置
     */
    default void setThreadLocal(){

    }

    /**
     * 清除历史数据
     */
    void clearHistoryData();

    /**
     * 获取导入实体class
     * @return
     */
    Class<T> getEntityClass();

    /**
     * 获取导入实体实例
     * @return
     */
    default T getEntityInstance(){
        try {
            return getEntityClass().newInstance();
        } catch (InstantiationException e) {
            throw new BizException(RespCode.SYS_EXCEL_IMPORT_GET_ENTITY_INSTANCE_FAILED);
        } catch (IllegalAccessException e) {
            throw new BizException(RespCode.SYS_EXCEL_IMPORT_GET_ENTITY_INSTANCE_FAILED);
        }
    }

    /**
     * 临时表数据持久化
     * @param list
     * @return
     */
    List<T> persistence(List<T> list);

    /**
     * 校验方法
     * 没有任何的返回值，如果需要终止程序，可直接抛出异常
     * 只能做单行校验，若需多行关联校验，需要在持久化之后，单独写校验方法
     * @param list
     */
    void check(List<T> list);

}

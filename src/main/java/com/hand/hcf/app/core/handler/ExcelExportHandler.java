package com.hand.hcf.app.core.handler;

import com.baomidou.mybatisplus.plugins.Page;

import java.util.List;

/**
 * <p>
 *     多线程导出excel 需要实现的接口
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/21
 */
public interface ExcelExportHandler<S,D> {

    /**
     * 总记录数
     * @return
     */
    int getTotal();

    /**
     * 总共多少调
     * @return
     */
    default int getPageSize(){ return 10000; }
    /**
     * 通过分页查询获取数据
     * @param page
     * @return
     */
    List<S> queryDataByPage(Page page);

    /**
     * 转DTO
     * @param t
     * @return
     */
    D toDTO(S t);

    /**
     * 获取导出的实体
     * @return
     */
    Class<D> getEntityClass();
}

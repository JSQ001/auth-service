package com.hand.hcf.app.payment.utils;

import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by lichao on 2017/6/29.
 */
public class MyBatisPageUtil {
    /**
     * pageable 转 mybatis plus page 参数
     * @param pageable
     * @return
     */
    public static Page getPage(Pageable pageable){
        return  new com.baomidou.mybatisplus.plugins.Page(pageable.getPageNumber()+1,pageable.getPageSize());
    }
}

package com.hand.hcf.app.base.lov.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

/**
 * Created by weishan on 2019/3/5.
 * lov列定义
 */
@Data
@TableName("sys_lov_cols")
public class LovCols extends Domain {

    private Long lov_id;


    private String title;
    private String field;

}

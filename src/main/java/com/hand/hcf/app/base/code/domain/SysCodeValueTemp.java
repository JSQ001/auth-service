package com.hand.hcf.app.base.code.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;


/**
 * Created by fanfuqiang 2018/11/26
 */
@Data
@TableName("sys_code_value_temp")
public class SysCodeValueTemp extends Domain {

    private String name;

    private String value;

    private Long codeId;   // 值列表Id


    private String remark;

    private String enabledStr;

    private String batchNumber;
    private String rowNumber;
    private String errorDetail;
    private Boolean errorFlag;
}

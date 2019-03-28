package com.hand.hcf.app.expense.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *     维度列，前端根据此对象生成动态列
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/26
 */
@Data
public class DimensionDTO implements Serializable {

    private String title;
    private String dataIndex;
}

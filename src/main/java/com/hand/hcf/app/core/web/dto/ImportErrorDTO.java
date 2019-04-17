package com.hand.hcf.app.core.web.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *     excel导入的错误信息明细
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/10/19
 */
@Data
public class ImportErrorDTO implements Serializable {

    private Integer index;
    private String error;

}

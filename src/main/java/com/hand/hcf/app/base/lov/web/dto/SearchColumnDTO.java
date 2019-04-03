package com.hand.hcf.app.base.lov.web.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/3/23
 */
@Data
public class SearchColumnDTO implements Serializable {
    private String type;
    private String id;
    private String label;
}

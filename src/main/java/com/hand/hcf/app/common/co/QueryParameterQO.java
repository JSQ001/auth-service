package com.hand.hcf.app.common.co;

import com.hand.hcf.app.common.enums.RangeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *  查询条件封装类
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/13
 */
@Data
public class QueryParameterQO implements Serializable {
    /**
     * 账套Id
     */

    private Long setOfBooksId;

    /**
     * 范围
     */
    private RangeEnum range;

    /**
     * 代码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     *
     */
    private List<Long> existsIds;

    private String codeFrom;
    private String codeTo;
    private String nameFrom;
    private String nameTo;
}

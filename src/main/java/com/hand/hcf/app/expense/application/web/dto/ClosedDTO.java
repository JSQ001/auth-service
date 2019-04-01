package com.hand.hcf.app.expense.application.web.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/2/27
 */
@Data
public class ClosedDTO implements Serializable {
    /**
     * 单据头Id
     */
    private List<Long> headerIds;
    /**
     * 原因/意见
     */
    private String messages;
}

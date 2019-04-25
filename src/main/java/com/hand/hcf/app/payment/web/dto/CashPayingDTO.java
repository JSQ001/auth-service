package com.hand.hcf.app.payment.web.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by 刘亮 on 2017/10/16.
 */
@Data
public class CashPayingDTO {
    private List<Long> detailIds;
    private List<Integer> versionNumbers;
}

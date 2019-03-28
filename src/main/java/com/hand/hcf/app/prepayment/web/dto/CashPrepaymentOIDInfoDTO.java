package com.hand.hcf.app.prepayment.web.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Created by 刘亮 on 2018/1/15.
 */
@Data
public class CashPrepaymentOIDInfoDTO {
    private List<UUID> oids;
}

package com.hand.hcf.app.expense.adjust.web.dto;

import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustHeader;
import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/5
 */
@Data
public class ExpenseAdjustHeaderWebDTO extends ExpenseAdjustHeader {

    private List<String> attachmentOidList;

    private List<AttachmentCO> attachments;

    private String companyName;

    private String unitName;

    private String employeeName;

    private String createdByName;

    private String typeName;

    private Integer entityType;
}

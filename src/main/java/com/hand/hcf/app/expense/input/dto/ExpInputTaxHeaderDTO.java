package com.hand.hcf.app.expense.input.dto;

import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.expense.input.domain.ExpInputTaxHeader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description
 * @Version: 1.0
 * @author: ShilinMao
 * @date: 2019/3/1 14:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpInputTaxHeaderDTO extends ExpInputTaxHeader {


    private String fullName;

    private String companyName;

    private String departmentName;

    private String transferTypeName;

    private String useTypeName;

    private List<String> attachmentOidList;

    private List<AttachmentCO> attachments;

    private  String applicantName;
    private  String createdName;

}

package com.hand.hcf.app.mdata.company.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * comment
 * Created by fanfuqiang 2018/11/20
 */
@Data
@Builder
public class VerifyCodeDTO {

    private UUID attachmentOid;
    //base64
    private String image;

}

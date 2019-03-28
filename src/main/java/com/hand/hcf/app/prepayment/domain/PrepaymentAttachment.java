package com.hand.hcf.app.prepayment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * Created by 刘亮 on 2017/12/18.
 */
@Data
@TableName("prepayment_attachment")
@Accessors(chain = true)
public class PrepaymentAttachment extends Domain {
    @TableField("attachment_oid")
    private String attachmentOID;
    @TableField("file_name")
    private  String fileName;
    @TableField("file_url")
    private String fileUrl;
    private String link;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long size;
}

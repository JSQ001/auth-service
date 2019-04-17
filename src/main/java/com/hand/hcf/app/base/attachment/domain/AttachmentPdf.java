package com.hand.hcf.app.base.attachment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Wkit on 2017/6/7.
 */
@Data
@TableName( "sys_attachment_pdf")
public class AttachmentPdf extends Domain implements Serializable {

    @TableField("entity_oid")
    @NotNull
    private String entityOid;

    @TableField( "entity_type")
    private int entityType;

    @NotNull
    @TableField( "attachment_oid")
    private String attachmentOid;


}

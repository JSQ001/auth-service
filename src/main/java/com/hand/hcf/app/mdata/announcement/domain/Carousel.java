package com.hand.hcf.app.mdata.announcement.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.core.domain.DomainEnable;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

/*import com.hand.hcf.app.client.attachment.AttachmentCO;*/

@TableName("sys_carousel")
@Data
public class Carousel extends DomainEnable {

    @TableField("carousel_oid")
    private UUID carouselOid;

    @TableField("company_oid")
    private UUID companyOid;

    /**
     * 关联附件的OID
     */
    @TableField(value = "attachment_oid", strategy = FieldStrategy.IGNORED)
    private String attachmentOid;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("preferred_date")
    private ZonedDateTime preferredDate;

    /**
     * 是否外部链接
     */
    private Boolean outLinkFlag;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("source")
    private Long source;

    @TableField(exist = false)
    private AttachmentCO attachment;
}

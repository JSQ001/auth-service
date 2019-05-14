package com.hand.hcf.app.base.attachment.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

/**
 * A Attachment.
 * @author polus
 */
@Data
@TableName("sys_attachment")
public class Attachment extends Domain implements Serializable {

    @NotNull
    @TableField("attachment_oid")
    private UUID attachmentOid;

    @NotEmpty
    private String name;

    @TableField("media_type_id")
    @NotNull
    private Integer mediaTypeID;

    private String path;

    private String thumbnailPath;

    private String iconPath;

    @TableField("size_")
    private Long sizes;
    @TableField("is_public")
    private Boolean publicFlag = false;
    @TableField("is_legacy")
    private Boolean legacyFlag = false;

    /**
     * 财务核对标记
     */
    private Boolean checked;

    /**
     * ftp 上绝对路径 上传方式不是FTP 请设置为空
     */
    private String absolutePath;
    /**
     * 业务类型主键
     */
    private String pkValue;
    /**
     * 业务类型名称
     */
    private String pkName;

}

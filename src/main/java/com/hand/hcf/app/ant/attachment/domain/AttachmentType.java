package com.hand.hcf.app.ant.attachment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description:  单据类型附件类型Domain
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @date 2019/5/16 14:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_attachment_type")
public class AttachmentType extends Domain implements Serializable {

    @TableField("id")
    private Long id;

    /**
     * 附件类型名称
     */
    @NotNull
    @TableField("attachment_type_name")
    private String attachmentTypeName;


    /**
     * 是否必须上传
     */
    @TableField("uploaded")
    private Boolean uploaded;

    /**
     * 是否显示在表单
     */
    @TableField("showed")
    private Boolean showed;

    /**
     * 单据类型id
     */
    @NotNull
    @TableField("exp_report_type_id")
    private Long expReportTypeId;

}

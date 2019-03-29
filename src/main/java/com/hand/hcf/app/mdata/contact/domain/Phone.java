
package com.hand.hcf.app.mdata.contact.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import javax.persistence.Transient;

/**
 * Created by mingming on 16/5/31.
 */
@Data
@TableName("sys_phone")
public class Phone extends Domain {
    private static final long serialVersionUID = 8994867643271195067L;

    @TableField("contact_id")

    @Transient
    private Long contactId;

    private String phoneNumber;

    @TableField("type")
    @Transient
    private Integer typeNumber;

    @TableField(value = "is_primary")
    private Boolean primaryFlag;

    @TableField("country_code")
    private String countryCode;
    @TableField("didi_enable")
    private Boolean didiEnable;

}

package com.hand.hcf.app.mdata.announcement.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import lombok.Data;

import java.util.UUID;

/**
 * @Author: zhaozhu
 * @Description: 公告图片实体；分 内置图片和模板图片
 * @Date: 2019/01/04 11:46
 */
@TableName("sys_carousel_template")
@Data
public class CarouselTemplate extends DomainI18nEnable {

    @TableField("attachment_oid")
    private UUID attachmentOid;

    @I18nField
    @TableField("template_name")
    private String templateName;

    @TableField("template_url")
    private String templateUrl;

    @TableField("type")
    private String type;
}
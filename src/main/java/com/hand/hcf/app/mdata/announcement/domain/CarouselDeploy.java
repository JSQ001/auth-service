package com.hand.hcf.app.mdata.announcement.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainEnable;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by Ray Ma on 2018/3/19.
 */
@TableName("sys_carousel_deploy")
@Data
public class CarouselDeploy extends DomainEnable {

    @NotNull
    @TableField("carousel_id")
    private Long carouselId;

    @NotNull
    @TableField("company_id")
    private Long companyId;

    @TableField("deploy_flag")
    private Boolean deployFlag;
}

package com.hand.hcf.app.mdata.area.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by chenliangqin on 16/11/26.
 */
@Data
@TableName( "sys_area_itinerary")
public class AreaItinerary extends Domain {

    private Long id;

    @TableField("application_oid")

    private UUID applicationOid;

    @TableField("itinerary_oid")

    private UUID itineraryOid;

    @TableField("area_code")
    private String areaCode;

    @TableField( "area_name")
    private String areaName;

    @TableField("amount")
    private BigDecimal amount;
}

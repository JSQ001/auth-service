package com.hand.hcf.app.ant.mdata.location.dto;

import com.hand.hcf.app.ant.mdata.location.domain.PayeeHeader;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zihao.yang
 * @create 2019-6-13 10:08:29
 * @remark
 */
@ApiModel(description = "收款方头信息")
@Data
public class PayeeHeaderDTO extends PayeeHeader {

    /**
     * 收款方详细信息
     */
    @ApiModelProperty(value = "收款方详细信息")
    private List<PayeeSettingLineDTO> payeeLineDTOList;

    /**
     * 收款方国家名称
     */
    @ApiModelProperty(value = "收款方国家名称")
    private String payeeCountryName;

    /**
     * 收款方城市名称
     */
    @ApiModelProperty(value = "收款方城市名称")
    private String payeeCityName;

    /**
     * 付款方国家名称
     */
    @ApiModelProperty(value = "收款方国家名称")
    private String payerCountryName;

    /**
     * 付款方城市名称
     */
    @ApiModelProperty(value = "收款方城市名称")
    private String payerCityName;

}

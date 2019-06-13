package com.hand.hcf.app.ant.mdata.location.dto;

import com.hand.hcf.app.ant.mdata.location.domain.PayeeHeader;
import com.hand.hcf.app.ant.mdata.location.domain.PayeeLine;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zihao.yang
 * @create 2019-6-13 10:08:29
 * @remark
 */
@ApiModel(description = "收款方行信息")
@Data
public class PayeeLineDTO extends PayeeLine {

}

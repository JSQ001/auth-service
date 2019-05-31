package com.hand.hcf.app.ant.taxreimburse.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpBankFlow;
import lombok.Data;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description:
 * @date 2019/5/29 14:33
 */
@Data
public class ExpBankFlowDTO extends ExpBankFlow {
    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 税种名称
     */
    private String taxCategoryName;

    /**
     * 会计科目名称
     */
    private String accountTitle;

}

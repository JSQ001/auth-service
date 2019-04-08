package com.hand.hcf.app.workflow.enums;

/**
 * @description: sys_wfl_document_ref.document_category (单据大类)
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/3/12
 */
public enum DocumentCategoryEnum {

     REPORT_PUBLIC(801001 ,"对公报账单")
    ,BUDGET_JOURNAL(801002,"预算日记账")
    ,PRE_PAYMENT(801003,"预付款单")
    ,CONTRACT(801004,"合同")
    ,PAYMENT_REQUESTION(801005,"付款申请单")
    ,EXPENSE_ADJUST(801006,"费用调整单")
    ,EXPENSE_RECOIL(801007,"费用反冲")
    ,ACCOUNT_WORK_ORDER(801008,"核算工单")
    ,expense_requestion(801009,"费用申请单")

    ;

    private Integer id;

    private String desc;

    DocumentCategoryEnum(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据id获取描述
     * @param id
     * @return
     */
    public static String getDescById(Integer id) {
        for (DocumentCategoryEnum documentCategoryEnum : DocumentCategoryEnum.values()) {
            if (documentCategoryEnum.id.equals(id)) {
                return documentCategoryEnum.desc;
            }
        }
        return null;
    }
}

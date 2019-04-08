package com.hand.hcf.app.mdata.externalApi;

import com.hand.hcf.app.expense.adjust.implement.web.AdjustControllerImpl;
import com.hand.hcf.app.expense.application.implement.web.ApplicationControllerImpl;
import com.hand.hcf.app.prepayment.implement.web.ImplementController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 获取单据信息
 * @Date 2019/2/20
 * @author chengshouting
 */
@Service
public class HcfFormTypeInterface {

    @Autowired
    private AdjustControllerImpl adjustInterface;
    @Autowired
    private ApplicationControllerImpl applicationInterface;
    @Autowired
    private ImplementController prepaymentInterface;


    /**
     * 根据单据大类和单据类型id获取单据类型名称
     * @param documentCategory
     * @param id
     * @return
     */
    public String getFormTypeNameByDocumentCategoryAndFormTypeId(String documentCategory, Long id) {
        //return this.getFormTypeNameByDocumentCategoryAndFormTypeId(documentCategory, id);
        //jiu.zhao 修改三方接口 20190404
        String formName = null;
        byte var5 = -1;
        switch(documentCategory.hashCode()) {
            case 1649068888:
                if (documentCategory.equals("801001")) {
                    var5 = 0;
                }
                break;
            case 1649068889:
                if (documentCategory.equals("801002")) {
                    var5 = 1;
                }
                break;
            case 1649068890:
                if (documentCategory.equals("801003")) {
                    var5 = 2;
                }
                break;
            case 1649068891:
                if (documentCategory.equals("801004")) {
                    var5 = 3;
                }
                break;
            case 1649068892:
                if (documentCategory.equals("801005")) {
                    var5 = 4;
                }
                break;
            case 1649068893:
                if (documentCategory.equals("801006")) {
                    var5 = 5;
                }
            case 1649068894:
            default:
                break;
            case 1649068895:
                if (documentCategory.equals("801008")) {
                    var5 = 6;
                }
                break;
            case 1649068896:
                if (documentCategory.equals("801009")) {
                    var5 = 7;
                }
        }

        switch(var5) {
            case 0:
            case 1:
            default:
                break;
            case 2:
                formName = this.prepaymentInterface.getFormTypeNameByFormTypeId(id);
                break;
            /*jiu.zhao 合同
            case 3:
                formName = this.contractInterface.getFormTypeNameByFormTypeId(id);
                break;*/
            /*jiu.zhao 支付
            case 4:
                formName = this.paymentInterface.getFormTypeNameByFormTypeId(id);
                break;*/
            case 5:
                formName = this.adjustInterface.getFormTypeNameByFormTypeId(id);
                break;
            /*jiu.zhao 核算
            case 6:
                formName = this.accountingInterface.getFormTypeNameByFormTypeId(id);
                break;*/
            case 7:
                formName = this.applicationInterface.getFormTypeNameByFormTypeId(id);
        }

        return formName;
    }
}

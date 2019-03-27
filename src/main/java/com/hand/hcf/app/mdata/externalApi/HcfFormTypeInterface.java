package com.hand.hcf.app.mdata.externalApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 获取单据信息
 * @Date 2019/2/20
 * @author chengshouting
 */
@Service
public class HcfFormTypeInterface {

    //jiu.zhao TODO 预付款
    //@Autowired
    //private BusinessModuleClient businessModuleClient;


    /**
     * 根据单据大类和单据类型id获取单据类型名称
     * @param documentCategory
     * @param id
     * @return
     */
    public String getFormTypeNameByDocumentCategoryAndFormTypeId(String documentCategory, Long id) {
        //return businessModuleClient.getFormTypeNameByDocumentCategoryAndFormTypeId(documentCategory, id);
        return null;
    }
}

package com.hand.hcf.app.mdata.implement.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.AuthorizeQueryCO;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.FormAuthorizeCO;
import com.hand.hcf.app.mdata.authorize.domain.FormCentralizedAuth;
import com.hand.hcf.app.mdata.authorize.domain.FormPersonalAuth;
import com.hand.hcf.app.mdata.authorize.service.FormCentralizedAuthService;
import com.hand.hcf.app.mdata.authorize.service.FormPersonalAuthService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 单据授权三方接口
 * @author shouting.cheng
 * @date 2019/2/11
 */
@RestController
public class AuthorizeControllerImpl {

    @Autowired
    private FormPersonalAuthService formPersonalAuthService;
    @Autowired
    private FormCentralizedAuthService formCentralizedAuthService;
    @Autowired
    private MapperFacade mapperFacade;

    public List<FormAuthorizeCO> listFormAuthorizeByDocumentCategoryAndUserId(@RequestParam String documentCategory,
                                                                              @RequestParam Long userId) {
        List<FormAuthorizeCO> result = new ArrayList();

        List<FormPersonalAuth>  formPersonalAuthList = formPersonalAuthService.selectList(
                new EntityWrapper<FormPersonalAuth>()
                        .eq("document_category", documentCategory)
                        .eq("bailee_id", userId)
                        .le("start_date", ZonedDateTime.now())
        );
        formPersonalAuthList = formPersonalAuthList.stream().filter(item -> {
            if (item.getEndDate() != null) {
                if (item.getEndDate().compareTo(ZonedDateTime.now()) > 0) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        formPersonalAuthList.stream().forEach(item -> {
            FormAuthorizeCO co = new FormAuthorizeCO();
            mapperFacade.map(item,co);
            result.add(co);
        });

        List<FormCentralizedAuth>  formCentralizedAuthList = formCentralizedAuthService.selectList(
                new EntityWrapper<FormCentralizedAuth>()
                        .eq("document_category", documentCategory)
                        .eq("bailee_id", userId)
                        .le("start_date", ZonedDateTime.now())
        );
        formCentralizedAuthList = formCentralizedAuthList.stream().filter(item -> {
            if (item.getEndDate() != null) {
                if (item.getEndDate().compareTo(ZonedDateTime.now()) > 0) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        formCentralizedAuthList.stream().forEach(item -> {
            FormAuthorizeCO co = new FormAuthorizeCO();
            mapperFacade.map(item,co);
            result.add(co);
        });

        return result;
    }

    //jiu.zhao 修改三方接口 20190403
    public Page<ContactCO> pageUsersByAuthorizeAndCondition(AuthorizeQueryCO queryCO, String userCode, String userName, Page page) {
        Page<ContactCO> contactCOList = this.pageUsersByAuthorizeAndCondition(queryCO, userCode, userName, page);
        return contactCOList != null ? contactCOList : new Page();
    }
}

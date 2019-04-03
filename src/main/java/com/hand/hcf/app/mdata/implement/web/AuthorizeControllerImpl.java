package com.hand.hcf.app.mdata.implement.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.FormAuthorizeCO;
import com.hand.hcf.app.mdata.authorize.domain.FormCentralizedAuth;
import com.hand.hcf.app.mdata.authorize.domain.FormPersonalAuth;
import com.hand.hcf.app.mdata.authorize.service.FormCentralizedAuthService;
import com.hand.hcf.app.mdata.authorize.service.FormPersonalAuthService;
import com.hand.hcf.app.mdata.contact.domain.Contact;
import com.hand.hcf.core.util.PageUtil;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
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

        List<FormPersonalAuth>  formPersonalAuthList = listPersonalAuthByDocumentCategoryAndUserId(documentCategory, userId);
        formPersonalAuthList.stream().forEach(item -> {
            FormAuthorizeCO co = new FormAuthorizeCO();
            mapperFacade.map(item,co);
            result.add(co);
        });

        List<FormCentralizedAuth>  formCentralizedAuthList = listCentralizedAuthByDocumentCategoryAndUserId(documentCategory, userId);
        formCentralizedAuthList.stream().forEach(item -> {
            FormAuthorizeCO co = new FormAuthorizeCO();
            mapperFacade.map(item,co);
            result.add(co);
        });

        return result;
    }

    /**
     * 根据条件查询单据可选的申请人
     * @param queryCO
     * @param userCode
     * @param userName
     * @param page
     * @param size
     * @return
     */

    public Page<ContactCO> pageUsersByAuthorizeAndCondition(@RequestBody AuthorizeQueryCO queryCO,
                                                            @RequestParam(required = false) String userCode,
                                                            @RequestParam(required = false) String userName,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        Page queryPage = PageUtil.getPage(page, size);
        Page<ContactCO> contactCOPage = new Page<>();

        Wrapper<Contact> orWrapper = this.getQueryWrapperByAuthorize(queryCO.getDocumentCategory(), queryCO.getFormTypeId(), queryCO.getCurrentUserId());

        List<Contact> contactList = contactService.listContactByConditionAndWrapper(
                queryCO.getCompanyIdList(),
                queryCO.getDepartmentIdList(),
                queryCO.getUserGroupIdList(),
                userCode,
                userName,
                orWrapper,
                queryPage
        );

        List<ContactCO> contactCOList = new ArrayList<>(8);
        contactList.stream().forEach(e -> {
            ContactCO contactCO = new ContactCO();
            contactCO.setId(e.getUserId());
            contactCO.setEmployeeCode(e.getEmployeeId());
            contactCO.setFullName(e.getFullName());
            contactCO.setUserOid(e.getUserOid().toString());
            contactCOList.add(contactCO);
        });

        contactCOPage.setTotal(queryPage.getTotal());
        contactCOPage.setRecords(contactCOList);
        return contactCOPage;
    }

    /**
     * 根据单据授权拼装查询SQL
     * @param documentCategory
     * @param formTypeId
     * @param currentUserId
     * @return
     */
    private Wrapper<Contact> getQueryWrapperByAuthorize(String documentCategory, Long formTypeId, Long currentUserId) {

        Wrapper<Contact> orWrapper = new EntityWrapper<Contact>();

        //查询所有相关单据授权
        List<FormAuthorizeCO> authorizeCOList = listFormAuthorizeByDocumentCategoryAndUserId(documentCategory, currentUserId);
        authorizeCOList = authorizeCOList.stream().filter(item -> {
            //根据单据类型筛选
            if (item.getFormId() != null && !item.getFormId().equals(formTypeId)){
                return false;
            }
            return true;
        }).collect(Collectors.toList());

        //加上当前登录人，即默认自己给自己授权
        FormAuthorizeCO currentUserAuthorizeCO = new FormAuthorizeCO();
        currentUserAuthorizeCO.setMandatorId(currentUserId);
        authorizeCOList.add(currentUserAuthorizeCO);

        //拼装SQL
        for(FormAuthorizeCO authorizeCO: authorizeCOList) {
            //如果有最大权限授权，不用拼装SQL
            if (authorizeCO.getDocumentCategory() != null
                    && authorizeCO.getCompanyId() == null
                    && authorizeCO.getUnitId() == null
                    && authorizeCO.getMandatorId() == null) {
                orWrapper = null;
                break;
            }
            orWrapper
                    .orNew()
                    .eq(authorizeCO.getCompanyId() != null, "c.company_id", authorizeCO.getCompanyId())
                    .eq(authorizeCO.getUnitId() != null, "du.department_id", authorizeCO.getUnitId())
                    .eq(authorizeCO.getMandatorId() != null, "c.user_id", authorizeCO.getMandatorId());
        }
        return orWrapper;
    }

    /**
     * 查询与该用户相关的个人授权
     * @param documentCategory
     * @param userId
     * @return
     */
    private List<FormPersonalAuth> listPersonalAuthByDocumentCategoryAndUserId(String documentCategory,
                                                                               Long userId) {
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

        return formPersonalAuthList;
    }

    /**
     * 查询与该用户相关的集中授权
     * @param documentCategory
     * @param userId
     * @return
     */
    private List<FormCentralizedAuth> listCentralizedAuthByDocumentCategoryAndUserId(String documentCategory,
                                                                                     Long userId) {
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
        return formCentralizedAuthList;
    }
}

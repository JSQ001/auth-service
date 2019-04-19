package com.hand.hcf.app.mdata.externalApi;

import com.hand.hcf.app.base.implement.web.*;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.common.co.OrderNumberCO;
import com.hand.hcf.app.common.co.UserCO;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.mdata.implement.web.CompanyControllerImpl;
import com.hand.hcf.app.mdata.system.enums.AttachmentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*import com.hand.hcf.app.client.user.UserCO;*/

/**
 * Created by liuzhiyu on 2017/9/15.
 */
@Service
public class HcfOrganizationInterface {

    @Autowired
    private CommonControllerImpl orgClient;

    @Autowired
    private UserControllerImpl userClient;

    @Autowired
    private AttchmentControllerImpl attachmentClient;
    @Autowired
    private CompanyControllerImpl companyClient;
    @Autowired
    private UserRoleControllerImpl userRoleClient;

    @Autowired
    private LovControllerImpl lovClient;

    public OrderNumberCO getVendorCode(String companyCode, Long tenantId) {
        return orgClient.getOrderNumberCO("VENDER", companyCode, "");

    }

    public SysCodeValueCO getValueBySysCodeAndValue(String code, String value) {
        return orgClient.getSysCodeValueByCodeAndValue(code, value);
    }

    public List<SysCodeValueCO> listAllSysCodeValueByCode(String code) {
        return orgClient.listAllSysCodeValueByCode(code);
    }

    public AttachmentCO getAttachmentByOid(String oid){
        return attachmentClient.getByOid(oid);
    }

    public AttachmentCO getAttachmentByOid(UUID oid){
        if(oid == null){
            return null;
        }
        return getAttachmentByOid(oid.toString());
    }

    public AttachmentCO getAttachmentById(Long attachmentId) {
        return attachmentClient.getAttachmentById(attachmentId);
    }

    public AttachmentCO uploadStatic(MultipartFile file, AttachmentType attachmentType){
        return attachmentClient.uploadStatic(file,attachmentType.name());
    }

    //删除附件
    public void removeFile(boolean isPublic, String path){
        attachmentClient.removeFile(isPublic,path);
    }

    /**
     * 根据用户OID获取用户语言
     * @param userOid
     * @return
     */
    public String getLanguageByUserOid(String userOid){
        return userClient.getLanguageByUserOid(userOid);
    }

    public String getLanguageByUserOid(UUID userOid){
        return getLanguageByUserOid(userOid.toString());
    }

    /**
     * 创建用户
     * @param user
     * @return
     */
    public UserCO saveUser(UserCO user){
        return userClient.saveUser(user);
    }

    /**
     * 员工离职
     * @param userId
     */
    public void updateUserLeaveOffice(Long userId){
        userClient.updateUserLeaveOffice(userId);
    }

    /**
     * 员工复职
     * @param userId
     */
    public void updateUserRecoverEntry(Long userId){
        userClient.updateUserRecoverEntry(userId);
    }

    /**
     * 批量创建用户
     * @param users
     * @return
     */
    public List<UserCO> saveUserBatch(List<UserCO> users){
        return userClient.saveUserBatch(users);
    }

    //jiu.zhao 修改三方接口
    public List<SysCodeValueCO> listEnabledSysCodeValueByCode(String code) {
        List<SysCodeValueCO> sysCodeValueCOS = orgClient.listSysValueByCodeConditionByEnabled(code, true);
        return (List)(null == sysCodeValueCOS ? new ArrayList() : sysCodeValueCOS);
    }
    /**
     * 校验数据权限规则是否被使用
     *
     * @param id 用户权限id
     * @return 是否被启用
     */
    public Boolean dataAuthHasUsed(Long id) {
        return userRoleClient.dataAuthHasUsed(id);
    }

    //jiancheng.li TODO
  /*  public Object getObjectByLovCode(String code,
                                     String id){
       return lovClient.getObjectByLovCode(code,id);
    }*/

}

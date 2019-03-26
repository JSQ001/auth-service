package com.hand.hcf.app.prepayment.web.adapter;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.base.attachment.AttachmentCO;
import com.hand.hcf.app.common.co.CashPaymentRequisitionHeaderCO;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.mdata.client.contact.ContactCO;
import com.hand.hcf.app.mdata.client.department.DepartmentCO;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionType;
import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionHead;
import com.hand.hcf.app.prepayment.domain.PrepaymentAttachment;
import com.hand.hcf.app.prepayment.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.prepayment.service.CashPayRequisitionTypeService;
import com.hand.hcf.app.prepayment.service.CashPaymentRequisitionLineService;
import com.hand.hcf.app.prepayment.service.PrepaymentAttachmentService;
import com.hand.hcf.app.prepayment.utils.RespCode;
import com.hand.hcf.app.prepayment.web.dto.CashPayRequisitionTypeDTO;
import com.hand.hcf.core.exception.BizException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CashPaymentRequisitionHeaderAdapter {

    @Autowired
    private CashPaymentRequisitionLineService cashPaymentRequisitionLineService;
    @Autowired
    private PrepaymentAttachmentService prepaymentAttachmentService;
    @Autowired
    private CashPayRequisitionTypeService cashPayRequisitionTypeService;
    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;



    public CashPaymentRequisitionHeaderCO toDTO(CashPaymentRequisitionHead head){
        CashPaymentRequisitionHeaderCO dto = new CashPaymentRequisitionHeaderCO();
        BeanUtils.copyProperties(head,dto);
        try{
            ContactCO userInfoDTOByCreatedBy = hcfOrganizationInterface.getUserById(head.getCreatedBy());
            dto.setCreateByName( userInfoDTOByCreatedBy.getFullName());
            dto.setCreatedByCode(userInfoDTOByCreatedBy.getEmployeeCode());
            //TODO
            ContactCO userInfoDTOByEmployeeId = hcfOrganizationInterface.getUserById(head.getEmployeeId());
            dto.setEmployeeName(userInfoDTOByEmployeeId.getFullName());
            dto.setEmployeeCode(userInfoDTOByEmployeeId.getEmployeeCode());
        }catch (Exception e){
            e.printStackTrace();
            throw new BizException(RespCode.SYS_USER_INFO_NOT_EXISTS);
        }
        try{
            dto.setCompanyName(hcfOrganizationInterface.getCompanyById(head.getCompanyId()).getName());
        }catch (Exception e){
            e.printStackTrace();
            throw new BizException(RespCode.SYS_COMPANY_INFO_NOT_EXISTS);
        }
           try{
               DepartmentCO unitsByUnitId = hcfOrganizationInterface.getUnitsByUnitId(head.getUnitId());
               dto.setUnitOid(unitsByUnitId.getDepartmentOid().toString());
               dto.setUnitName(unitsByUnitId.getName());
               dto.setPath(unitsByUnitId.getPath());

           }catch (Exception e){
               e.printStackTrace();
               throw new BizException(RespCode.SYS_UNIT_INFO_NOT_EXITS);
           }

           try {
               CashPayRequisitionTypeDTO cashPay = cashPayRequisitionTypeService.getCashPayRequisitionType(head.getPaymentReqTypeId());
               CashPayRequisitionType requisitionType = cashPay.getCashPayRequisitionType();
               dto.setTypeName(requisitionType.getTypeName());
               dto.setIfApplication(requisitionType.getNeedApply());
               dto.setPaymentMethod(requisitionType.getPaymentMethodCategoryName());
               dto.setPaymentMethodCode(requisitionType.getPaymentMethodCategory());
               if(StringUtils.isNotEmpty(requisitionType.getFormOid())){
                   dto.setFormOid(requisitionType.getFormOid());
               }
           }catch (Exception e){
               e.printStackTrace();
                throw new BizException(RespCode.PREPAY_CASH_PAY_REQUISITION_TYPE_NOT_EXIST);
           }

        if(StringUtils.isNotEmpty(head.getAttachmentOid())){
            try{
                    AttachmentOidParseArray(head.getAttachmentOid(),dto);
                    List<AttachmentCO> prepaymentAttachments = new ArrayList<>();
                    for(String oid:dto.getAttachmentOids()){
                        prepaymentAttachments.add(hcfOrganizationInterface.getAttachmentByOID(oid));//读取本地附件
                    }
                    dto.setAttachments(prepaymentAttachments);

            }catch (Exception e){
                   e.printStackTrace();
                   throw new BizException(RespCode.SYS_ATTACHMENT_INFO_ERR);
            }
        }else {
            dto.setAttachments(new ArrayList<>());
            dto.setAttachmentOids(new ArrayList<>());
        }

        return dto;
    }

    public static CashPaymentRequisitionHead toDomain(CashPaymentRequisitionHeaderCO dto){
        CashPaymentRequisitionHead head = new CashPaymentRequisitionHead();
        BeanUtils.copyProperties(dto,head);
        AttachmentOidToJsonString(dto.getAttachmentOids(),head);

        return head;
    }


    //jsonToString
    public static void AttachmentOidToJsonString(List<String> list, CashPaymentRequisitionHead header){
        if(CollectionUtils.isNotEmpty(list)){
            header.setAttachmentOid(JSON.toJSONString(list));
        }
    }


    //StringToJson
    public static void AttachmentOidParseArray(String attachmentOid, CashPaymentRequisitionHeaderCO dto){
        dto.setAttachmentOids(JSON.parseArray(attachmentOid,String.class));
    }


    //读取本地附件
    public  AttachmentCO getAttachmentByOid(String OID){
        AttachmentCO attachmentDTO = new AttachmentCO();
        PrepaymentAttachment prepaymentAttachment = prepaymentAttachmentService.selectByOId(OID);
        BeanUtils.copyProperties(prepaymentAttachment,attachmentDTO);
        return attachmentDTO;
    }

}

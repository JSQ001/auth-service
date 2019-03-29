package com.hand.hcf.app.mdata.legalEntity.conver;

/*import com.hand.hcf.app.client.attachment.AttachmentCO;*/
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.legalEntity.domain.LegalEntity;
import com.hand.hcf.app.mdata.legalEntity.dto.LegalEntityDTO;
import com.hand.hcf.core.component.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

import java.time.ZonedDateTime;

/**
 * 法人转换类
 * Created by fanfuqiang 2018/11/19
 */
public class LegalEntityConver {

    /**
     * 法人实体类转换法人实体视图对象
     *
     * @param legalEntity：法人实体对象
     * @return
     */
    public static LegalEntityDTO legalEntityTolegalEntityDTO(LegalEntity legalEntity) {
        LegalEntityDTO legalEntityDTO = null;
        if (legalEntity != null) {
            legalEntityDTO = new LegalEntityDTO();
            legalEntityDTO.setId(legalEntity.getId());
            legalEntityDTO.setCompanyReceiptedOid(legalEntity.getLegalEntityOid());
            legalEntityDTO.setTenantId(legalEntity.getTenantId());
            legalEntityDTO.setSetOfBooksId(legalEntity.getSetOfBooksId());
            legalEntityDTO.setParentLegalEntityId(legalEntity.getParentLegalEntityId());
            legalEntityDTO.setEntityName(legalEntity.getEntityName());
            legalEntityDTO.setAddress(legalEntity.getAddress());
            legalEntityDTO.setTaxpayerNumber(legalEntity.getTaxpayerNumber());
            legalEntityDTO.setAccountBank(legalEntity.getAccountBank());
            legalEntityDTO.setTelePhone(legalEntity.getTelePhone());
            legalEntityDTO.setAccountNumber(legalEntity.getAccountNumber());
            legalEntityDTO.setEnabled(legalEntity.getEnabled());
            legalEntityDTO.setDeleted(legalEntity.getDeleted());
            legalEntityDTO.setCreatedBy(legalEntity.getCreatedBy());
            legalEntityDTO.setCreatedDate(legalEntity.getCreatedDate());
            legalEntityDTO.setLastUpdatedBy(legalEntity.getLastUpdatedBy());
            legalEntityDTO.setLastUpdatedate(legalEntity.getLastUpdatedDate());
            legalEntityDTO.setI18n(legalEntity.getI18n());
            legalEntityDTO.setAttachmentId(legalEntity.getAttachmentId());
            legalEntityDTO.setMainLanguage(legalEntity.getMainLanguage());
            if(null != legalEntity.getAttachmentId()){
                ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
                HcfOrganizationInterface organizationInterface = applicationContext.getBean(HcfOrganizationInterface.class);
                AttachmentCO attachmentCO = organizationInterface.getAttachmentById(legalEntity.getAttachmentId());
                legalEntityDTO.setFileURL(attachmentCO.getFileUrl());
                legalEntityDTO.setThumbnailUrl(attachmentCO.getThumbnailUrl());
                legalEntityDTO.setIconUrl(attachmentCO.getIconUrl());
            }
            legalEntityDTO.setPath(legalEntity.getPath());
            legalEntityDTO.setDepth(legalEntity.getDepth());
        }
        return legalEntityDTO;
    }

    /**
     * 法人实体视图对象转换法人实体对象
     *
     * @param legalEntityDTO：法人视图对象
     * @return
     */
    public static LegalEntity legalEntityDTOTolegalEntity(LegalEntityDTO legalEntityDTO) {
        LegalEntity legalEntity = null;
        if (legalEntityDTO != null) {
            legalEntity = new LegalEntity();
            legalEntity.setId(legalEntityDTO.getId());
            legalEntity.setLegalEntityOid(legalEntityDTO.getCompanyReceiptedOid());
            legalEntity.setTenantId(legalEntityDTO.getTenantId());
            legalEntity.setSetOfBooksId(legalEntityDTO.getSetOfBooksId());
            legalEntity.setParentLegalEntityId(legalEntityDTO.getParentLegalEntityId());
            legalEntity.setEntityName(legalEntityDTO.getEntityName());
            legalEntity.setAddress(legalEntityDTO.getAddress());
            legalEntity.setTaxpayerNumber(legalEntityDTO.getTaxpayerNumber());
            legalEntity.setAccountBank(legalEntityDTO.getAccountBank());
            legalEntity.setTelePhone(legalEntityDTO.getTelePhone());
            legalEntity.setAccountNumber(legalEntityDTO.getAccountNumber());
            legalEntity.setEnabled(legalEntityDTO.getEnabled());
            legalEntity.setDeleted(legalEntityDTO.getDeleted());
            legalEntity.setI18n(legalEntityDTO.getI18n());
            legalEntity.setMainLanguage(legalEntityDTO.getMainLanguage());
            legalEntity.setLastUpdatedDate(ZonedDateTime.now());
            legalEntity.setAttachmentId(legalEntityDTO.getAttachmentId());
        }
        return legalEntity;
    }

}

package com.hand.hcf.app.base.attachment;

import com.hand.hcf.app.base.attachment.domain.Attachment;
import com.hand.hcf.app.base.attachment.enums.AttachmentType;
import com.hand.hcf.app.base.attachment.persistence.AttachmentMapper;
import com.hand.hcf.app.base.attachment.service.IAttachment;
import com.hand.hcf.app.base.system.constant.CacheConstants;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Attachment.
 * @author polus
 */
@Service
@CacheConfig(cacheNames = {CacheConstants.ATTACHMENT})
public class AttachmentService extends BaseService<AttachmentMapper,Attachment> {

    public static final String THUMBNAIL = "-thumbnail-";
    public static final String ICON = "-icon-";
    private final Logger log = LoggerFactory.getLogger(AttachmentService.class);
    @Autowired
    private AttachmentMapper attachmentMapper;

    @Autowired
    IAttachment attachmentImpl;

    /**
     * delete the  attachment by oids.
     * delete the  attachment by oid.
     */
    public void delete(String oid) {
        log.debug("Request to delete Attachment : {}", oid);
        Attachment attachment =  attachmentMapper.findByAttachmentOid(oid);
        attachmentImpl.removeFile(Arrays.asList(attachment));
        attachmentMapper.deleteById(attachment.getId());
    }
    /**
     * delete the  attachment by oids.
     */
    public void deleteByOids(List<UUID> oids) {
        log.debug("Request to delete Attachment : {}", oids);
        List<Attachment> attachments = new ArrayList<>();
        Collection<Attachment> attachmentsParam = attachmentMapper.findByAttachmentOidIn(oids);
        attachments.addAll(attachmentsParam);
        List<Long> attachmentIds = attachments.stream().map(u -> u.getId()).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(attachmentIds)) {
            //先删除表
            attachmentMapper.deleteBatchIds(attachmentIds);
            //再删除文件
            attachmentImpl.removeFile(attachments);
        }
    }

    public void removeFile(boolean isPublic, String path){
        attachmentImpl.removeFile(isPublic,path);
    }

    public Attachment uploadStatic(MultipartFile file, AttachmentType attachmentType){
        return attachmentImpl.uploadStatic(file,attachmentType);
    }

    public AttachmentCO uploadStaticFile(MultipartFile file, AttachmentType attachmentType){
        return  attachmentImpl.uploadStaticFile(file,attachmentType);
    }


    public AttachmentCO uploadStaticFile(byte[] content, AttachmentType attachmentType,String fileName,Long length){
        return  attachmentImpl.uploadStaticFile(content,attachmentType,fileName,length);
    }

    /**
     * get one attachment by Oid.
     *
     * @return the entity
     */
    @Transactional(readOnly = true)
    public AttachmentCO findByAttachmentOid(UUID attachmentOid) {
        log.debug("Request to get Attachment : {}", attachmentOid);
        Attachment attachment = attachmentMapper.findByAttachmentOid(attachmentOid.toString());
        if (attachment == null) {
            return null;
        }
        AttachmentCO AttachmentCO = attachmentImpl.adapterAttachmentCO(attachment);
        return AttachmentCO;
    }

    @Transactional(readOnly = true)
    public Attachment findByOId(String id){
        Attachment attachment = attachmentMapper.findByAttachmentOid(id);
        return attachment;
    }


    /**
     * get List attachment by Oids.
     *
     * @return the entity
     */
    @Transactional(readOnly = true)
    public List<AttachmentCO> findByAttachmentOids(List<UUID> attachmentOids) {
        if (attachmentOids == null || attachmentOids.size() == 0) {
            return new ArrayList<AttachmentCO>();
        }
        log.debug("Request to get Attachments : {}", attachmentOids);
        Collection<Attachment> attachments = attachmentMapper.findByAttachmentOidIn(attachmentOids);
        List<Attachment> attachmentsParam = new ArrayList<>();
        attachmentsParam.addAll(attachments);
        return attachmentsParam.stream().map(a->attachmentImpl.adapterAttachmentCO(a)).collect(Collectors.toList());
    }



    public Attachment findOneByAttachmentOid(UUID attachmentOid) {
        return attachmentMapper.findByAttachmentOid(attachmentOid.toString());
    }




    /**
     * 根据附件id获取附件信息
     * @param id：附件id
     * @return
     */
    @Transactional
    public AttachmentCO getAttachmentById(Long id){
        return attachmentImpl.adapterAttachmentCO(attachmentMapper.selectById(id));
    }

    public static Attachment AttachmentCOToAttachment(AttachmentCO AttachmentCO){
        if(null == AttachmentCO){
            return null;
        }
        Attachment attachment = new Attachment();
        attachment.setId(AttachmentCO.getId());
        attachment.setAttachmentOid(AttachmentCO.getAttachmentOid());
        attachment.setName(AttachmentCO.getFileName());
        attachment.setMediaTypeID(AttachmentCO.getFileType().getId());
        attachment.setPath(AttachmentCO.getFileUrl());
        attachment.setThumbnailPath(AttachmentCO.getThumbnailUrl());
        attachment.setIconPath(AttachmentCO.getIconUrl());
        attachment.setSizes(AttachmentCO.getSize());
        attachment.setCreatedBy(LoginInformationUtil.getCurrentUserId());
        return attachment;
    }

    public AttachmentCO uploadFile(MultipartFile file, AttachmentType attachmentType){
        return attachmentImpl.uploadFile(file,attachmentType);
    }

    public AttachmentCO uploadFileAsync(MultipartFile file, AttachmentType attachmentType){
        return attachmentImpl.uploadFileAsync(file,attachmentType);
    }

    public String uploadPublicStaticAttachment( AttachmentType attachmentType, MultipartFile file){
        return attachmentImpl.uploadPublicStaticAttachment(attachmentType,file);
    }

    public void writeFileToResp(HttpServletResponse response, Attachment attachment){
        attachmentImpl.writeFileToResp(response,attachment);
    }

    public AttachmentCO AttachmentToAttachmentCO(Attachment attachment){
        return attachmentImpl.adapterAttachmentCO(attachment);
    }
}

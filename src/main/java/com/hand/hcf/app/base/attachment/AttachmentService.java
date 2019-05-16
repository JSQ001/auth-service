package com.hand.hcf.app.base.attachment;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.base.attachment.domain.Attachment;
import com.hand.hcf.app.base.attachment.persistence.AttachmentMapper;
import com.hand.hcf.app.base.attachment.service.IAttachment;
import com.hand.hcf.app.base.system.constant.CacheConstants;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.core.service.BaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Attachment.
 * @author polus
 */
@Service
@CacheConfig(cacheNames = {CacheConstants.ATTACHMENT})
public class AttachmentService extends BaseService<AttachmentMapper, Attachment> {

    private final Logger log = LoggerFactory.getLogger(AttachmentService.class);
    @Autowired
    private AttachmentMapper attachmentMapper;

    @Autowired
    IAttachment attachmentImpl;

    /**
     * delete the  attachment by oids.
     * delete the  attachment by oid.
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String oid) {
        Attachment attachment =  attachmentMapper.findByAttachmentOid(oid);
        attachmentImpl.removeFile(Arrays.asList(attachment));
        attachmentMapper.deleteById(attachment.getId());
    }
    // OSS 删除 根据传入的附件的Oid
    @Transactional(rollbackFor = Exception.class)
    public void deleteOssFile(String oid) {
        Attachment attachment =  attachmentMapper.findByAttachmentOid(oid);
        String objectName = attachment.getPath();
        //先删除服务器文件
        if(StringUtils.isNotBlank(objectName)) {
            attachmentImpl.deleteOssFile(objectName);
        }
        //再删除表中数据
        attachmentMapper.deleteById(attachment.getId());

    }
    /**
     * delete the  attachment by oids.
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteByOids(List<UUID> oids) {
        Collection<Attachment> attachmentsParam = attachmentMapper.findByAttachmentOidIn(oids);
        List<Attachment> attachments = new ArrayList<>(attachmentsParam);
        List<Long> attachmentIds = attachments.stream().map(Attachment::getId).collect(Collectors.toList());
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

    @Transactional(rollbackFor = Exception.class)
    public Attachment uploadStatic(MultipartFile file, String attachmentType, String pkValue){
        return attachmentImpl.uploadStatic(file,attachmentType, pkValue);
    }
    @Transactional(rollbackFor = Exception.class)
    public AttachmentCO uploadStaticFile(MultipartFile file, String attachmentType, String pkValue){
        return  attachmentImpl.uploadStaticFile(file, attachmentType, pkValue);
    }



    /**
     * get one attachment by Oid.
     *
     * @return the entity
     */
    public AttachmentCO findByAttachmentOid(UUID attachmentOid) {
        log.debug("Request to get Attachment : {}", attachmentOid);
        Attachment attachment = attachmentMapper.findByAttachmentOid(attachmentOid.toString());
        if (attachment == null) {
            return null;
        }
        return attachmentImpl.adapterAttachmentCO(attachment);
    }

    public Attachment findByOId(String id){
        return attachmentMapper.findByAttachmentOid(id);
    }


    /**
     * get List attachment by Oids.
     *
     * @return the entity
     */
    public List<AttachmentCO> findByAttachmentOids(List<UUID> attachmentOids) {
        if (attachmentOids == null || attachmentOids.size() == 0) {
            return new ArrayList<>();
        }
        log.debug("Request to get Attachments : {}", attachmentOids);
        Collection<Attachment> attachments = attachmentMapper.findByAttachmentOidIn(attachmentOids);
        List<Attachment> attachmentsParam = new ArrayList<>(attachments);
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
    public AttachmentCO getAttachmentById(Long id){
        return attachmentImpl.adapterAttachmentCO(attachmentMapper.selectById(id));
    }


    @Transactional(rollbackFor = Exception.class)
    public AttachmentCO uploadFile(MultipartFile file, String attachmentType, String pkValue){
        return attachmentImpl.uploadFile(file, attachmentType, pkValue);
    }


    public void writeFileToResp(HttpServletResponse response, Attachment attachment){
        attachmentImpl.writeFileToResp(response,attachment);
    }

    public AttachmentCO attachmentToAttachmentCO(Attachment attachment){
        return attachmentImpl.adapterAttachmentCO(attachment);
    }

    public void savePkVale(List<String> attachmentOids, String pkValue, String attachmentType) {
        List<Attachment> attachments = attachmentMapper.selectList(
                getWrapper()
                        .eq("pk_name", attachmentType)
                        .in("attachment_oid", attachmentOids));
        if (CollectionUtils.isNotEmpty(attachments)) {
            attachments.forEach(e -> e.setPkValue(pkValue));
            this.updateBatchById(attachments);
        }
    }


    public List<AttachmentCO> listByPkValue(String pkValue, String attachmentType) {
        List<Attachment> attachments = attachmentMapper.selectList(
                getWrapper()
                        .eq("pk_value", pkValue)
                        .eq("pk_name", attachmentType));
        return attachments.stream().map(e -> attachmentImpl.adapterAttachmentCO(e)).collect(Collectors.toList());
    }

    public Map<String, List<AttachmentCO>> listByPkValues(String attachmentType, List<String> pkValues) {
        Map<String, List<AttachmentCO>> result = new HashMap<>(16);
        pkValues.forEach(v -> result.put(v, listByPkValue(v, attachmentType)));
        return result;
    }

    public void deleteByPkValue(String pkValue, String attachmentType) {
        Wrapper<Attachment> wrapper = getWrapper()
                .eq("pk_value", pkValue)
                .eq("pk_name", attachmentType);
        deleteFileByWrapper(wrapper);
    }

    public void deleteByPkValues(String attachmentType, List<String> pkValues) {
        Wrapper<Attachment> wrapper = getWrapper()
                .in("pk_value", pkValues)
                .eq("pk_name", attachmentType);
        deleteFileByWrapper(wrapper);
    }

    private void deleteFileByWrapper (Wrapper<Attachment> wrapper) {
        List<Attachment> attachments = attachmentMapper.selectList(wrapper);
        // 删除表
        this.delete(wrapper);
        // 删文件
        attachmentImpl.removeFile(attachments);
    }
}

package com.hand.hcf.app.base.implement.web;


import com.hand.hcf.app.base.attachment.AttachmentService;
import com.hand.hcf.app.common.co.AttachmentCO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/18
 */

@RestController
public class AttachmentControllerImpl {
    @Autowired
    private AttachmentService attachmentService;
    /**
     * 根据附件Oid集合查询附件信息
     * @param oidList 附件oid集合
     */
    public List<AttachmentCO> listByOids(@RequestBody List<String> oidList){
        List<UUID> collect = oidList.stream().map(UUID::fromString).collect(Collectors.toList());
        return attachmentService.findByAttachmentOids(collect);
    }

    /**
     * 根据附件Oid集合删除附件信息
     * @param oidList 附件oid集合
     */
    public void deleteByOids(@RequestBody List<String> oidList){
        List<UUID> collect = oidList.stream().map(UUID::fromString).collect(Collectors.toList());
        attachmentService.deleteByOids(collect);
    }

    /**
     * 根据附件Oid获取附件信息
     * @param oid 附件oid
     */
    public AttachmentCO getByOid(@PathVariable("oid") String oid){
        return attachmentService.findByAttachmentOid(UUID.fromString(oid));
    }

    public AttachmentCO getAttachmentById(Long id) {
        return attachmentService.getAttachmentById(id);
    }


    public AttachmentCO uploadStatic(MultipartFile file, String attachmentType, String pkValue) {
        return attachmentService.uploadStaticFile(file, attachmentType, pkValue);
    }


    public void removeFile(boolean isPublic, String path) {
        attachmentService.removeFile(isPublic,path);
    }

    public void savePkVale(List<String> attachmentOids, String pkValue, String attachmentType) {
        attachmentService.savePkVale(attachmentOids, pkValue, attachmentType);
    }

    public List<AttachmentCO> listByPkValue(String pkValue, String attachmentType) {
        return attachmentService.listByPkValue(pkValue, attachmentType);
    }

    public Map<String, List<AttachmentCO>> listByPkValues(String attachmentType, List<String> pkValues) {
        return attachmentService.listByPkValues(attachmentType, pkValues);
    }

    public void deleteByPkValue(String pkValue, String attachmentType) {
        attachmentService.deleteByPkValue(pkValue, attachmentType);
    }

    public void deleteByPkValues(String attachmentType, List<String> pkValues) {
        attachmentService.deleteByPkValues(attachmentType, pkValues);
    }

}

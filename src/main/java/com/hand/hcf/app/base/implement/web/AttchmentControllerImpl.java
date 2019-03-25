package com.hand.hcf.app.base.implement.web;


import com.hand.hcf.app.base.attachment.AttachmentCO;
import com.hand.hcf.app.base.attachment.AttachmentService;
import com.hand.hcf.app.base.attachment.enums.AttachmentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
public class AttchmentControllerImpl {
    @Autowired
    private AttachmentService attachmentService;
    /**
     * 根据附件Oid集合查询附件信息
     * @param oidList 附件oid集合
     */
    public List<AttachmentCO> listByOids(@RequestBody List<String> oidList){
        List<UUID> collect = oidList.stream().map(e -> UUID.fromString(e)).collect(Collectors.toList());
        return attachmentService.findByAttachmentOids(collect);
    }

    /**
     * 根据附件Oid集合删除附件信息
     * @param oidList 附件oid集合
     */
    public void deleteByOids(@RequestBody List<String> oidList){
        List<UUID> collect = oidList.stream().map(e -> UUID.fromString(e)).collect(Collectors.toList());
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

    public AttachmentCO uploadStatic(MultipartFile file, String attachmentType) {
        return attachmentService.uploadStaticFile(file, AttachmentType.valueOf(attachmentType));
    }

    public void removeFile(boolean isPublic, String path) {
        attachmentService.removeFile(isPublic,path);
    }

}

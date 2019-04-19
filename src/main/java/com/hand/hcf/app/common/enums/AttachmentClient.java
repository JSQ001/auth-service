package com.hand.hcf.app.common.enums;

import com.hand.hcf.app.base.attachment.AttachmentCO;
import com.hand.hcf.app.base.implement.web.AttchmentControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Date: Created in 23:24 2018/6/27
 * @Modified by
 */
@Service
public class AttachmentClient {

    @Autowired
    private AttchmentControllerImpl attachmentInterface;

    /**
     * 根据附件OID集合查询附件信息
     * @param oidList 附件oid集合
     * @return 附件对象集合
     */
    public List<AttachmentCO> listByOids(List<String> oidList){
        if (CollectionUtils.isEmpty(oidList)){
            return new ArrayList<>();
        }
        return attachmentInterface.listByOids(oidList);
    }

    /**
     * 根据附件OID集合删除附件信息
     * @param oidList 附件oid集合
     */
    public void deleteByOids(List<String> oidList){
        if (CollectionUtils.isEmpty(oidList)){
            return;
        }
        attachmentInterface.deleteByOids(oidList);
    }


    /**
     * 根据附件OID获取附件信息
     * @param oid 附件oid
     * @return 附件信息
     */
    public AttachmentCO getByOid(String oid){
        return attachmentInterface.getByOid(oid);
    }

    public AttachmentCO getAttachmentById(Long attachmentId) {
        return attachmentInterface.getAttachmentById(attachmentId);
    }

    public AttachmentCO uploadStatic(MultipartFile file, String attachmentType){
        return attachmentInterface.uploadStatic(file,attachmentType);
    }


    //删除附件
    public void removeFile(boolean isPublic, String path){
        attachmentInterface.removeFile(isPublic,path);
    }

}

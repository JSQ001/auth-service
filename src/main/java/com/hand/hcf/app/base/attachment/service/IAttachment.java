package com.hand.hcf.app.base.attachment.service;

import com.hand.hcf.app.base.attachment.domain.Attachment;
import com.hand.hcf.app.base.attachment.enums.AttachmentType;
import com.hand.hcf.app.common.co.AttachmentCO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Service Implementation for managing Attachment.
 */
@Component
public interface IAttachment {


    void writeFileToResp(HttpServletResponse response, Attachment attachment);

    void removeFile(List<Attachment> attachments);

    String upload(String path, byte[] bytes);

    Attachment upload(MultipartFile file, AttachmentType attachmentType);

    AttachmentCO uploadFile(MultipartFile file, AttachmentType attachmentType);

    AttachmentCO uploadFileAsync(MultipartFile file, AttachmentType attachmentType);

    /**
     * 上传附件
     *
     * @param content
     * @param fileName
     * @param attachmentType
     * @return
     */
    AttachmentCO uploadFile(byte[] content, String fileName, AttachmentType attachmentType, UUID userOid);
    String uploadStaticFile(String path, byte[] bytes);
    Attachment uploadStatic(MultipartFile file, AttachmentType attachmentType);

    Attachment uploadStatic(MultipartFile file, AttachmentType attachmentType, String fileName);

    Attachment uploadStatic(byte[] content, AttachmentType attachmentType, String filename, Long length);

    AttachmentCO uploadStaticFile(MultipartFile file, AttachmentType attachmentType);

    AttachmentCO uploadStaticFile(MultipartFile file, AttachmentType attachmentType, String fileName);

    AttachmentCO uploadStaticFile(byte[] content, AttachmentType attachmentType, String filename, Long length);

    AttachmentCO uploadTempFile(byte[] content, String fileName, AttachmentType attachmentType, UUID userOid);

    Attachment uploadTempStatic(byte[] content, String fileName, AttachmentType attachmentType, UUID userOid);


    void removeFile(boolean isPublic, String path);

    /**
     * 轮播图
     *
     * @param attachmentType
     * @param file
     * @return
     */
    String uploadPublicStaticAttachment( AttachmentType attachmentType, MultipartFile file);

    AttachmentCO adapterAttachmentCO(Attachment attachment);

    void downLoadFile(HttpServletRequest httpServletRequest,
                      HttpServletResponse httpServletResponse,
                      String objectName) throws IOException;

}

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

    Attachment upload(MultipartFile file, String attachmentType, String pkValue);

    AttachmentCO uploadFile(MultipartFile file, String attachmentType, String pkValue);


    Attachment uploadStatic(MultipartFile file, String attachmentType, String pkValue);


    AttachmentCO uploadStaticFile(MultipartFile file, String attachmentType, String pkValue);


    void removeFile(boolean isPublic, String path);


    AttachmentCO adapterAttachmentCO(Attachment attachment);

    void downLoadFile(HttpServletRequest httpServletRequest,
                      HttpServletResponse httpServletResponse,
                      String objectName) throws IOException;

}

package com.hand.hcf.app.base.attachment.service;

import com.hand.hcf.app.base.attachment.domain.Attachment;
import com.hand.hcf.app.base.attachment.enums.AttachmentType;
import com.hand.hcf.app.common.co.AttachmentCO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

/**
 * Service Implementation for managing Attachment.
 */
@Component
public interface IOSSAttachmentService {

    /**
     * 上传附件
     *
     * @param file
     * @param attachmentType
     * @return
     */
    AttachmentCO uploadStaticFile(MultipartFile file, AttachmentType attachmentType);

    Attachment uploadStatic(MultipartFile file, AttachmentType attachmentType);

    AttachmentCO adapterAttachmentCO(Attachment attachment);

}

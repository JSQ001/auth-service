package com.hand.hcf.app.base.attachment;

import com.hand.hcf.app.base.attachment.domain.Attachment;
import com.hand.hcf.app.base.attachment.enums.AttachmentType;
import com.hand.hcf.app.base.attachment.persistence.AttachmentMapper;
import com.hand.hcf.app.base.attachment.service.IAttachment;
import com.hand.hcf.app.base.attachment.service.IOSSAttachmentService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Attachment.
 * @author polus
 */
@Service
@CacheConfig(cacheNames = {CacheConstants.ATTACHMENT})
public class OSSAttachmentService extends BaseService<AttachmentMapper,Attachment> {


    private final Logger log = LoggerFactory.getLogger(OSSAttachmentService.class);


    @Autowired
    private IOSSAttachmentService attachmentService;



    public AttachmentCO uploadStaticFile(MultipartFile file, AttachmentType attachmentType){
        return  attachmentService.uploadStaticFile(file,attachmentType);
    }

    public AttachmentCO AttachmentToAttachmentCO(Attachment attachment){
        return attachmentService.adapterAttachmentCO(attachment);
    }

    public Attachment uploadStatic(MultipartFile file, AttachmentType attachmentType){
        return attachmentService.uploadStatic(file,attachmentType);
    }
}

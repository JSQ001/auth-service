package com.hand.hcf.app.base.attachment;

import com.hand.hcf.app.base.attachment.domain.AttachmentPdf;
import com.hand.hcf.app.base.attachment.persistence.AttachmentPdfMapper;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Created by Wkit on 2017/6/7.
 */
@Service
@AllArgsConstructor
public class AttachmentPdfService extends BaseService<AttachmentPdfMapper, AttachmentPdf> {

    private final AttachmentPdfMapper attachmentPdfMapper;

    public List<AttachmentPdf> findByEntityOid(UUID entityOid) {
        return attachmentPdfMapper.findByEntityOid(entityOid.toString());
    }

    @Transactional
    public AttachmentPdf save(AttachmentPdf attachmentPdf) {

        attachmentPdfMapper.insert(attachmentPdf);
        return attachmentPdf;
    }
}

package com.hand.hcf.app.base.attachment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.attachment.domain.AttachmentPdf;

import java.util.List;

public interface AttachmentPdfMapper extends BaseMapper<AttachmentPdf> {

    List<AttachmentPdf> findByEntityOid(String entityOid);

}

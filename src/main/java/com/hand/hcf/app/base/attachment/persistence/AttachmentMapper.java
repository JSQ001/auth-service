package com.hand.hcf.app.base.attachment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.attachment.domain.Attachment;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.UUID;

public interface AttachmentMapper extends BaseMapper<Attachment> {

    Attachment findByAttachmentOid(@Param("attachmentOid") String attachmentOid);

    Collection<Attachment> findByAttachmentOidIn(@Param("attachmentOids") Collection<UUID> attachmentOids);

}

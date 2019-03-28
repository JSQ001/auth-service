package com.hand.hcf.app.base.attachment;

import com.hand.hcf.app.base.attachment.enums.MediaType;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;


/**
 * A DTO for the Attachment entity.
 */
@Data
public class AttachmentCO implements Serializable {

    private Long id;

    private UUID attachmentOid;

    private String fileName;

    private MediaType fileType;

    private String fileUrl;

    private String link;

    private String thumbnailUrl;

    private String iconUrl;

    private Long size;



}

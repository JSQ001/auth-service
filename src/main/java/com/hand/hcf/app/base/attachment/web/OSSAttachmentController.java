package com.hand.hcf.app.base.attachment.web;

import com.hand.hcf.app.base.attachment.AttachmentService;
import com.hand.hcf.app.base.attachment.OSSAttachmentService;
import com.hand.hcf.app.base.attachment.domain.Attachment;
import com.hand.hcf.app.base.attachment.enums.AttachmentType;
import com.hand.hcf.app.base.util.HeaderUtil;
import com.hand.hcf.app.common.co.AttachmentCO;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * OSS 文件上传controller
 *
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @date 2019/4/29 10:26
 */
@RestController
@RequestMapping("/api/oss")
public class OSSAttachmentController {

    private final Logger log = LoggerFactory.getLogger(OSSAttachmentController.class);

    @Autowired
    private OSSAttachmentService attachmentService;

    @RequestMapping(value = "/upload/static/attachment", method = RequestMethod.POST)
    public ResponseEntity<AttachmentCO> uploadStaticAttachment(@RequestParam MultipartFile file,
                                                                @RequestParam AttachmentType attachmentType) {
        if (AttachmentType.EXPENSE_ICON.equals(attachmentType)) {
            Attachment attachment = attachmentService.uploadStatic(file, attachmentType);
            // expenseTypeIconService.createExpenseTypeIcon(attachment);暂时注释
            return ResponseEntity.ok(attachmentService.AttachmentToAttachmentCO(attachment));
        }
        return ResponseEntity.ok(attachmentService.uploadStaticFile(file, attachmentType));
    }


}

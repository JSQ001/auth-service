package com.hand.hcf.app.base.attachment.web;

import com.hand.hcf.app.base.attachment.AttachmentService;
import com.hand.hcf.app.base.attachment.domain.Attachment;
import com.hand.hcf.app.base.attachment.service.IAttachment;
import com.hand.hcf.app.base.util.HeaderUtil;
import com.hand.hcf.app.common.co.AttachmentCO;
import io.micrometer.core.annotation.Timed;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @Auther: chenzhipeng
 * @Date: 2019/1/3 09:09
 */
@RestController
@RequestMapping("/api")
public class AttachmentController {

    private final Logger log = LoggerFactory.getLogger(AttachmentController.class);

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private IAttachment attachmentImpl;

    /**
     * DELETE  /attachments/:id -> delete the "id" attachment.
     */
    @DeleteMapping("/attachments/{oid}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable String oid) {
        log.debug("REST request to delete Attachment : {}", oid);
        //删除文件 antfin cx
        attachmentService.deleteOssFile(oid);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("attachment", oid)).build();
    }

    @RequestMapping(value = "/upload/attachment", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<AttachmentCO> uploadPicture(@RequestParam(name = "file") MultipartFile file,
                                                      @RequestParam(name = "attachmentType") String attachmentType,
                                                      @RequestParam(name = "pkValue", required = false) String pkValue
    ) {
        return ResponseEntity.ok(attachmentService.uploadFile(file, attachmentType, pkValue));
    }

    @RequestMapping(value = "/upload/attachment/batch", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<List<AttachmentCO>> uploadPictureBatch(
            @RequestParam(name = "files") List<MultipartFile> files,
            @RequestParam(name = "attachmentType") String attachmentType,
            @RequestParam(name = "pkValue", required = false) String pkValue) {
        List<AttachmentCO> batch = new ArrayList<>();
        for (MultipartFile file : files){
            batch.add(attachmentService.uploadFile(file, attachmentType, pkValue));
        }
        return ResponseEntity.ok(batch);
    }

    //上传文件
    @RequestMapping(value = "/upload/static/attachment", method = RequestMethod.POST)
    public ResponseEntity<AttachmentCO> uploadStaticAttachment(
            @RequestParam MultipartFile file,
            @RequestParam String attachmentType,
            @RequestParam(name = "pkValue", required = false) String pkValue,
            HttpServletRequest request) {
        return ResponseEntity.ok(attachmentService.uploadStaticFile(file, attachmentType, pkValue));
    }

    @RequestMapping(value = "/upload/static/attachment/batch", method = RequestMethod.POST)
    public ResponseEntity<List<AttachmentCO>> batchUploadStaticAttachment(
            @RequestParam MultipartFile[] file,
            @RequestParam String attachmentType,
            @RequestParam(name = "pkValue", required = false) String pkValue) {
        List<AttachmentCO> attachmentDTOs = new ArrayList<>();
        Arrays.stream(file).forEach(
                f -> attachmentDTOs.add(attachmentService.attachmentToAttachmentCO(
                        attachmentService.uploadStatic(f, attachmentType, pkValue))));
        return ResponseEntity.ok(attachmentDTOs);
    }

    @PostMapping("/public/upload/static/attachment")
    public ResponseEntity uploadPublicStaticAttachment(
            @RequestParam MultipartFile file,
            @RequestParam String attachmentType,
            @RequestParam(name = "pkValue", required = false) String pkValue) {
        return ResponseEntity.ok(attachmentService.uploadStatic(file, attachmentType, pkValue));
    }

    @GetMapping(value = "/attachments/{attachmentOid}")
    public ResponseEntity<AttachmentCO> getAttachmentByOid(@PathVariable UUID attachmentOid) {
        AttachmentCO attachmentCO = attachmentService
                .attachmentToAttachmentCO(attachmentService.findOneByAttachmentOid(attachmentOid));
        return Optional.ofNullable(attachmentCO)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    //@GetMapping("/attachments/download/{oid}")
    public void downLoad(@PathVariable String oid,
                         HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse) throws Exception {
        Attachment attachment = attachmentService.findByOId(oid);
        String name = attachment.getName();
        String userAgent = httpServletRequest.getHeader("User-Agent");
        httpServletResponse.reset();
        if (userAgent.contains("Firefox")) {
            name = new String(name.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
        } else {
            name = URLEncoder.encode(name, StandardCharsets.UTF_8.name());
        }
        httpServletResponse.addHeader("Content-Disposition",
                "attachment; filename=" + name);
        httpServletResponse.setContentType( "application/octet-stream;charset=" + StandardCharsets.UTF_8.name());
        httpServletResponse.setHeader("Accept-Ranges", "bytes");
        attachmentService.writeFileToResp(httpServletResponse, attachment);
    }

    //下载文件
    @RequestMapping(value = "/attachments/download/{oid}", method = RequestMethod.GET)
    public void downLoadAttachment(@PathVariable String oid,
                                   HttpServletRequest httpServletRequest,
                                   HttpServletResponse httpServletResponse) throws IOException{
            Attachment attachment = attachmentService.findByOId(oid);
            String objectName = attachment.getPath();
            if(StringUtils.isNotBlank(objectName)) {
                attachmentImpl.downLoadFile(httpServletRequest,httpServletResponse,objectName);
            } else{
                return;
            }
    }
}

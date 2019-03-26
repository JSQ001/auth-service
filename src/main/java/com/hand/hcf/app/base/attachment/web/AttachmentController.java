package com.hand.hcf.app.base.attachment.web;

import com.hand.hcf.app.base.attachment.AttachmentService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.*;

/**
 * @Auther: chenzhipeng
 * @Date: 2019/1/3 09:09
 */
@RestController
@RequestMapping("/api")
public class AttachmentController{

    private final Logger log = LoggerFactory.getLogger(AttachmentController.class);

    @Autowired
    private AttachmentService attachmentService;

    /**
     * DELETE  /attachments/:id -> delete the "id" attachment.
     */
    @RequestMapping(value = "/attachments/{oid}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAttachment(@PathVariable String oid) {
        log.debug("REST request to delete Attachment : {}", oid);
        attachmentService.delete(oid);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("attachment", oid)).build();
    }

    // upload picture
    @RequestMapping(value = "/upload/attachment", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<AttachmentCO> uploadPicture(@RequestParam(name = "file") MultipartFile file,
                                                      @RequestParam(name = "attachmentType") AttachmentType attachmentType
    ) {
        return ResponseEntity.ok(attachmentService.uploadFile(file, attachmentType));
    }

    @RequestMapping(value = "/upload/attachment/batch", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<List<AttachmentCO>> uploadPictureBatch(@RequestParam(name = "files") List<MultipartFile> files,
                                                                  @RequestParam(name = "attachmentType") AttachmentType attachmentType) {
//        if (file.getSizes() > artemisProperties.getFileUploadMaxSize()) {
//            throw new ValidationException(new ValidationError("file", "file is not allowed bigger than 2Mb"));
//        }
        //return ResponseEntity.ok(attachmentService.uploadFile(file, attachmentType, SecurityUtils.getCurrentUser().getCompany().getCompanyOID()));
        List<AttachmentCO> batch = new ArrayList<>();
        for (MultipartFile file : files){
            batch.add(attachmentService.uploadFile(file, attachmentType));
        }
        return ResponseEntity.ok(batch);
    }


    // upload picture async
    @RequestMapping(value = "/upload/attachment/async", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<AttachmentCO> uploadPictureAsync(@RequestParam(name = "file") MultipartFile file,
                                                            @RequestParam(name = "attachmentType") AttachmentType attachmentType) {
//        if (file.getSizes() > artemisProperties.getFileUploadMaxSize()) {
//            throw new ValidationException(new ValidationError("file", "file is not allowed bigger than 2Mb"));
//        }
        return ResponseEntity.ok(attachmentService.uploadFileAsync(file, attachmentType));
    }

    @RequestMapping(value = "/upload/static/attachment", method = RequestMethod.POST)
    public ResponseEntity<AttachmentCO> uploadStaticAttachment(@RequestParam MultipartFile file,
                                                                @RequestParam AttachmentType attachmentType) {
        if (AttachmentType.EXPENSE_ICON.equals(attachmentType)) {
            Attachment attachment = attachmentService.uploadStatic(file, attachmentType);
//            expenseTypeIconService.createExpenseTypeIcon(attachment);暂时注释
            return ResponseEntity.ok(attachmentService.AttachmentToAttachmentCO(attachment));
        }
        return ResponseEntity.ok(attachmentService.uploadStaticFile(file, attachmentType));
    }

    @RequestMapping(value = "/upload/static/attachment/batch", method = RequestMethod.POST)
    public ResponseEntity<List<AttachmentCO>> batchUploadStaticAttachment(@RequestParam MultipartFile[] file,
                                                                           @RequestParam AttachmentType attachmentType) {
        List<AttachmentCO> attachmentDTOs = new ArrayList<>();
        if (AttachmentType.EXPENSE_ICON.equals(attachmentType)) {
            Arrays.stream(file).forEach(f -> {
                Attachment attachment = attachmentService.uploadStatic(f, attachmentType);
//                expenseTypeIconService.createExpenseTypeIcon(attachment);暂时注释
                attachmentDTOs.add(attachmentService.AttachmentToAttachmentCO(attachment));
            });

//            for (MultipartFile f : file) {
//                Attachment attachment = attachmentService.uploadStatic(f, attachmentType, SecurityUtils.getCurrentUser().getCompany().getCompanyOID());
//                expenseTypeIconService.createExpenseTypeIcon(attachment);
//                attachmentDTOs.add(attachmentService.adapterAttachmentDTO(attachment));
//            }
        } else {
            Arrays.stream(file).forEach(
                    f -> {
                        attachmentDTOs.add(attachmentService.AttachmentToAttachmentCO(attachmentService.uploadStatic(f, attachmentType)));
                    }
            );
        }
        return ResponseEntity.ok(attachmentDTOs);
    }

    @RequestMapping(value = "/public/upload/static/attachment", method = RequestMethod.POST)
    public ResponseEntity<String> uploadPublicStaticAttachment(@RequestParam(required = false) UUID companyOID,
                                                               @RequestParam MultipartFile file,
                                                               @RequestParam AttachmentType attachmentType) {
        return ResponseEntity.ok(attachmentService.uploadPublicStaticAttachment( attachmentType, file));
    }

    @RequestMapping(value = "/attachments/{attachmentOid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AttachmentCO> getExpenseReport(@PathVariable UUID attachmentOid) {
        AttachmentCO attachmentCO = attachmentService.AttachmentToAttachmentCO(attachmentService.findOneByAttachmentOid(attachmentOid));
        return Optional.ofNullable(attachmentCO)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/attachments/download/{oid}", method = RequestMethod.GET)
    @Timed
    public void getExpenseReport(@PathVariable String oid, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String ENC = "UTF-8";
        Attachment attachment = attachmentService.findByOId(oid);

        response.addHeader("ContentList-Disposition", "attachment;filename=\"" + URLEncoder.encode(attachment.getName(), ENC) + "\"");
        response.setContentType( "application/octet-stream;charset=" + ENC);
        response.setHeader("Accept-Ranges", "bytes");
        attachmentService.writeFileToResp(response, attachment);
    }

}

package com.hand.hcf.app.base.attachment.service.impl;

import com.hand.hcf.app.base.attachment.AttachmentService;
import com.hand.hcf.app.base.attachment.constant.Constants;
import com.hand.hcf.app.base.attachment.domain.Attachment;
import com.hand.hcf.app.base.attachment.enums.AttachmentType;
import com.hand.hcf.app.base.attachment.service.IOSSAttachmentService;
import com.hand.hcf.app.base.attachment.util.OSSFileUpload;
import com.hand.hcf.app.base.config.FtpConfiguration;
import com.hand.hcf.app.base.config.HcfBaseProperties;
import com.hand.hcf.app.base.config.HcfOssProperties;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.common.enums.MediaType;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class OSSAttachmentServiceImpl implements IOSSAttachmentService {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    HcfOssProperties hcfOssProperties;

    private HcfBaseProperties hcfBaseProperties;

    public OSSAttachmentServiceImpl(HcfBaseProperties hcfBaseProperties){
        this.hcfBaseProperties = hcfBaseProperties;
    }

    @Override
    public AttachmentCO uploadStaticFile(MultipartFile file, AttachmentType attachmentType) {
        return adapterAttachmentCO(uploadStatic(file, attachmentType));
    }

    @Override
    public AttachmentCO adapterAttachmentCO(Attachment attachment) {
        if (attachment == null) {
            return null;
        }
        AttachmentCO AttachmentCO = new AttachmentCO();
        AttachmentCO.setFileUrl(attachment.getPath());
        AttachmentCO.setId(attachment.getId());
        AttachmentCO.setThumbnailUrl(attachment.getThumbnailPath());
        AttachmentCO.setSize(attachment.getSizes());
        AttachmentCO.setAttachmentOid(attachment.getAttachmentOid());
        AttachmentCO.setFileName(attachment.getName());
        AttachmentCO.setFileType(MediaType.parse(attachment.getMediaTypeID()));
        return AttachmentCO;
    }

    @Override
    public Attachment uploadStatic(MultipartFile file, AttachmentType attachmentType) {
        Attachment attachment = new Attachment();
        try {
            UUID attachmentOid = UUID.randomUUID();
            String originalFilename = file.getOriginalFilename();
            String path = generatePath(attachmentOid, attachmentType, originalFilename, null);
            String filePath;
            try {
                filePath = OSSFileUpload.uploadByByte(hcfOssProperties,file.getBytes(),originalFilename);
            } catch (Exception e) {
                log.error("upload attachment error:" + e.getMessage());
                throw new ValidationException(new ValidationError("attachment", "file upload failed"));
            }
            attachment.setAttachmentOid(attachmentOid);
            attachment.setName(originalFilename);
            attachment.setMediaTypeID(MediaType.getMediaTypeByFileName(originalFilename).getId());
            attachment.setPath(path);
            attachment.setThumbnailPath(getTempUrl(attachment));
            attachment.setAbsolutePath(filePath);
            attachment.setSizes(file.getSize());
            attachment.setPublicFlag(true);
            attachmentService.insertOrUpdate(attachment);
            return attachment;
        } catch (Exception e) {
            throw e;
        }
    }

    private String generatePath(UUID attachmentOid, AttachmentType attachmentType, String filename, String thumb) {
        String path;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String suffix = "." + filename.substring(filename.lastIndexOf(".") + 1);
        String date = "/" + sdf.format(new Date()) + "/";
        String rootPath = hcfBaseProperties.getStorage().getFtp().getDirectoryName();
        switch (attachmentType) {
            case INVOICE_IMAGES:
                path =  rootPath + "/" + "invoices/"  + date + DigestUtils.md5Hex(attachmentOid.toString() + (thumb != null ? thumb : "-") + filename) + suffix;
                break;
            case IMAGE_INVOICE_IMAGES:
                path = rootPath + "/" + "invoice_images/"  + date + DigestUtils.md5Hex(attachmentOid.toString() + (thumb != null ? thumb : "-") + filename) + suffix;
                break;
            case FEEDBACK_IMAGES:
                path = rootPath + "/" + "feedback" + date + DigestUtils.md5Hex(attachmentOid.toString() + (thumb != null ? thumb : "-") + filename) + suffix;
                break;
            case HEAD_PORTRAIT:
                path = rootPath + "/" + "headPortrait" + date + DigestUtils.md5Hex(attachmentOid.toString() + (thumb != null ? thumb : "-") + filename) + suffix;
                break;
            case CARROUSEL_IMAGES:
                path = rootPath + "/" + "carrousel/"  + date + DigestUtils.md5Hex(attachmentOid.toString() + (thumb != null ? thumb : "-") + filename) + suffix;
                break;
            case PDF:
                path = rootPath + "/" + "pdf/"  + date + DigestUtils.md5Hex(filename + UUID.randomUUID().toString().replaceAll("-", "")) + suffix;
                break;
            case REPAYMENT_IMAGES:
                path = rootPath + "/" + "repayment/"  + date + DigestUtils.md5Hex(attachmentOid.toString() + (thumb != null ? thumb : "-") + filename) + suffix;
                break;
            case COMPANY_LOGO:
                path = rootPath + "/" + Constants.OSS_COMPANY_LOGO_FOLDER + DigestUtils.md5Hex(filename) + suffix;
                break;
            case EXPENSE_ICON:
                path = rootPath + "/" + "expenseIcon/"  + date + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename) + suffix;
                break;
            case BUDGET_JOURNAL:
                path =  rootPath + "/" + "budget/"  + date + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename) + suffix;
                break;
            case CONTRACT:
                path = rootPath + "/" + "contract/"  + date + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename) + suffix;
                break;
            case PREPAYMENT:
                path = rootPath + "/" + "prepayment/"  + date + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename) + suffix;
                break;
            case EXP_REPORT:
                path = rootPath + "/" + "report/"  + date + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename) + suffix;
                break;
            case APPLICATION_ICON:
                path = rootPath + "/" + "application" + date + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename) + suffix;
                break;
            case SKIN_PACKAGE:
                path = rootPath + "/" + "skin" + date + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename) + suffix;
                break;
            case EXP_ADJUST:
                path = rootPath + "/exp_adjust" +date + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename) + suffix;
                break;
            case PAYMENT:
                path = rootPath + "/payment" +date + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename) + suffix;
                break;
            case CASH_WRITE_OFF:
                path = rootPath + "/cash_write_off" +date + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename) + suffix;
                break;
            case GL_WORK_ORDER:
                path = rootPath + "/gl_work_order" +date + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename) + suffix;
                break;
            case EXP_REVERSE:
                path = rootPath+"/exp_reverse" + date + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename)+suffix;
                break;
            case FUND:
                path = rootPath+"/fund" + date + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename)+suffix;
                break;
            case TAX:
                path = rootPath+"/tax" + date + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename)+suffix;
                break;
            case SUPPLIER:
                path = rootPath+"/supplier" + date + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename)+suffix;
                break;
            case OTHER:
                path = rootPath + "/" + "other/"  + date + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename) + suffix;
                break;
            default:
                throw new IllegalArgumentException("unrecognised attachment type");
        }
        return path;
    }

    public String getTempUrl(Attachment attachment) {
        return hcfBaseProperties.getStorage().getFtp().getStaticUrl() + attachment.getPath();
    }

}

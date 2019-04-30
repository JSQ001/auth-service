package com.hand.hcf.app.base.attachment.service.impl;

import com.hand.hcf.app.base.attachment.AttachmentService;
import com.hand.hcf.app.base.attachment.constant.Constants;
import com.hand.hcf.app.base.attachment.domain.Attachment;
import com.hand.hcf.app.base.attachment.enums.AttachmentType;
import com.hand.hcf.app.base.attachment.service.IAttachment;
import com.hand.hcf.app.base.config.FtpConfiguration;
import com.hand.hcf.app.base.config.HcfBaseProperties;
import com.hand.hcf.app.base.config.OssConfiguration;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.common.enums.MediaType;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FTPAttachmentService implements IAttachment {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    AttachmentService attachmentService;

    private HcfBaseProperties hcfBaseProperties;

    public FTPAttachmentService(HcfBaseProperties hcfBaseProperties){
        this.hcfBaseProperties = hcfBaseProperties;
    }

    /**
     * @Description: 上传
     * @param: path 上传路径
     * @param: bytes
     */
    @Override
    public String upload(String path, byte[] bytes) {
        String filePath = null;

        if ("FTP".equals(hcfBaseProperties.getStorage().getMode())) {
            FtpConfiguration.SFTPUtil sftpUtil = applicationContext.getBean(FtpConfiguration.SFTPUtil.class);
            //连接登录sftp
            sftpUtil.login();
            try {
                filePath = sftpUtil.upload(path, bytes);

            } catch (SftpException e) {
                throw new BizException("file upload failed.","file upload failed. " + e.getMessage());
            }finally {
                sftpUtil.logout();
            }
        } else if ("OSS".equals(hcfBaseProperties.getStorage().getMode())) {
            OssConfiguration.OSSUtil ossUtil = applicationContext.getBean(OssConfiguration.OSSUtil.class);
            filePath = ossUtil.upload(path, bytes);
        }
        return filePath;
    }


    @Override
    public Attachment upload(MultipartFile file, AttachmentType attachmentType) {
        Attachment attachment = new Attachment();
        try {
            UUID attachmentOid = UUID.randomUUID();
            String path = generatePath(attachmentOid, attachmentType, file.getOriginalFilename(), null);
            String filePath;
            try {
                filePath = upload(path, file.getBytes());
            } catch (Exception e) {
                throw new ValidationException(new ValidationError("attachment", "file upload failed"));
            }
            attachment.setAttachmentOid(attachmentOid);
            attachment.setName(file.getOriginalFilename());
            attachment.setMediaTypeID(MediaType.getMediaTypeByFileName(file.getOriginalFilename()).getId());
            attachment.setPath(path);
            attachment.setThumbnailPath(getTempUrl(attachment));
            attachment.setAbsolutePath(filePath);
            attachment.setSizes(file.getSize());
            attachment.setCreatedDate(ZonedDateTime.now());
            attachment.setCreatedBy(LoginInformationUtil.getCurrentUserId());
            attachmentService.insertOrUpdate(attachment);
            return attachment;
        } catch (Exception e) {
            throw e;
        }
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
    public AttachmentCO uploadFile(MultipartFile file, AttachmentType attachmentType) {
        Attachment attachment = new Attachment();
        try {
            UUID attachmentOid = UUID.randomUUID();
            String path = generatePath(attachmentOid, attachmentType, file.getOriginalFilename(), null);
            String filePath;
            try {
                filePath = upload(path, file.getBytes());
            } catch (Exception e) {
                log.error("upload attachment error:", e);
                throw new ValidationException(new ValidationError("attachment", "file upload failed"));
            }
            attachment.setAttachmentOid(attachmentOid);
            attachment.setName(file.getOriginalFilename());
            attachment.setMediaTypeID(MediaType.getMediaTypeByFileName(file.getOriginalFilename()).getId());
            attachment.setPath(path);
            attachment.setThumbnailPath(getTempUrl(attachment));
            attachment.setAbsolutePath(filePath);
            attachment.setSizes(file.getSize());
            attachmentService.insertOrUpdate(attachment);
            return this.adapterAttachmentCO(attachment);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public AttachmentCO uploadFileAsync(MultipartFile file, AttachmentType attachmentType) {
        return null;
    }

    @Override
    public AttachmentCO uploadFile(byte[] content, String fileName, AttachmentType attachmentType, UUID userOid) {
        Attachment attachment = new Attachment();
        try {
            UUID attachmentOid = UUID.randomUUID();
            String path = generatePath(attachmentOid, attachmentType, fileName, null);
            String filePath;
            try {
                filePath = upload(path, content);
            } catch (Exception e) {
                log.error("upload attachment error:" + e.getMessage());
                throw new ValidationException(new ValidationError("attachment", "file upload failed"));
            }
            attachment.setAttachmentOid(attachmentOid);
            attachment.setName(fileName);
            attachment.setMediaTypeID(MediaType.getMediaTypeByFileName(fileName).getId());
            attachment.setPath(path);
            attachment.setThumbnailPath(getTempUrl(attachment));
            attachment.setAbsolutePath(filePath);
            attachment.setSizes(0L);
            attachmentService.insertOrUpdate(attachment);
            return adapterAttachmentCO(attachment);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public String uploadStaticFile(String path, byte[] bytes) {
        return null;
    }


    //oss修改
    @Override
    public Attachment uploadStatic(MultipartFile file, AttachmentType attachmentType) {
        Attachment attachment = new Attachment();
        try {
            UUID attachmentOid = UUID.randomUUID();
            String originalFilename = file.getOriginalFilename();
            originalFilename = originalFilename.replace(" ","_");
            String path = generatePath(attachmentOid, attachmentType, originalFilename, null);
            String tempUrl;
            try {
                tempUrl = upload(path, file.getBytes());

            } catch (Exception e) {
                log.error("upload attachment error:" + e.getMessage());
                throw new ValidationException(new ValidationError("attachment", "file upload failed"));
            }
            attachment.setAttachmentOid(attachmentOid);
            attachment.setName(originalFilename);
            attachment.setMediaTypeID(MediaType.getMediaTypeByFileName(originalFilename).getId());
            attachment.setPath(path);
            attachment.setThumbnailPath(getTempUrl(attachment));
            attachment.setAbsolutePath(tempUrl);
            attachment.setSizes(file.getSize());
            attachment.setPublicFlag(true);
            attachmentService.insertOrUpdate(attachment);
            return attachment;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Attachment uploadStatic(MultipartFile file, AttachmentType attachmentType, String fileName) {
        return null;
    }


    @Override
    public Attachment uploadStatic(byte[] content, AttachmentType attachmentType, String filename, Long length) {
        Attachment attachment = new Attachment();
        try {
            UUID attachmentOid = UUID.randomUUID();
            String path = generatePath(attachmentOid, attachmentType, filename, null);
            String tempUrl;
            try {
                tempUrl = upload(path, content);
            } catch (Exception e) {
                log.error("upload attachment error:" + e.getMessage());
                throw new ValidationException(new ValidationError("attachment", "file upload failed"));
            }
            attachment.setAttachmentOid(attachmentOid);
            attachment.setName(filename);
            attachment.setMediaTypeID(MediaType.getMediaTypeByFileName(filename).getId());
            attachment.setPath(path);
            attachment.setThumbnailPath(getTempUrl(attachment));
            attachment.setAbsolutePath(tempUrl);
            attachment.setSizes(length);// yuvia todo head
            attachment.setPublicFlag(true);
            attachmentService.insertOrUpdate(attachment);
            return attachment;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public AttachmentCO uploadStaticFile(MultipartFile file, AttachmentType attachmentType) {
        return adapterAttachmentCO(uploadStatic(file, attachmentType));
    }

    @Override
    public AttachmentCO uploadStaticFile(MultipartFile file, AttachmentType attachmentType, String fileName) {
        return null;
    }

    @Override
    public AttachmentCO uploadStaticFile(byte[] content, AttachmentType attachmentType, String filename, Long length) {
        return adapterAttachmentCO(uploadStatic(content, attachmentType, filename,length));
    }

    @Override
    public AttachmentCO uploadTempFile(byte[] content, String fileName, AttachmentType attachmentType, UUID userOid) {
        return null;
    }

    @Override
    public Attachment uploadTempStatic(byte[] content, String fileName, AttachmentType attachmentType, UUID userOid) {
        return null;
    }

    @Override
    public String uploadPublicStaticAttachment(AttachmentType attachmentType, MultipartFile file) {
        String path = generatePath(UUID.randomUUID(), attachmentType, file.getOriginalFilename(), null);
        String tempUrl;
        try {
            tempUrl = uploadStaticFile(path, file.getBytes());
        } catch (Exception e) {
            log.error("upload attachment error:" + e.getMessage());
            throw new RuntimeException("upload public file upload failed");
        }
        return tempUrl;
    }


    @Override
    public void removeFile(List<Attachment> attachments) {
        delete(attachments);
    }

    @Override
    public void removeFile(boolean isPublic, String path) {
        delete(isPublic, path);
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

    public String getTempIconUrl(Attachment attachment) {
        return getTempUrl(attachment);
    }

    public String getTempThumbnailUrl(Attachment attachment) {
        return getTempUrl(attachment);
    }

    public String getTempUrl(Attachment attachment) {
        return hcfBaseProperties.getStorage().getFtp().getStaticUrl() + attachment.getPath();
    }

    /**
     * @Description: 删除
     * @param: key 删除路径
     * @param: getPublicFlag
     */
    protected void delete(boolean isPublic, String key) {
        FtpConfiguration.SFTPUtil sftpUtil = applicationContext.getBean(FtpConfiguration.SFTPUtil.class);
        //连接登录sftp
        sftpUtil.login();
        try {
            sftpUtil.remove(key);
        } catch (SftpException e) {
            throw new BizException("file delete failed.", "file delete failed. " + e.getMessage());
        }finally {
            sftpUtil.logout();
        }
    }


    protected void delete(List<Attachment> attachments) {
        FtpConfiguration.SFTPUtil sftpUtil = applicationContext.getBean(FtpConfiguration.SFTPUtil.class);
        //连接登录sftp
        sftpUtil.login();
        try {
            attachments.stream().forEach( u -> {
                if (StringUtils.hasText(u.getAbsolutePath())) {
                    try {
                        sftpUtil.remove(u.getAbsolutePath());
                    } catch (SftpException e) {
                        throw new BizException("file delete failed.", "file delete failed. " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            throw new BizException("file delete failed.", "file delete failed. " + e.getMessage());
        } finally {
            sftpUtil.logout();
        }

    }
    @Override
    public void writeFileToResp(HttpServletResponse response, Attachment attachment) {
        InputStream inputStream = null;
        FtpConfiguration.SFTPUtil sftpUtil = applicationContext.getBean(FtpConfiguration.SFTPUtil.class);
        try {
            sftpUtil.login();
            inputStream = sftpUtil.getInputStream(attachment.getAbsolutePath());
            byte[] buf = new byte[1024 * 10];
            ServletOutputStream outputStream = response.getOutputStream();
            int readLength;
            while (((readLength = inputStream.read(buf)) != -1)) {
                outputStream.write(buf, 0, readLength);
            }
            outputStream.flush();
        } catch (SftpException e) {
            throw new BizException("sftp exception","sftp exception " + e.getMessage());
        } catch (IOException e) {
            throw new BizException("io exception","io exception " + e.getMessage());
        }finally {
            sftpUtil.logout();
            try {
                if(inputStream!=null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

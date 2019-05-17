package com.hand.hcf.app.base.attachment.service.impl;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.base.attachment.AttachmentService;
import com.hand.hcf.app.base.attachment.domain.Attachment;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
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

    public FTPAttachmentService(HcfBaseProperties hcfBaseProperties) {
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
                throw new BizException("file upload failed.", "file upload failed. " + e.getMessage());
            } finally {
                sftpUtil.logout();
            }
        } else if ("OSS".equals(hcfBaseProperties.getStorage().getMode())) {
            OssConfiguration.OSSUtil ossUtil = applicationContext.getBean(OssConfiguration.OSSUtil.class);
            filePath = ossUtil.upload(path, bytes);
        }
        return filePath;
    }


    @Override
    public Attachment upload(MultipartFile file, String attachmentType, String pkValue) {
        Attachment attachment = new Attachment();
        try {
            UUID attachmentOid = UUID.randomUUID();
            String path = generatePath(attachmentOid, attachmentType, file.getOriginalFilename() == null
                    ? UUID.randomUUID().toString() : file.getOriginalFilename(), null);
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
            attachment.setPkName(attachmentType);
            attachment.setPkValue(pkValue);
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
        AttachmentCO attachmentCO = new AttachmentCO();
        attachmentCO.setFileUrl(attachment.getPath());
        attachmentCO.setId(attachment.getId());
        attachmentCO.setThumbnailUrl(attachment.getThumbnailPath());
        attachmentCO.setSize(attachment.getSizes());
        attachmentCO.setAttachmentOid(attachment.getAttachmentOid());
        attachmentCO.setFileName(attachment.getName());
        attachmentCO.setFileType(MediaType.parse(attachment.getMediaTypeID()));
        attachmentCO.setPkName(attachment.getPkName());
        attachmentCO.setPkValue(attachment.getPkValue());
        return attachmentCO;
    }

    @Override
    public AttachmentCO uploadFile(MultipartFile file, String attachmentType, String pkValue) {
        Attachment attachment = new Attachment();
        try {
            UUID attachmentOid = UUID.randomUUID();
            String path = generatePath(attachmentOid, attachmentType, file.getOriginalFilename() == null
                    ? attachmentOid.toString() : file.getOriginalFilename(), null);
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
            attachment.setPkName(attachmentType);
            attachment.setPkValue(pkValue);
            attachmentService.insertOrUpdate(attachment);
            return this.adapterAttachmentCO(attachment);
        } catch (Exception e) {
            throw e;
        }
    }




    @Override
    public Attachment uploadStatic(MultipartFile file, String attachmentType, String pkValue) {
        Attachment attachment = new Attachment();
        try {
            UUID attachmentOid = UUID.randomUUID();
            String originalFilename = file.getOriginalFilename();
            if (StringUtils.hasText(originalFilename)) {
                originalFilename = originalFilename.replace(" ", "_");
            } else {
                originalFilename = UUID.randomUUID().toString();
            }
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
            attachment.setPkValue(pkValue);
            attachment.setPkName(attachmentType);
            attachmentService.insertOrUpdate(attachment);
            return attachment;
        } catch (Exception e) {
            throw e;
        }
    }


    @Override
    public AttachmentCO uploadStaticFile(MultipartFile file, String attachmentType, String pkValue) {
        return adapterAttachmentCO(uploadStatic(file, attachmentType, pkValue));
    }


    @Override
    public void removeFile(List<Attachment> attachments) {
        if (!CollectionUtils.isEmpty(attachments)) {
            delete(attachments);
        }
    }

    @Override
    public void removeFile(boolean isPublic, String path) {
        delete(isPublic, path);
    }

    private String generatePath(UUID attachmentOid, String attachmentType, String filename, String thumb) {
        String path;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String suffix = "." + filename.substring(filename.lastIndexOf(".") + 1);
        String date = "/" + sdf.format(new Date()) + "/";
        String rootPath = hcfBaseProperties.getStorage().getFtp().getDirectoryName();
        path = rootPath + "/" + attachmentType.toLowerCase()   + date
                + DigestUtils.md5Hex(attachmentOid.toString() + "-" + filename) + suffix;
        return path;
    }

    public String getTempIconUrl(Attachment attachment) {
        return getTempUrl(attachment);
    }

    public String getTempThumbnailUrl(Attachment attachment) {
        return getTempUrl(attachment);
    }

    public String getTempUrl(Attachment attachment) {
        String thumbnailPath = null;
        if ("FTP".equals(hcfBaseProperties.getStorage().getMode())) {
            thumbnailPath = hcfBaseProperties.getStorage().getFtp().getStaticUrl() + attachment.getPath();
        } else if ("OSS".equals(hcfBaseProperties.getStorage().getMode())) {
            String endpoint = hcfBaseProperties.getStorage().getOss().getEndpoint();
            String bucketName = hcfBaseProperties.getStorage().getOss().getBucket().getName();
            String filehost = hcfBaseProperties.getStorage().getOss().getFilehost();
            String frontUrl = "https://" + bucketName + "." + endpoint + "/" + filehost;
            thumbnailPath = frontUrl + "/" + attachment.getPath();
        }
        return thumbnailPath;
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
            } finally {
                sftpUtil.logout();
            }
    }


    protected void delete(List<Attachment> attachments) {
        FtpConfiguration.SFTPUtil sftpUtil = applicationContext.getBean(FtpConfiguration.SFTPUtil.class);
        //连接登录sftp
        sftpUtil.login();
        try {
            attachments.forEach( u -> {
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
            throw new BizException("sftp exception", "sftp exception " + e.getMessage());
        } catch (IOException e) {
            throw new BizException("io exception", "io exception " + e.getMessage());
        } finally {
            sftpUtil.logout();
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //下载附件的方法
    @Override
    public void downLoadFile(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse,
                             String objectName,
                             String originName) throws IOException {
        OssConfiguration.OSSUtil ossUtil = applicationContext.getBean(OssConfiguration.OSSUtil.class);
        ossUtil.downLoad(httpServletRequest, httpServletResponse, objectName,originName);
    }

    //删除Oss文件的方法
    @Override
    public void deleteOssFile(String path) {
        OssConfiguration.OSSUtil ossUtil = applicationContext.getBean(OssConfiguration.OSSUtil.class);
        ossUtil.deleteOssFile(path);
    }

}

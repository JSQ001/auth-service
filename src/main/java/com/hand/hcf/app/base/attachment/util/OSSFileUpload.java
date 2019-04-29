package com.hand.hcf.app.base.attachment.util;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.hand.hcf.app.base.config.HcfOssProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @date 2019/4/29 11:33
 */
public class OSSFileUpload {
    private final static Logger log = LoggerFactory.getLogger(OSSFileUpload.class);

    //以字节流的形式上传
    public static String uploadByByte(HcfOssProperties hcfOssProperties, byte[] bytes, String fileName) {
        String endpoint = hcfOssProperties.getEndpoint();

        String accessKeyId = hcfOssProperties.getAccessKeyId();

        String accessKeySecret = hcfOssProperties.getAccessKeySecret();

        String bucketName = hcfOssProperties.getBucketName();

        String fileHost = hcfOssProperties.getFileHost();
        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            //容器不存在，就创建
            if (!ossClient.doesBucketExist(bucketName)) {
                ossClient.createBucket(bucketName);
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                ossClient.createBucket(createBucketRequest);
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = format.format(new Date());
            //创建文件路径
            String fileUrl = fileHost + "/" + dateStr+ "/" +  fileName;

            //上传文件
            PutObjectResult result = ossClient.putObject(new PutObjectRequest(bucketName, fileUrl, new ByteArrayInputStream(bytes)));
            //ossClient.putObject(bucketName, fileName, new ByteArrayInputStream(bytes));
            //设置权限 这里是公开读
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
            if (null != result) {
                log.info("==========>OSS文件上传成功,OSS地址：" + fileUrl);
                return fileUrl;
            }
        } catch (OSSException oe) {
            log.error(oe.getMessage());
        } catch (ClientException ce) {
            log.error(ce.getMessage());
        } finally {
            //关闭ossClient
            ossClient.shutdown();
        }
        return null;
    }
}

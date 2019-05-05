package com.hand.hcf.app.base.config;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
@Slf4j
@EnableConfigurationProperties(HcfBaseProperties.class)
public class OssConfiguration {

    @Autowired
    private HcfBaseProperties hcfBaseProperties;

    private static final String STORAGE_MODE = "OSS";

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public OSSUtil ossUtil(){
        if (STORAGE_MODE.equals(hcfBaseProperties.getStorage().getMode())) {
            OSSUtil ossUtil;

            String endpoint = hcfBaseProperties.getStorage().getOss().getEndpoint();
            String accessKeyId = hcfBaseProperties.getStorage().getOss().getClient().getId();
            String accessKeySecret = hcfBaseProperties.getStorage().getOss().getClient().getSecret();
            String bucketName = hcfBaseProperties.getStorage().getOss().getBucket().getName();
            String filehost = hcfBaseProperties.getStorage().getOss().getFilehost();

            ossUtil = new OSSUtil(endpoint, accessKeyId, accessKeySecret, bucketName, filehost);
            return ossUtil;
        } else {
            return null;
        }
    }

    public class OSSUtil {

        private String endpoint;

        private String accessKeyId;
        private String accessKeySecret;

        private String bucketName;

        private  String filehost;

        public OSSUtil(){}

        public OSSUtil(String endpoint, String accessKeyId, String accessKeySecret, String bucketName, String filehost) {
            this.endpoint = endpoint;
            this.accessKeyId= accessKeyId;
            this.accessKeySecret = accessKeySecret;
            this.bucketName = bucketName;
            this.filehost = filehost;
        }

        public String upload(String path, InputStream inputStream) {
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
                String fileUrl = filehost + "/" + dateStr+ "/" +  path;

                //上传文件
                PutObjectResult result = ossClient.putObject(new PutObjectRequest(bucketName, fileUrl, inputStream));
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

        public String upload(String path, byte[] bytes) {
            String str = upload(path, new ByteArrayInputStream(bytes));
            return str;
        }
    }
}
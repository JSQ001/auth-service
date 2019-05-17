package com.hand.hcf.app.base.config;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

@Configuration
@Slf4j
@EnableConfigurationProperties(HcfBaseProperties.class)
public class OssConfiguration {

    @Autowired
    private HcfBaseProperties hcfBaseProperties;

    private static final String STORAGE_MODE = "OSS";

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public OSSUtil ossUtil() {
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

        private String filehost;

        public OSSUtil() {
        }

        public OSSUtil(String endpoint, String accessKeyId, String accessKeySecret, String bucketName, String filehost) {
            this.endpoint = endpoint;
            this.accessKeyId = accessKeyId;
            this.accessKeySecret = accessKeySecret;
            this.bucketName = bucketName;
            this.filehost = filehost;
        }

        public String upload(String path, InputStream inputStream) {
            OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            String fileUrl = filehost + "/" +  path;
            try {
                //容器不存在，就创建
                if (!ossClient.doesBucketExist(bucketName)) {
                    ossClient.createBucket(bucketName);
                    CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                    createBucketRequest.setCannedACL(CannedAccessControlList.PublicReadWrite);
                    ossClient.createBucket(createBucketRequest);
                }
                //上传文件
                PutObjectResult result = ossClient.putObject(new PutObjectRequest(bucketName, fileUrl, inputStream));
                //设置权限 这里是公开读
                ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicReadWrite);
                if (null != result) {
                    log.info("==========>file upload successfully：" + fileUrl);
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
            return fileUrl;
        }

        public String upload(String path, byte[] bytes) {
            String str = upload(path, new ByteArrayInputStream(bytes));
            return str;

        }

        //附件下载
        public void downLoad(HttpServletRequest request, HttpServletResponse response, String objectName,String originName) throws IOException {
            // 创建OSSClient实例。
            OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            try {
                String filePath = filehost + "/" +  objectName;
                // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
                OSSObject ossObject = ossClient.getObject(bucketName, filePath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
                InputStream inputStream = ossObject.getObjectContent();
                //缓冲文件输出流
                BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
                //通知浏览器以附件形式下载
               // response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(objectName, "UTF-8"));
                // 为防止 文件名出现乱码
                response.setContentType("application/doc");
                final String userAgent = request.getHeader("USER-AGENT");
                if(StringUtils.contains(userAgent, "MSIE")){//IE浏览器
                    objectName = URLEncoder.encode(objectName,"UTF-8");
                }else if(StringUtils.contains(userAgent, "Mozilla")){//google,火狐浏览器
                    objectName = new String(objectName.getBytes(), "ISO8859-1");
                }else{
                    objectName = URLEncoder.encode(objectName,"UTF-8");//其他浏览器
                }
                response.addHeader("Content-Disposition", "attachment;filename=" +originName);//这里设置一下让浏览器弹出下载提示框，而不是直接在浏览器中打开

                byte[] car = new byte[1024];
                int L;
                while((L = inputStream.read(car)) != -1){
                    if (car.length!=0){
                        outputStream.write(car, 0,L);
                    }
                }

                if(outputStream!=null){
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (OSSException oe) {
                log.error(oe.getMessage());
            } catch (ClientException ce) {
                log.error(ce.getMessage());
            } finally {
                //关闭ossClient
                ossClient.shutdown();
            }
        }

        //附件删除
        public void deleteOssFile(String objectName){
            // 创建OSSClient实例。
            OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            //判断文件是否存在
            String filePath = filehost + "/" +  objectName;
            boolean found = ossClient.doesObjectExist(bucketName, filePath);
            if(true) {
                // 删除文件 objectName不只是文件名称，还要指定相对路径
                ossClient.deleteObject(bucketName, filePath);
            }
            // 关闭OSSClient。
            ossClient.shutdown();
        }
    }
}

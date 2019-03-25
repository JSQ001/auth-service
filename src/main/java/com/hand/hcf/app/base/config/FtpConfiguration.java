package com.hand.hcf.app.base.config;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

/**
 * @author zlp95
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(HcfBaseProperties.class)
public class FtpConfiguration {

    @Autowired
    private HcfBaseProperties hcfBaseProperties;

    private static final String STORAGE_MODE = "FTP";

    @Bean
    public FTPClient ftpClient(){
        if (STORAGE_MODE.equals(hcfBaseProperties.getStorage().getMode())) {
            FTPClient ftpClient =  new FTPClient();
            //ftp设置
            ftpClient.setControlEncoding("GBK");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setBufferSize(1024);
            return ftpClient;
        } else {
            return null;
        }
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SFTPUtil sftpUtil(){
        if (STORAGE_MODE.equals(hcfBaseProperties.getStorage().getMode())) {
            SFTPUtil sftpUtil;
            String url = hcfBaseProperties.getStorage().getFtp().getUrl();
            int port = hcfBaseProperties.getStorage().getFtp().getPort();
            String username = hcfBaseProperties.getStorage().getFtp().getUsername();
            String password = hcfBaseProperties.getStorage().getFtp().getPassword() == null ? null :
                    new String(Base64.getDecoder().decode(hcfBaseProperties.getStorage().getFtp().getPassword()));
            String privateKey = hcfBaseProperties.getStorage().getFtp().getPrivateKey();
            if (!StringUtils.isEmpty(privateKey)) {
                sftpUtil = new SFTPUtil(username, url, port, privateKey);
            } else {
                sftpUtil = new SFTPUtil(username, password, url, port);
            }
            return sftpUtil;
        } else {
            return null;
        }
    }

    public class SFTPUtil {
        private ChannelSftp sftp;

        private Session session;

        private String username;

        private String password;

        private String privateKey;

        private String host;

        private int port;

        public SFTPUtil(){}

        /**
         * 构建基于密码认证的sftp对象
         * @param username
         * @param password
         * @param host
         * @param port
         */
        public SFTPUtil(String username, String password, String host, int port) {
            this.username = username;
            this.password = password;
            this.host = host;
            this.port = port;
        }


        public SFTPUtil(String username, String host, int port, String privateKey) {
            this.username = username;
            this.host = host;
            this.port = port;
            this.privateKey = privateKey;

        }

        /**
         * 连接SFTP服务器
         */
        public void login(){
            try {
                JSch jSch = new JSch();
                if (!StringUtils.isEmpty(privateKey)) {
                    InputStream in = this.getClass().getResourceAsStream(privateKey);
                    byte[] prvkey = null;
                    try {
                        prvkey = new byte[in.available()];
                        in.read(prvkey);
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    jSch.addIdentity(privateKey,prvkey,null,null);
                    log.info("sftp connect,path of private key file：{}" , privateKey);
                }
                log.info("sftp connect by host:{} username:{}",host,username);

                session = jSch.getSession(username, host, port);
                log.info("session is build.");
                if (!StringUtils.isEmpty(password)) {
                    session.setPassword(password);
                }
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                // 关闭 gssapi认证
                config.put("userauth.gssapi-with-mic", "no");
                session.setConfig(config);
                session.connect();
                log.info("session is connected.");

                Channel channel = session.openChannel("sftp");
                channel.connect();
                log.info("channel is connected.");

                sftp = (ChannelSftp)channel;
            } catch (JSchException e) {
                e.printStackTrace();
                log.error("Cannot connect to specified sftp server : {}:{} \n Exception message is: {}",
                        new Object[]{host, port, e.getMessage()});
            }
        }

        /**
         * 关闭连接server
         */
        public void logout() {
            if (sftp != null) {
                if (sftp.isConnected()) {
                    sftp.disconnect();
                    log.info("sftp is closed already.");
                }
            }
            if (session != null) {
                if (session.isConnected()) {
                    session.disconnect();
                    log.info("session is closed already.");
                }
            }
        }


        /**
         * 将输入流的数据上传到sftp作为文件
         * @param path
         * @param inputStream
         * @throws SftpException
         */
        public String upload(String path, InputStream inputStream) throws SftpException{
            //分级创建目录、文件名
            String[] paths = StringUtils.split(path, "/");
            String uploadPath = hcfBaseProperties.getStorage().getFtp().getUploadPath();
            String filePath;
            if (StringUtils.isEmpty(uploadPath)){
                filePath = sftp.pwd() + "/" + path;
            }else{
                filePath = uploadPath + "/" + path;
                // 进入到指定的目录
                sftp.cd(uploadPath);
            }
            String filename = paths[paths.length - 1];
            for (int i = 0;i < paths.length - 1;i++) {
                try {
                    sftp.cd(paths[i]);
                }catch (SftpException e){
                    try {
                        sftp.mkdir(paths[i]);
                        sftp.cd(paths[i]);
                    }catch (SftpException e1){
                        sftp.cd(paths[i]);
                    }

                }
            }
            sftp.put(inputStream, filename);
            log.info("file:{} is upload successful" , filename);
            return filePath;
        }

        /**
         * 将byte[]上传到sftp，作为文件。注意:从String生成byte[]是，要指定字符集。
         *
         * @param path
         *            上传到sftp目录
         * @param bytes
         *            要上传的字节数组
         * @throws SftpException
         * @throws Exception
         */
        public String upload(String path, byte[] bytes) throws SftpException{

            String str = upload(path, new ByteArrayInputStream(bytes));
            return str;
        }

        public void remove(String path) throws SftpException{
            String[] paths = StringUtils.split(path, "/");
            String filename = paths[paths.length - 1];
            sftp.rm(path);
            log.info("file:{} is deleted successful" , filename);
        }

        public InputStream getInputStream(String path) throws SftpException{
            return sftp.get(path);
        }
    }
}

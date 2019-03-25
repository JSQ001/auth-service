/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.hand.hcf.app.base.util;

import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.util.LoginInformationUtil;
import com.itextpdf.text.io.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class FileUtil {
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    public static byte[] getFileBinaryForDownload(String filePath) {

        InputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = StreamUtil.getResourceStream(filePath);
            bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            throw new BizException(RespCode.READ_FILE_FAILED);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static String getFileHeader(MultipartFile file) {
        InputStream is = null;
        String value = null;
        try {
            is = file.getInputStream();
            byte[] b = new byte[4];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        System.out.println(builder.toString());
        return builder.toString();
    }

    public static boolean isValidImage(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith("jpeg") && !fileName.endsWith("jpg") && !fileName.endsWith("png")) {
            return false;
        }
        String fileHead = getFileHeader(file);
        HashMap<String, String> mFileTypes = new HashMap<String, String>();
        mFileTypes.put("FFD8FFE0", "jpg");
        mFileTypes.put("89504E47", "png");
        mFileTypes.put("47494638", "gif");
        if (!mFileTypes.containsKey(fileHead)) {
            return false;
        } else {
            return true;
        }
    }

    public static String getTemplatePath(String path,String language) {
        if(StringUtils.isEmpty(language)){
            language = LoginInformationUtil.getCurrentLanguage();
        }
        String localTemplatePath = path.replace(".", "-" + language.toString() + ".");
        try {
            InputStream inputStream = StreamUtil.getResourceStream(localTemplatePath);
            if (inputStream == null) {
                return path;
            } else {
                return localTemplatePath;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return path;
    }
}

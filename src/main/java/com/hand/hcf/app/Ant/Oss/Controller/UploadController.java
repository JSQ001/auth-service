package com.hand.hcf.app.Ant.Oss.Controller;
import com.hand.hcf.app.Ant.Oss.Util.AliyunOSSUtil;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;


@Controller
@RequestMapping("upload")
public class UploadController {
 private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
 /**
  * 文件上传
  * @param file
  */
 @RequestMapping(value = "uploadBlog",method = RequestMethod.POST)
 public String uploadBlog(MultipartFile file){

  logger.info("============>文件上传");
  try {

   if(null != file){
    String filename = file.getOriginalFilename();
    if(!"".equals(filename.trim())){
     File newFile = new File(filename);
     System.out.println(newFile);
     FileOutputStream os = new FileOutputStream(newFile);
     os.write(file.getBytes());
     os.close();
     file.transferTo(newFile);
     //上传到OSS
     String uploadUrl = AliyunOSSUtil.upload(newFile);

    }

   }
  }catch (Exception ex){
   ex.printStackTrace();
  }
  return null;
 }


}

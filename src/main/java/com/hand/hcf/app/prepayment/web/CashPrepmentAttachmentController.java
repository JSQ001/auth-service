package com.hand.hcf.app.prepayment.web;


import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.prepayment.domain.PrepaymentAttachment;
import com.hand.hcf.app.prepayment.service.PrepaymentAttachmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Created by 刘亮 on 2017/11/20.
 */
@Api(tags = "预付款附件")
@RestController
@RequestMapping("/api/attachment")
public class CashPrepmentAttachmentController {
    private final PrepaymentAttachmentService prepaymentAttachmentService;

    public CashPrepmentAttachmentController(PrepaymentAttachmentService prepaymentAttachmentService) {
        this.prepaymentAttachmentService = prepaymentAttachmentService;
    }


    /**
     * 当需要上传文件到本地服务器时，使用。
     *   目前是上传到阿里云服务器，暂不使用，但作保留
     *   注意，若使用本方式，需要在日记账头表里面添加path字段
     * @param request
     * @return
     */
    /**
     * @apiDescription 当需要上传文件到本地服务器时，使用。 目前是上传到阿里云服务器，暂不使用，但作保留，注意，若使用本方式，需要在日记账头表里面添加path字段
     * @api {get} /api/attachment/upload/batch 【附件】 上传
     * @apiGroup PrepaymentService
     */
    @RequestMapping(value = "/upload/batch")
    @ApiOperation(value = "当需要上传文件到本地服务器时，使用", notes = "当需要上传文件到本地服务器时，使用 开发:刘亮")
    public ResponseEntity<PrepaymentAttachment> batchUpload(HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)request;
        List<MultipartFile> files = multipartHttpServletRequest.getFiles("file");
        String attachmentType = multipartHttpServletRequest.getParameter("attachmentType");
        Long objectId = multipartHttpServletRequest.getParameter("objectId") ==null?null:Long.parseLong(multipartHttpServletRequest.getParameter("objectId"));
        List<PrepaymentAttachment> list = prepaymentAttachmentService.uploadBath(files,attachmentType,objectId);
        return ResponseEntity.ok(CollectionUtils.isNotEmpty(list)?list.get(0):null);
    }

    /**
     * 下载附件
     */
    /**
     * @apiDescription 下载附件
     * @api {get} /api/attachment/download 【附件】 下载
     * @apiGroup PrepaymentService
     */
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ApiOperation(value = "下载附件", notes = "下载附件 开发:刘亮")
    public ResponseEntity<InputStreamResource> downloadFile(@ApiParam(value = "路径") @RequestParam("path") String path)
            throws IOException {
        String filePath =path;
        FileSystemResource file = new FileSystemResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getFilename()));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");


        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(file.getInputStream()));
    }


    /*
    * 根据附件oid查询附件详情
    * */

    /**
     * @apiDescription 根据附件oid查询附件详情
     * @api {get} /api/attachment/get/by/oid?oid=3d6e3b33-bccd-4f8e-9e70-bdb83989ef9c 【附件】 查询
     * @apiGroup PrepaymentService
     * @apiParam {String} oid 附件oid
     * @apiSuccess {String} attachmentOID 附件oid
     * @apiSuccess {String} fileName 文件名
     * @apiSuccess {String} fileUrl 文件url
     * @apiSuccess {String} link 连接
     * @apiSuccess {Long} size 大小
     * @apiSuccessExample {json} 请求参数:
    {
    "id": "971990148185071618",
    "isEnabled": true,
    "isDeleted": false,
    "createdDate": "2018-03-09T14:04:45+08:00",
    "createdBy": 177601,
    "lastUpdatedDate": "2018-03-09T14:04:45+08:00",
    "lastUpdatedBy": 177601,
    "versionNumber": 1,
    "attachmentOID": "3d6e3b33-bccd-4f8e-9e70-bdb83989ef9c",
    "fileName": "费控系统_项目解决方案V5.0.pdf",
    "fileUrl": "http://116.228.77.183:25297/u01/hec/attachment/e106441b-fa16-4527-924b-1bbb07b28be9/prepayment/3d6e3b33-bccd-4f8e-9e70-bdb83989ef9c-费控系统_项目解决方案V5.0.pdf",
    "link": "http://116.228.77.183:25297/u01/hec/attachment/e106441b-fa16-4527-924b-1bbb07b28be9/prepayment/3d6e3b33-bccd-4f8e-9e70-bdb83989ef9c-费控系统_项目解决方案V5.0.pdf",
    "size": "1212343"
    }
     */
    @GetMapping("/get/by/oid")
    @ApiOperation(value = "根据附件oid查询附件详情", notes = "根据附件oid查询附件详情 开发:刘亮")
    public ResponseEntity<PrepaymentAttachment> getAttachmentByOid(@ApiParam(value = "附件oid") @RequestParam String oid){
        PrepaymentAttachment prepaymentAttachment = prepaymentAttachmentService.selectByOId(oid);
        return ResponseEntity.ok(prepaymentAttachment);
    }


}

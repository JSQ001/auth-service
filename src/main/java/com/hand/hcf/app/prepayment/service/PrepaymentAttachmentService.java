package com.hand.hcf.app.prepayment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.base.attachment.enums.AttachmentType;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionHead;
import com.hand.hcf.app.prepayment.domain.PrepaymentAttachment;
import com.hand.hcf.app.prepayment.persistence.CashPaymentRequisitionHeadMapper;
import com.hand.hcf.app.prepayment.persistence.PrepaymentAttachmentMapper;
import com.hand.hcf.app.prepayment.web.adapter.CashPaymentRequisitionHeaderAdapter;
import com.hand.hcf.core.service.BaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 刘亮 on 2017/12/18.
 */
@Service
public class PrepaymentAttachmentService extends BaseService<PrepaymentAttachmentMapper,PrepaymentAttachment> {
    @Value("${Prepayment.attachments.path:}")
    private String path;//路径
    @Value("${Prepayment.attachments.prefix:}")
    private String prefix;//前缀
    private final PrepaymentAttachmentMapper prepaymentAttachmentMapper;
    private final CashPaymentRequisitionHeadMapper cashPaymentRequisitionHeadMapper;

    public PrepaymentAttachmentService(PrepaymentAttachmentMapper prepaymentAttachmentMapper, CashPaymentRequisitionHeadMapper cashPaymentRequisitionHeadMapper) {
        this.prepaymentAttachmentMapper = prepaymentAttachmentMapper;
        this.cashPaymentRequisitionHeadMapper = cashPaymentRequisitionHeadMapper;
    }


    /*根据附件oid查询附件详情*/
    public PrepaymentAttachment selectByOId(String oid){
        PrepaymentAttachment prepaymentAttachment = prepaymentAttachmentMapper.selectList(
                new EntityWrapper<PrepaymentAttachment>().eq("attachment_oid",oid)
        ).get(0);
        return prepaymentAttachment;
    }

    //批量上传附件
    public List<PrepaymentAttachment> uploadBath(List<MultipartFile> files, String attachmentsType, Long objectId){

        AttachmentType attachmentType = null;
        if(attachmentsType.equals(AttachmentType.PREPAYMENT.toString())){
            attachmentType = AttachmentType.PREPAYMENT;
        }else {
//            throw new BizException(RespCode.ATTACHMENT_TYPE_NOT_NULL);
        }

        List<PrepaymentAttachment> attachments = new ArrayList<PrepaymentAttachment>();
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        List<String> fullNames = new ArrayList<>();
        for (int i = 0; i < files.size(); ++i) {

            file = files.get(i);
            if (file!=null) {
                try {
                    byte[] bytes = file.getBytes();
                    String fileOriginalFilename = file.getOriginalFilename();
                    String fileSuffix= fileOriginalFilename.substring(fileOriginalFilename.lastIndexOf("."));
                    String fileName = fileOriginalFilename.substring(0,fileOriginalFilename.indexOf("."));
                    String fullName = fileName+ UUID.randomUUID()+fileSuffix;
                    fullNames.add(fullName);
                    PrepaymentAttachment attachment = new PrepaymentAttachment();
                    attachment.setAttachmentOID(UUID.randomUUID().toString())
                            .setFileName(file.getOriginalFilename())
                            .setSize(file.getSize());
                    String fileUrl = getFileUrl(attachment, attachmentType);
                    attachment.setFileUrl(fileUrl).setLink(fileUrl);

                    attachments.add(attachment);
                    String filePath = fileUrl.substring(0,fileUrl.lastIndexOf("/")+1);
                    //路径不存在，创建路径
                    if (!(new File(filePath).isDirectory())) {
                        new File(filePath).mkdirs();
                    }
                    attachment.setFileUrl(prefix+attachment.getFileUrl());
                    attachment.setLink(prefix+attachment.getLink());
                    this.insert(attachment);
                    stream = new BufferedOutputStream(new FileOutputStream(fileUrl));
                    stream.write(bytes);
                    stream.close();
                } catch (Exception e) {
                    stream = null;
//                    throw new BizException(RespCode.BUDGET_JOURNAL_HEAD_UPLOAD_ERR);
                }
            } else {
//                throw new BizException(RespCode.BUDGET_JOURNAL_HEAD_FILE_IS_NULL);
            }
        }

        /*如果是修改，将这些附件oid直接保存到对应的表里
        * 如果事新增，则不管
        * */
        if(objectId !=null){
            List<String> attachmentOids = new ArrayList<>();
            attachments.forEach(
                    attachment->{
                        attachmentOids.add(attachment.getAttachmentOID());
                    }
            );
            switch (attachmentType){
                case PREPAYMENT:
                {
                    CashPaymentRequisitionHead header = new CashPaymentRequisitionHead();
                    CashPaymentRequisitionHeaderAdapter.AttachmentOidToJsonString(attachmentOids,header);
                    header.setId(objectId);
                    cashPaymentRequisitionHeadMapper.updateById(header);
                }
            }
        }
        return attachments;
//        return StringUtils.join(fullNames,",");
    }

    private String getFileUrl(PrepaymentAttachment attachment, AttachmentType attachmentType){
        String fileUrl =path;
        switch (attachmentType){
            case PREPAYMENT:

                fileUrl = this.path+ OrgInformationUtil.getCurrentCompanyOid().toString()+"/prepayment/"+attachment.getAttachmentOID()+"/"+attachment.getFileName();
        }
        return fileUrl;
    }



    /*
    * 将给定的URL下载
    * */


}

package com.hand.hcf.app.mdata.contact.dto;


import lombok.Data;


/**
 * 用户基本信息查询 方便查询使用
 * for service
 * Created by zhiyu.liu on 18/5/4
 */
@Data
public class UserInfoDTO {
   private Long userId;
   private String userOid;
   private String userName;
   private String userCode;
   private String companyName;
}

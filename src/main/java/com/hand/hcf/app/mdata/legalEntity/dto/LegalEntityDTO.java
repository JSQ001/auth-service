package com.hand.hcf.app.mdata.legalEntity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 法人实体视图对象类
 * Created by Strive on 17/9/4.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LegalEntityDTO implements Serializable {


    private Long id;                        // 法人实体id

    @JsonIgnore
    private Long tenantId;                  // 租户id


    private Long setOfBooksId;              // 账套id

    private String setOfBooksName;          // 账套名称

    private String setOfBooksCode;          // 账套代码

    private UUID companyReceiptedOid;       // 法人实体oid

    private String entityName;              // 法人实体名称

    private UUID companyOid;                // 公司oid


    private Long parentLegalEntityId;       // 上级法人实体id

    private String parentLegalEntityName;   // 上级法人实体名称

    private String address;                 // 地址

    private String taxpayerNumber;          // 纳税人识别号

    private String accountBank;             // 开户支行

    private String telePhone;               // 电话

    private String accountNumber;           // 账号

    private Boolean enabled;              // 是否启用

    private Boolean deleted = false;      // 是否删除

    private ZonedDateTime createdDate;           // 创建日期

    private Long createdBy;               // 创建人

    private ZonedDateTime lastUpdatedate;       // 最后修改日期

    private Long lastUpdatedBy;          // 最后修改人

    private Map<String, List<Map<String, String>>> i18n;    // 多语言



    private Long attachmentId;              // 附件id

    private String fileURL;                 // 文件路径

    private String thumbnailUrl;

    private String iconUrl;

    private String path;

    private Integer depth;

    /**
     * 主语言
     */
    private String mainLanguage;
    private Integer versionNumber;
}

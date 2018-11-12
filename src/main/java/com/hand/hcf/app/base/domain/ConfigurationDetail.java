package com.hand.hcf.app.base.domain;

/**
 * Created by markfredchen on 16/3/20.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hcf.app.base.domain.enumeration.TravelApplyType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.map.HashedMap;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ConfigurationDetail {
    private Workflow workflow = new Workflow();
    private Didi didi = new Didi();
    private Travel travel = new Travel();
    private Reimbursement reimbursement = new Reimbursement();
    private Bpo bpo = new Bpo();
    private String comment;
    private List<CustomConfiguration> departmentConfigurations = new ArrayList<CustomConfiguration>();
    private List<CustomConfiguration> userConfigurations = new ArrayList<CustomConfiguration>();
    private ApprovalRule approvalRule = new ApprovalRule();//审批规则
    private FinanceConfiguration financeConfiguration = new FinanceConfiguration();//财务配置
    private UI ui = new UI();//显示配置
    private List<Integer> integrations = new ArrayList<>();//已开通的第三方集成
    private List<ThridParties> messageKeys = new ArrayList<>();//已开通的第三方messageKey集成
    private List<Integer> menuList = new ArrayList<>();//app菜单列表
    private WxConfiguration wxConfiguration;//微信配置

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class Workflow {
        //工作流订制类
        private String serviceClassName;
        //是否启用财务付款
        private Boolean enableFinanceLoan = true;
        //是否审批基于部门
        private Boolean approvalBasedDepartment = true;
        //是否启用财务审核
        private Boolean enableFinanceInvoiceVerification = true;
        //是否启用财务批次
        private Boolean enableFinanceBatch = true;

        private String prefixApproverPath;
        private String suffixApproverPath;

        //是否启用设置审批人
        private Boolean enableAssignApprovers = false;

    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class Bpo {
        //是否启用BPO费用识别
        private Boolean enableInvoiceRecognition = false;
        //是否启用BPO发票审核
        private Boolean enableInvoiceVerification = false;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class Didi {
        private String companyID;
        private String phone;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class Travel {
        //是否启用
        private Boolean enabled = false;
        //是否启用自审批
        private Boolean autoApproval = false;
        //项目是否必须
        private Boolean costCenterItemCodeRequired = false;
        //启用的差旅类型
        private List<Integer> travelTypes = Arrays.asList(TravelApplyType.NORMAL.getID());
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class Reimbursement {
        //是否启用报销单
        private Boolean enabled = true;
        //项目是否必须
        private Boolean costCenterItemCodeRequired = false;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class CustomConfiguration {
        //对应OID
        private Set<UUID> oids = new HashSet<>();
        private Workflow workflow;
        private Didi didi;
        private Bpo bpo;
        private Travel travel;
        private Reimbursement reimbursement;
        private String comment = "";
    }


    //审批规则
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class ApprovalRule {
        private Integer approvalMode = 1003;//默认选人审批
        private Integer maxApprovalChain = -1;//审批链长度
        private Integer approvalPathMode = 1001;//获取审批人模式,全链
        private Integer departmentLevel = 1;//选部门审批部门级
    }

    //部门和成本中心显示配置
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class UI {
        private Selector showDepartmentSelector = new Selector(Boolean.TRUE, Boolean.TRUE);
        private Selector showCostCenterSelector = new Selector(Boolean.FALSE, Boolean.FALSE);
        //是否显示外部参与人
        private Boolean showExternalParticipant = false;

        //是否显示供应商列表
        private Boolean showSupplierList = false;
    }

    //显示配置
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Selector {
        private Boolean applications = false;
        private Boolean expenseReports = false;
    }

    //财务管理配置
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class FinanceConfiguration {
        //是否启用过滤部门
        private Boolean enableFilterDepartments = false;
        private Map<UUID, Set<UUID>> financeManagerOIDDepartmentOIDsMap = new HashedMap();

        //是否启用过滤法人实体
        private Boolean enableFilterCorporations = false;
        private Map<UUID, Set<UUID>> financeManagerOIDCorporationOIDsMap = new HashedMap();

        private Boolean enableFilterLoan = false;
        private Set<UUID> financeManagerLoan = new HashSet<>();//借款单查看

        private Boolean enableFilterRepayment = false;
        private Set<UUID> financeManagerRepayment = new HashSet<>();//付款单查看
    }

    //微信相关配置
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class WxConfiguration {
        //企业号是否开启消息推送
        private Boolean pushFlag = false;
        //回调token
        private String token;
        //套件ID
        private String suiteId;
        //套件secret
        private String suiteSecret;
        //回调encodingAesKey
        private String encodingAesKey;
        //对应的企业号ID
        private String corpId;
        //对应的secretKey
        private String secretKey;
        //对应的企业AppID
        private String appId;
        //对应的secretKey
        private String appSecretKey;
        //通讯录是否映射
        private Boolean mappingFlag = false;
        //企业号通讯录是否由HLY进行同步（可能部分是客户自己已有同步程序）
        private Boolean synFlag = true;
        //企业号通讯录同步人员根据字段
        private String synBasis;
        //企业号汇联易应用的ID
        private Integer hlyId;
        //消息跳转链接
        private String msgUrl;
        //FLYBACK消息跳转链接
        private String flybackUrl;
        //MAPPING SQL,according wxid to find user
        private String mapSql;
        //MAPPING SQL,according user to find wxid
        private String userOIDSql;
    }

    //供应商配置
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class ThridParties {
        private String messageKey;
        private String ssoURL;
        private Boolean HLY_TokenRequire = false;
        private String ssoType;
        private String icon;
        private String target;
        private Boolean hasToolbar = false;
    }
}

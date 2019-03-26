package com.hand.hcf.app.mdata.bank.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainLogic;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/24 09:34
 */
@Data
@TableName("sys_bank_transaction")
public class BankTransaction extends DomainLogic {
    /**
     * 用户OID
     */
    @TableField("owner_oid")
    private UUID ownerOid;
    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private Long tenantID;
    @TableField("company_oid")
    private UUID companyOID;
    /**
     * 商务卡消费是否被生成费用
     */
    @TableField("used")
    private Boolean used;
    /**
     * 商务卡数据生成费用的方式  1001-商务卡池生成  1000其他方式生成
     */
    @TableField("expense_created_type")
    private Integer expenseCreatedType;
    /**
     * 逻辑删除标识，true已删除 false未删除 ,默认未删除
     */
    @TableField("deleted")
    private Boolean deleted = false;
    /**
     * bank code
     */
    @TableField("card_type_code")
    private String cardTypeCode;
    /**
     *银行名称
     */
    @TableField(exist = false)
    private String bankName;
    /**
     * 银行描述
     */
    @TableField(exist = false)
    private String bankDescription;
    /**
     * 成本中心号
     */
    @TableField("cor_num")
    private String corNum;
    /**
     * 持卡人账单号
     */
    @TableField("act_num")
    private String actNum;
    /**
     * 账单月,yyyy-MM
     */
    @TableField("bil_mon")
    private String bilMon;
    /**
     * 账单日,yyyy-MM-dd
     */
    @TableField("bil_date")
    private String bilDate;
    /**
     * 卡号
     */
    @TableField("crd_num")
    private String crdNum;
    /**
     * 持卡人中文名
     */
    @TableField("act_chi_nam")
    private String actChiNam;
    /**
     * 持卡人英文名
     */
    @TableField("act_eng_nam")
    private String actEngNam;
    /**
     * 员工号
     */
    @TableField("emp_num")
    private String empNum;
    /**
     * 交易日,yyyy-MM-dd
     */
    @TableField("trs_date")
    private String trsDate;
    /**
     * 交易时间,HHmmss
     */
    @TableField("trx_tim")
    private String trxTim;
    /**
     * 交易金额
     */
    @TableField("ori_cur_amt")
    private BigDecimal oriCurAmt;
    /**
     * 交易币种
     */
    @TableField("ori_cur_cod")
    private String oriCurCod;
    /**
     * 入账日,yyyy-MM-dd
     */
    @TableField("pos_date")
    private String posDate;
    /**
     * 入账金额
     */
    @TableField("pos_cur_amt")
    private BigDecimal posCurAmt;
    /**
     * 入账货币
     */
    @TableField("pos_cur_cod")
    private String posCurCod;
    /**
     * 商户所在国家代码
     */
    @TableField("acp_cty_cod")
    private String acpCtyCod;
    /**
     * 商户类型描述码
     */
    @TableField("acp_type")
    private String acpType;
    /**
     * 商户代码
     */
    @TableField("acp_id")
    private String acpId;
    /**
     * 商户名称
     */
    @TableField("acp_name")
    private String acpName;
    /**
     * 授权返回码
     */
    @TableField("apr_cod")
    private String aprCod;
    /**
     * 交易参考号
     */
    @TableField("trx_ref")
    private String trxRef;
    /**
     * 交易类型，00-一般消费,01-预借现金,12-预借现金退货,20-一般消费退货,60-还款及费用
     */
    @TableField("trs_cod")
    private String trsCod;
    /**
     * 收单顺序号
     */
    @TableField("trs_vch_nbr")
    private String trsVchNbr;
    /**
     * 序列号
     */
    @TableField("seq_num")
    private String seqNum;
    /**
     * 是否逾期,true-逾期  false未逾期 ,默认false
     */
    @TableField("over_time")
    private Boolean overTime;
    /**
     * 商务卡审核通过期限日期
     */
    @TableField("approved_dead_line_date")
    private String approvedDeadLineDate;
    /**
     * 备注
     */
    private String remark;
}

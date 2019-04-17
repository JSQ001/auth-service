package com.hand.hcf.app.base.system.service;

import com.hand.hcf.app.base.codingrule.domain.CodingRule;
import com.hand.hcf.app.base.codingrule.domain.CodingRuleDetail;
import com.hand.hcf.app.base.codingrule.domain.CodingRuleValue;
import com.hand.hcf.app.base.codingrule.service.CodingRuleDetailService;
import com.hand.hcf.app.base.codingrule.service.CodingRuleObjectService;
import com.hand.hcf.app.base.codingrule.service.CodingRuleService;
import com.hand.hcf.app.base.codingrule.service.CodingRuleValueService;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dong.liu on 2017-08-24
 */
@Service
public class OrderNumberService {

    @Autowired
    private CodingRuleValueService codingRuleValueService;

    @Autowired
    private CodingRuleObjectService codingRuleObjectService;

    @Autowired
    private CodingRuleService codingRuleService;

    @Autowired
    private CodingRuleDetailService codingRuleDetailService;


    /**
     * 编码规则对外提供的接口，生成单据编号
     *
     * @param documentTypeCode 单据类型代码
     * @param companyCode      公司代码
     * @param operationDate    操作日期
     * @return
     */
    public String getOderNumber(
        String documentTypeCode,
        String companyCode,
        String operationDate,
        Long tenantId) {

        // null or ""，报错
        if (StringUtils.isBlank(documentTypeCode)) {
            throw new BizException(RespCode.BUDGET_CODING_RULE_VALUE_DOCUMENT_TYPE_NOT_FOUND);
        }

        // null or ""，报错
        if (StringUtils.isBlank(companyCode)) {
            throw new BizException(RespCode.BUDGET_CODING_RULE_VALUE_COMPANY_CODE_NOT_FOUND);
        }

        //操作日期如果为空用系统当前时间
        if (operationDate == null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            operationDate = simpleDateFormat.format(new Date());
        } else {
            //正则判断日期格式是否为yyyy-MM-dd
            String eL = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
            Pattern p = Pattern.compile(eL);
            Matcher m = p.matcher(operationDate);
            boolean dateFlag = m.matches();
            if (!dateFlag) {
                throw new BizException(RespCode.BUDGET_CODING_RULE_OPERATION_DATE_FORMAT_EXCEPTION);
            }
        }

        //区分是租户级的编码规则定义还是公司级的
        boolean isTenant = false;

        //匹配单据类型、公司代码、当前租户。公司级，用户手动新建的数据
        Long codingRuleObjectId = codingRuleObjectService.getCodingRuleObjectIdByCond(documentTypeCode, companyCode, tenantId);

        //匹配不到再匹配单据类型、当前租户。租户级，默认数据
        if (codingRuleObjectId == null) {
            codingRuleObjectId = codingRuleObjectService.getCodingRuleObjectIdByCond(documentTypeCode, null, tenantId);
            isTenant = true;
        }

        //如果还匹配不到就报错
        if (codingRuleObjectId == null) {
            throw new BizException(RespCode.BUDGET_CODING_RULE_OBJECT_NOT_ENABLED);
        }

        //匹配到启用的编码规则，要用到其中的重置频率
        CodingRule codingRule = codingRuleService.getCodingRuleByCond(codingRuleObjectId);

        //匹配到编码规则明细
        List<CodingRuleDetail> codingRuleDetails = codingRuleDetailService.getCodingRuleDetailByCond(
            codingRule.getId()
        );

        if (codingRuleDetails.size() == 0) {
            throw new BizException(RespCode.BUDGET_CODING_RULE_DETAIL_NOT_FOUND);
        }

        //建立一个CodingRuleValues对象来判断是否要更新值,且作为最终要插入或更新的值
        CodingRuleValue codingRuleValue = new CodingRuleValue();
        //以下四个字段可以直接set
        codingRuleValue.setDocumentTypeCode(documentTypeCode);
        codingRuleValue.setCodingRuleId(codingRule.getId());
        codingRuleValue.setPeriodName(operationDate);
        codingRuleValue.setTenantId(tenantId);
        if (isTenant) {
            codingRuleValue.setCompanyCode("");
        } else {
            codingRuleValue.setCompanyCode(companyCode);
        }

        //最终返回的单据编号
        String orderNumber = "";

        for (CodingRuleDetail codingRuleDetail : codingRuleDetails) {
            String segmentType = codingRuleDetail.getSegmentType();

            //固定字段
            if (segmentType.equals("10")) {
                orderNumber += codingRuleDetail.getSegmentValue();
            }

            //日期格式
            if (segmentType.equals("20")) {
                //用于将yyyy-MM-dd日期参数转为date
                SimpleDateFormat simpleDateFormat_operationDate = new SimpleDateFormat("yyyy-MM-dd");
                //YYMMDD需要->YYMMdd
                SimpleDateFormat simpleDateFormat_periodName = new SimpleDateFormat(codingRuleDetail.getDateFormat().replace("DD", "dd"));
                try {
                    String periodName = simpleDateFormat_periodName.format(simpleDateFormat_operationDate.parse(operationDate));
                    orderNumber += periodName;
                } catch (ParseException e) {
                    throw new BizException(RespCode.BUDGET_CODING_RULE_DATE_FORMAT_EXCEPTION);
                }
            }

            //单据类型代码
            if (segmentType.equals("30")) {
                orderNumber += documentTypeCode;
            }

            //公司代码
            if (segmentType.equals("40")) {
                orderNumber += companyCode;
            }

            //遍历是否有序列号
            if (segmentType.equals("50")) {
                //用于存放最后要存于表中的当前值
                int temp_current_value = 0;

                //如果是租户级公司代码当空字符串去mapping一个value值
                if (isTenant) {
                    companyCode = "";
                }

                //根据单据类别、单据类型、公司代码获取coding_rule_value表中对应数据
                List<CodingRuleValue> codingRuleValues = codingRuleValueService.getBudgetCodingRuleValueByCond(
                    documentTypeCode, companyCode, codingRule.getId());

                //判断表中是否有各种频率的当前值
                boolean has_current_value = false;

                if (codingRule.getResetFrequence().equals("NEVER")) {
                    for (CodingRuleValue codingRuleValue_for_update : codingRuleValues) {
                        //获取表中频率为从不的当前值
                        if (codingRuleValue_for_update.getNeverCurrentValue() != null) {
                            codingRuleValue.setNeverCurrentValue(
                                codingRuleValue_for_update.getNeverCurrentValue()
                                    + codingRuleDetail.getIncremental()
                            );
                            codingRuleValue.setId(codingRuleValue_for_update.getId());

                            has_current_value = true;
                            //有则直接跳出当前循环
                            break;
                        }
                    }
                    //表中没有频率为从不的情况
                    if (codingRuleValues.size() == 0 || !has_current_value) {
                        codingRuleValue.setNeverCurrentValue(codingRuleDetail.getStartValue());
                    }
                    temp_current_value = codingRuleValue.getNeverCurrentValue();
                } else if (codingRule.getResetFrequence().equals("PERIOD")) {
                    for (CodingRuleValue codingRuleValue_for_update : codingRuleValues) {
                        //获取表中频率为没月的当前值
                        if (codingRuleValue_for_update.getMonthCurrentValue() != null) {
                            //取yyyy-MM格式(即年加月)为比较格式
                            String periodName_substring = codingRuleValue_for_update.getPeriodName().substring(0, 7);
                            String operationDate_substring = operationDate.substring(0, 7);

                            //当前时间与表里数据比较，true更新 false插入
                            if (periodName_substring.equals(operationDate_substring)) {
                                codingRuleValue.setMonthCurrentValue(
                                    codingRuleValue_for_update.getMonthCurrentValue()
                                        + codingRuleDetail.getIncremental()
                                );
                                codingRuleValue.setId(codingRuleValue_for_update.getId());

                                has_current_value = true;

                                //有则直接跳出当前循环
                                break;
                            }
                        }
                    }
                    //表中没有当前月份的当前值
                    if (codingRuleValues.size() == 0 || !has_current_value) {
                        codingRuleValue.setMonthCurrentValue(codingRuleDetail.getStartValue());
                    }
                    temp_current_value = codingRuleValue.getMonthCurrentValue();
                } else if (codingRule.getResetFrequence().equals("YEAR")) {
                    for (CodingRuleValue codingRuleValue_for_update : codingRuleValues) {
                        //获取表中频率为从不的当前值
                        if (codingRuleValue_for_update.getYearCurrentValue() != null) {
                            //取yyyy格式进行比较
                            String periodName_substring = codingRuleValue_for_update.getPeriodName().substring(0, 4);
                            String operationDate_substring = operationDate.substring(0, 4);

                            //当前时间与表里数据比较，true更新 false插入
                            if (periodName_substring.equals(operationDate_substring)) {
                                codingRuleValue.setYearCurrentValue(
                                    codingRuleValue_for_update.getYearCurrentValue()
                                        + codingRuleDetail.getIncremental()
                                );
                                codingRuleValue.setId(codingRuleValue_for_update.getId());

                                has_current_value = true;
                                //有则直接跳出当前循环
                                break;
                            }
                        }
                    }

                    //表中没有频率为每年的情况
                    if (codingRuleValues.size() == 0 || !has_current_value) {
                        codingRuleValue.setYearCurrentValue(codingRuleDetail.getStartValue());
                    }
                    temp_current_value = codingRuleValue.getYearCurrentValue();
                }

                int temp_current_value_length = ("" + temp_current_value).toCharArray().length;

                if (temp_current_value_length > codingRuleDetail.getLength()) {
                    throw new BizException(RespCode.BUDGET_CODING_RULE_CURRENT_VALUE_OVERFLOW);
                }

                int length = codingRuleDetail.getLength() - temp_current_value_length;

                //位数不到补0填充
                for (int i = 0; i < length; i++) {
                    orderNumber += "0";
                }
                orderNumber += temp_current_value;
            }
        }

        //单据编号不能超过30为
        if (orderNumber.length() > 30) {
            throw new BizException(RespCode.BUDGET_CODING_RULE_ORDER_NUMBER_LENGTH_NO_MORE_THAN_30);
        }

        codingRuleValueService.insertOrUpdate(codingRuleValue);

        return orderNumber;
    }

}

package com.hand.hcf.app.expense.input.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @description 从报账单取其行数据的DTO
 * @Version: 1.0
 * @author: ShilinMao
 * @date: 2019/3/4 11:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpInputForReportDistDTO {

  private Long id;

  private Long inputTaxLineId;

  private Long expReportDistId;

  private String selectFlag;


  private Long tenantId;

  protected Long setOfBooksId;

  private Long companyId;

  private Long departmentId;

  private Long responsibilityCenterId;


  private String  currencyCode;

  private Long rate;

  private BigDecimal baseAmount;

  private BigDecimal baseFunctionAmount;

  /**
   * 报账单分摊行分摊金额
   */
  private BigDecimal distAmount;
  /**
   * 报账单分摊行分摊税额
   */
  private BigDecimal distTaxAmount;





}

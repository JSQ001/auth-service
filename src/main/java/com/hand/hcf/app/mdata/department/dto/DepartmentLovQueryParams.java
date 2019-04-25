package com.hand.hcf.app.mdata.department.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentLovQueryParams {
   private Long companyId;
   private String departmentCode;
   private String departmentCodeFrom;
   private String departmentCodeTo;
   private String departmentName;
   private Long id;
   private String codeName;
   private Long tenantId;
   private Integer status;
   private Boolean associateCompanyFlag;
   private Long setOfBooksId;
   /**
    * 部门ID集合
    */
   private List<Long> ids;
   /**
    * 数据权限
    */
   private String dataAuthLabel;
}

package com.hand.hcf.app.base.user.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 *  员工导入domain
 *
 *  @Author yuan.liu
 */

@Data
@NoArgsConstructor
@TableName("sys_user_import_temp")
public class UserTempDomain extends Domain {

    @TableField("row_number")
    private String rowNumber;

    @TableField("employee_id")
    private String employeeId;

    @TableField("full_name")
    private String fullName;

    @TableField("company_code")
    private String companyCode;

    @TableField("company_id")
    private Long companyId;

    @TableField("department_code")
    private String departmentCode;

    @TableField("department_id")
    private Long departmentId;

    @TableField("email")
    private String email;

    @TableField("mobile_area_code")
    private String mobileAreaCode;

    @TableField("mobile")
    private String mobile;

    @TableField("direct_manager_id")
    private String directManagerId;

    @TableField("direct_manager")
    private UUID directManager;

    @TableField("duty_code")
    private String dutyCode;

    @TableField("duty")
    private String duty;

    @TableField("title")
    private String title;

    @TableField("employee_type_code")
    private String employeeTypeCode;

    @TableField("employee_type")
    private String employeeType;

    @TableField("rank_code")
    private String rankCode;

    @TableField("rank")
    private String rank;

    @TableField("gender_code")
    private String genderCode;

    @TableField("gender")
    private Integer gender;

    @TableField("birthday_str")
    private String birthdayStr;

    @TableField("birthday")
    private ZonedDateTime birthday;

    @TableField("entry_date_str")
    private String entryDateStr;

    @TableField("entry_date")
    private ZonedDateTime entryDate;

    @TableField("user_oid")
    private UUID userOid;

    @TableField("login")
    private String login;

    //临时表字段
    @TableField("batch_number")
    private String batchNumber ;

    @TableField("error_detail")
    private String errorDetail;

    @TableField("error_flag")
    private Boolean errorFlag;

}

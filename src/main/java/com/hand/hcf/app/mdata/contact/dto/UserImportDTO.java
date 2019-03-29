package com.hand.hcf.app.mdata.contact.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class UserImportDTO {
    private String departmentPath;
    private String mobile;
    @Size(max = 100)
    private String fullName;
    private String employeeId;
    private String email;
    private Boolean isManager;
    private String title;
    private String corporation;
    private ZonedDateTime leavingDate;
    //员工状态
    private Integer status;
    private String departmentName;  //部门名称
    private ZonedDateTime entryTime;     //入职日期
    private ZonedDateTime birthday;      //生日
    private String gender;          //性别
    /**
     * 性别编码
     */
    private String genderCode;
    private String employeeType;    //人员类型
    /**
     * 人员类型编码
     */
    private String employeeTypeCode;
    private String duty;            //职位
    /**
     * 职位编码
     */
    private String dutyCode;

    private String rank;            //职级
    /**
     * 职级编码
     */
    private String rankCode;
    private String companyName;//生成账户
    private String countryCode;

    private UUID directManager;//直属领导
    private String directManagerId;
    private String directManagerName;
    private String dataSource;  // 数据来源

    public UserImportDTO() {
    }

    public UserImportDTO(String departmentPath, String mobile, String fullName, String employeeId, String email, boolean isManager, String title,
                         String corporation,
                         String companyName,
                         ZonedDateTime leavingDate, Integer status, String departmentName, ZonedDateTime entryTime, ZonedDateTime birthday, String gender, String employeeType, String duty, String rank, String directManagerId,
                         String dataSource) {
        this.departmentPath = departmentPath;
        this.mobile = mobile;
        this.fullName = fullName;
        this.employeeId = employeeId;
        this.email = email;
        this.isManager = isManager;
        this.title = title;
        this.corporation = corporation;
        this.companyName = companyName;
        this.leavingDate = leavingDate;
        this.status = status;
        this.departmentName = departmentName;
        this.entryTime = entryTime;
        this.birthday = birthday;
        this.gender = gender;
        this.employeeType = employeeType;
        this.duty = duty;
        this.rank = rank;
        this.directManagerId = directManagerId;
        this.dataSource = dataSource;
    }
}

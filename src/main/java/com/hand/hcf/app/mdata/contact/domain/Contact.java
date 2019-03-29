package com.hand.hcf.app.mdata.contact.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * A Contact.
 */
@Data
@TableName( "sys_contact")
public class Contact extends Domain {
    private static final long serialVersionUID = -2930278358725883708L;


    @NotNull
    @Size(max = 100)
    private String fullName;

    private String firstName;

    private String lastName;

    private String email;

    @Size(max = 20)
    @TableField( "employee_id")
    private String employeeId;

    @Size(max = 100)
    private String title;

    @TableField( "corporation_oid")
    private UUID corporationOid;

    @NotNull
    private Boolean senior = false;

    private UUID headPortrait;

    /**
     * qi.yang 2017.01.10 新加人员字段
     */
    @TableField(strategy = FieldStrategy.IGNORED)
    private Integer gender;         //性别
    /**
     * 性别编码
     */
    @TableField(strategy = FieldStrategy.IGNORED)
    private String genderCode;
    private ZonedDateTime birthday;      //生日
    @TableField(strategy = FieldStrategy.IGNORED)
    private String duty;            //职务
    @TableField(strategy = FieldStrategy.IGNORED)
    private String employeeType;    //人员类型
    private ZonedDateTime entryDate;     //入职日期

    // qi.yang 2017.04.01 add
    @TableField(strategy = FieldStrategy.IGNORED)
    private String rank;    //职级

    @TableField(strategy = FieldStrategy.IGNORED)
    private UUID directManager;//直属领导工号
    /**
     * 职务编码
     */
    @TableField(strategy= FieldStrategy.IGNORED)
    private String dutyCode;

    @TableField(strategy= FieldStrategy.IGNORED)
    private String employeeTypeCode;

    @TableField(strategy= FieldStrategy.IGNORED)
    private String rankCode;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户OID
     */
    private UUID userOid;

    /**
     * 员工状态
     */
    private Integer status;

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 离职日期
     */
    private ZonedDateTime leavingDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Contact{" +
            "id=" + id +
            ", fullName='" + fullName + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", employeeId='" + employeeId + '\'' +
            ", title='" + title + '\'' +
            ", corporationOid=" + corporationOid +
            ", getSenior=" + senior +
            ", headPortrait=" + headPortrait +
            ", gender=" + gender +
            ", birthday=" + birthday +
            ", duty='" + duty + '\'' +
            ", employeeType='" + employeeType + '\'' +
            ", entryDate=" + entryDate +
            ", rank='" + rank + '\'' +
            '}';
    }
}

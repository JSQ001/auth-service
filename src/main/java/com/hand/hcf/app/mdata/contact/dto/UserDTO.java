package com.hand.hcf.app.mdata.contact.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hand.hcf.app.mdata.contact.domain.Phone;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


/**
 * A DTO for the Contact entity.
 */
@Data
public class UserDTO implements Serializable {

    /**
     * 用户ID
     */
    private Long id;

    private UUID userOid;

    /**
     * 员工ID
     */
    private Long contactId;

    @NotNull
    @Size(max = 100)
    private String fullName;

    private String firstName;

    private String lastName;

    @NotNull
    @Size(max = 20)
    private String mobile;

    @Size(max = 255)
    @Pattern(regexp = "^(([a-zA-Z]|[0-9])|([-]|[_]|[.]))+[@](([a-zA-Z0-9])|([-])){2,63}([.](([a-zA-Z0-9]){2,63})){1,2}$")
    private String email;

    @Size(max = 20)
    private String employeeId;

    @Size(max = 100)
    private String title;

    private Boolean senior;

    private Set<Phone> phoneDTOSet;

    private String lastUpdatedBy;

    private ZonedDateTime lastUpdatedDate;

    private UUID companyOid;

    private Long companyId;

    private String entryDate;
    private String birthday;      //生日

    private String mobileStatus;
    private String countryCode;
    private Set<Phone> phones;

    private Integer gender;
    private String genderCode;
    private String employeeType;    //人员类型
    private String employeeTypeCode;
    @JsonProperty(value = "rank")
    private String rankInfo;    //职级
    private String rankCode;

    private String duty;
    /**
     * 职务编码
     */
    private String dutyCode;

    private UUID departmentOid;

    private Long departmentId;

    private String departmentName;

    private String departmentPath;

    private String filePath;

    @JsonIgnore
    private UUID avatarOid;
    private String avatar;

    //状态
    private Integer status;

    private String companyName;
    private String companyAccountCode;

    //法人实体
    private UUID corporationOid;
    private String corporationName;
    private String legalEntityName;

    private Long tenantId;

    private UUID directManager;

    private String directManagerId;

    private String directManagerName;
    private Long setOfBooksId;
    private String setOfBooksName;

    /**
     * 离职日期
     */
    private ZonedDateTime leavingDate;

    private ZonedDateTime createdDate = ZonedDateTime.now();

    private UUID headPortrait;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserDTO contactDTO = (UserDTO) o;

        return Objects.equals(id, contactDTO.id);

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

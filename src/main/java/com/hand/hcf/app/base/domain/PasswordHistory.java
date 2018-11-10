package com.hand.hcf.app.base.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Created by Transy on 2017/5/17.
 */
@Data
@TableName(value = "art_password_history")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordHistory {
  //  @TableField(value = "user_oid")
   /* @NotNull
    @Type(type = "uuid-char")
    @Column(name = "user_oid", length = 36, nullable = false)*/
    @TableField(value = "user_oid")
    protected UUID  userOID;


    @TableField(value = "password")
    protected String password;

    @CreatedBy
    @NotNull
    @TableField(value = "created_by")
    @JsonIgnore
    private String createdBy;

    @LastModifiedBy
    @TableField(value = "last_modified_by")
    @JsonIgnore
    private String lastModifiedBy;

    @TableId(value = "id")
    private Long id;
    /**
     * 创建时间
     */
    @TableField(value = "created_date", fill = FieldFill.INSERT)
    protected DateTime createdDate;
    /**
     * 最后更改时间
     */
    @TableField(value = "last_modified_date", fill = FieldFill.INSERT_UPDATE)
    protected DateTime lastModifiedDate;
}

package com.hand.hcf.app.base.user.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.base.user.enums.SMSTokenType;
import com.hand.hcf.app.core.domain.BaseObject;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@TableName( "sys_sms_token")
public class SMSToken extends BaseObject {

    @TableField( "user_oid")
    private UUID userOid;
    @NotNull
    @TableField( "type_id")
    private Integer typeID;
    @NotEmpty
    @Size(max = 6)
    private String tokenValue;
    @NotNull
    @Column(name = "expire_time")
    private ZonedDateTime expireTime;
    @NotNull
    private String toUser;

    public SMSTokenType getSMSTokenType() {
        return SMSTokenType.parse(this.typeID);
    }

    public void setSMSTokenType(SMSTokenType type) {
        this.typeID = type.getId();
    }
}

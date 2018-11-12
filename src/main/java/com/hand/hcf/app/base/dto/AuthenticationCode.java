package com.hand.hcf.app.base.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author qingsheng.chen
 * @date 2017/12/29 11:45
 * @description 扫码登录
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("all")
public class AuthenticationCode implements Serializable {
    /**
     * UUID刚生成
     */
    @JsonIgnore
    public static final String INITIAL = "INITIAL";
    /**
     * 用户已经扫描，但此时还未确定登录
     */
    @JsonIgnore
    public static final String WAITING = "WAITING";
    /**
     * 用户确定登录
     */
    @JsonIgnore
    public static final String LOGGED = "LOGGED";

    /**
     * 用于标识唯一
     */
    private String uuid;

    /**
     * 当前状态
     */
    private String status;

    /**
     * 当前扫码的用户名
     */
    private String username;

    /**
     * 当前扫码的用户的公司名
     */
    private String companyName;

    @JsonIgnore
    private boolean returnWaiting = false;

    @Setter
    private UUID userOID;

    public String getUuid() {
        return uuid;
    }

    public AuthenticationCode setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public AuthenticationCode setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public AuthenticationCode setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public AuthenticationCode setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public boolean isReturnWaiting() {
        return returnWaiting;
    }

    public AuthenticationCode setReturnWaiting(boolean returnWaiting) {
        this.returnWaiting = returnWaiting;
        return this;
    }
}

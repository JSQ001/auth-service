/*
 * #{copyright}#
 */
package com.hand.hcf.app.workflow.dto;

import com.baomidou.mybatisplus.plugins.Page;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hand.hcf.app.workflow.util.RespCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 通用rest数据返回对象.
 *
 * @author yunfei.ma
 */
@Getter
@Setter
@JsonPropertyOrder({"success","code", "message", "rows", "total"})
public class ResponseDataDTO<T> {

    // 返回状态编码
    @JsonInclude(Include.NON_NULL)
    private String code = RespCode.SYS_SUCCESS;

    // 返回信息
    @JsonInclude(Include.NON_NULL)
    private String message;

    //数据
    @JsonInclude(Include.NON_NULL)
    private T rows;

    // 成功标识
    private boolean success = true;

    //总数
    @JsonInclude(Include.NON_NULL)
    private Integer total;

    public ResponseDataDTO() {
    }

    public ResponseDataDTO(boolean success) {
        setSuccess(success);
    }

    public ResponseDataDTO(T body){
        this.success = true;
        this.rows = body;
    }

    public ResponseDataDTO(T body, int total){
        this.success = true;
        this.rows = body;
        this.total = total;
    }

    public ResponseDataDTO(T body, String code, String message, boolean success){
        this.rows = body;
        this.code = code;
        this.message = message;
        this.success = success;
    }

    public static ResponseDataDTO ok(){
        return new ResponseDataDTO(true);
    }

    public static <T> ResponseDataDTO ok(T body){
        if(body instanceof Page){
            return new ResponseDataDTO(((Page) body).getRecords(),Math.toIntExact(((Page) body).getTotal()));
        }
        return new ResponseDataDTO(body);
    }

    public static <T> ResponseDataDTO<T> error(T result, String code, String message){
        return new ResponseDataDTO(result,code,message,false);
    }

    @Override
    public String toString() {
        return "ResponseDataDTO{" +
            "code='" + code + '\'' +
            ", message='" + message + '\'' +
            ", rows=" + rows +
            ", success=" + success +
            ", total=" + total +
            '}';
    }
}

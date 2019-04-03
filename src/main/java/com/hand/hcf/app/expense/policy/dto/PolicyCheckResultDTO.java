package com.hand.hcf.app.expense.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * 费用政策校验返回结果DTO
 * @author shouting.cheng
 * @date 2019/2/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyCheckResultDTO implements Serializable {
    /**
     * 通过标记
     */
    private Boolean passFlag = true;
    /**
     * 校验返回码
     */
    private String code;
    /**
     * 校验说明信息
     */
    private String message;

    public static PolicyCheckResultDTO ok(){
        return PolicyCheckResultDTO.builder().passFlag(true).build();
    }

    public static PolicyCheckResultDTO error(String code, String message){
        return PolicyCheckResultDTO.builder().passFlag(false).code(code).message(message).build();
    }

}

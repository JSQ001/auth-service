package com.hand.hcf.app.base.user.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 *  修改密码dto
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("密码修改dto")
public class PasswordUpdateDTO {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}

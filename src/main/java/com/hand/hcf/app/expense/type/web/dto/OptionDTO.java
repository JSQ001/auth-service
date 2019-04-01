package com.hand.hcf.app.expense.type.web.dto;

import com.hand.hcf.app.base.org.SysCodeValueCO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *  值列表值信息。申请类型/费用类型field使用
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO implements Serializable {

    private String value;

    private String label;

    public static OptionDTO createOption(SysCodeValueCO sysCodeValueCO){
        if (null == sysCodeValueCO){
            return null;
        }
        OptionDTO optionDTO = new OptionDTO();
        optionDTO.setLabel(sysCodeValueCO.getName());
        optionDTO.setValue(sysCodeValueCO.getValue());
        return optionDTO;
    }
}

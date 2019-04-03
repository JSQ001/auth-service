package com.hand.hcf.app.base.lov.web.dto;

import com.hand.hcf.app.base.lov.domain.Lov;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/3/22
 */
@Data
public class LovColumnInfoDTO implements Serializable {
    private Long id;
    private String name;
}

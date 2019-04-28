package com.hand.hcf.app.common.co;

import com.hand.hcf.app.core.security.domain.PrincipalLite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarryMessageCO implements Serializable {
    private PrincipalLite userBean;
    private Long tenantId;
    private Map<String, Object> dataMap;
}

package com.hand.hcf.app.core.web.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/10/19
 */
@Data
public class ImportResultDTO implements Serializable {

    private Integer successEntities;

    private Integer failureEntities;

    private List<ImportErrorDTO> errorData;
}

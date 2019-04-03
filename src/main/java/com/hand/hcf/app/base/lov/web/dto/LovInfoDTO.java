package com.hand.hcf.app.base.lov.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/3/23
 */
@Data
public class LovInfoDTO implements Serializable {
    private String title;
    private String url;
    private List<SearchColumnDTO> searchForm;
    private List<ColumnDTO> columns;
    private String method;
    @JsonIgnore
    private String requestColumn;
    @JsonIgnore
    private String responseColumn;
}

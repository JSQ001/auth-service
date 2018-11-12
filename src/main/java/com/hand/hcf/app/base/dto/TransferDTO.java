package com.hand.hcf.app.base.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/9/10.
 */
@Data
public class TransferDTO {
    //需要处理的多语言
    private List<String> languages;
    //需要转换的目标语言
    private String to;
}

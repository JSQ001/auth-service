package com.hand.hcf.app.workflow.dto;

import com.hand.hcf.app.core.domain.enumeration.LanguageEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by micro on 2017/10/30.
 * 固定条件分类使用
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FormValueI18nDTO implements Serializable {

    private String fieldName;
    private UUID fieldOid;
    private String messageKey;
    /**
     * 当前语言
     */
    private String language= LanguageEnum.ZH_CN.getKey();



}

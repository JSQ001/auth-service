package com.hand.hcf.app.core.domain;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class BaseI18nMetaData {

    private String field;
    private String language;
    private String value;

}

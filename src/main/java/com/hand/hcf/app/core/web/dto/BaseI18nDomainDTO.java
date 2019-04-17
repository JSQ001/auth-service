package com.hand.hcf.app.core.web.dto;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BaseI18nDomainDTO extends DomainObjectDTO {

    protected Map<String, List<Map<String, String>>> i18n;
}

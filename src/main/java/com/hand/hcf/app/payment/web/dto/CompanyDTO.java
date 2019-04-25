package com.hand.hcf.app.payment.web.dto;

import com.hand.hcf.app.core.web.dto.DomainObjectDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Date: Created in 16:04 2018/1/23
 * @Modified by
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO extends DomainObjectDTO {

    private Long companyId;

    private String companyCode;

    private String companyName;

    private String companyTypeName;

    private Integer versionNumber;

    private Long setOfBooksId;

    private String setOfBooksName;

    private String setOfBooksCode;

}
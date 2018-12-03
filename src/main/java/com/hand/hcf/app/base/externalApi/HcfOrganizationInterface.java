package com.hand.hcf.app.base.externalApi;

import com.hand.hcf.app.client.org.CustomEnumerationItemDTO;
import com.hand.hcf.app.client.org.OrganizationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/11/27 09:51
 * @remark 组织架构第三方接口
 */
@Service
@AllArgsConstructor
public class HcfOrganizationInterface {

    private final OrganizationService organizationService;

    /**
     * 获取值列表信息
     * @param typeId
     * @return
     */
    public List<CustomEnumerationItemDTO> getSysCodeValues(String typeId){
        return organizationService.getSysCodeValues(typeId,null);
    }
}

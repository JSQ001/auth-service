package com.hand.hcf.app.mdata.area.web;


import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.area.dto.InternationalAreaDTO;
import com.hand.hcf.app.mdata.area.service.AreaService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.system.constant.Constants;
import com.hand.hcf.app.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/*import com.hand.hcf.app.client.constant.Constants;*/

//import org.springframework.data.domain.Page;

/**
 * REST controller for managing Province.
 */
@RestController
@RequestMapping("/api")
public class AreaResource {

    @Autowired
    private AreaService areaService;

    /**
     * @api {GET} /api/areas/international/list 按照国家，省，市，区维度查询国际城市级别，地点组交集
     * @apiGroup Area
     * @apiParam {String} [type] 查询维度类型 （country:国家,stage:州/省,city:城市,district:地区）
     * @apiParam {String} [language] 多语言类型，默认zh_cn
     * @apiParam {String} [vendorType] 供应商类型 ，默认standard
     * @apiParam {String} [code] 上级编码
     * @apiParam {UUID} [levelOid] 地点组Oid,与搜索条件交集组合使用
     * @apiParam {Integer} [page] 起始页
     * @apiParam {Integer} [size] 每页记录数
     * @apiSuccessExample: SUCCESS-RESPONSE:
     * [
     * {
     * "code": "CHN011000000",
     * "type": "CITY/STATE",
     * "country": "中国",
     * "state": "北京",
     * "city": null,
     * "district": null,
     * "vendorType": "standard"
     * },
     * {
     * "code": "CHN012000000",
     * "type": "CITY/STATE",
     * "country": "中国",
     * "state": "天津",
     * "city": null,
     * "district": null,
     * "vendorType": "standard"
     * }
     * ]
     * @apiUse InternationalAreaDTO
     */
    @RequestMapping(value = "/areas/international/list",
        method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<InternationalAreaDTO>> findInternationalStagesByCountryCode(@RequestParam("type") String type,
                                                                                           @RequestParam(value = "languages", required = false) String languages,
                                                                                           @RequestParam(value = "vendorType", required = false) String vendorType,
                                                                                           @RequestParam(value = "code", required = false) String code,
                                                                                           @RequestParam(value = "levelOid", required = false) UUID levelOid,
                                                                                           @RequestParam(value = "roleType", required = false) String roleType,
                                                                                           Pageable pageable)  {
        Page page = PageUtil.getPage(pageable);
        boolean isTenant = false;
        if (roleType != null && Constants.ROLE_TENANT.equals(roleType)) {
            isTenant = true;
        }
        List<InternationalAreaDTO> result =
            areaService.findAllInternalAreasByParentCode(OrgInformationUtil.getCurrentTenantId(),  !StringUtils.isEmpty(languages) ? languages : LanguageEnum.ZH_CN.getKey(), vendorType,
                code, type, levelOid, isTenant, OrgInformationUtil.getCurrentCompanyOid(),pageable);
        return new ResponseEntity<>(result, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

}

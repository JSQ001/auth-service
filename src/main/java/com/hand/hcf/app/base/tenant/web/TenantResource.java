package com.hand.hcf.app.base.tenant.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.tenant.domain.Tenant;
import com.hand.hcf.app.base.tenant.dto.TenantDTO;
import com.hand.hcf.app.base.tenant.dto.TenantRegisterDTO;
import com.hand.hcf.app.base.tenant.service.TenantService;
import com.hand.hcf.app.base.user.service.UserService;
import com.hand.hcf.core.security.AuthoritiesConstants;
import com.hand.hcf.core.service.BaseI18nService;
import com.hand.hcf.core.util.LoginInformationUtil;
import com.hand.hcf.core.util.PageUtil;
import com.hand.hcf.core.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/tenant")
@RestController
public class TenantResource {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private BaseI18nService baseI18nService;

    @Autowired
    UserService userService;



    @RequestMapping(value = "/upload/logo/{tenantId}", method = RequestMethod.POST)
    public ResponseEntity<Tenant> uploadTenantLogo(@PathVariable("tenantId") Long tenantId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok().body(tenantService.uploadTenantLogo(null, tenantId, file));
    }

    @RequestMapping(value = "/update/tenantLogo", method = RequestMethod.POST)
    public ResponseEntity<Tenant> updateTenantLogo(@RequestBody TenantDTO tenantDTO) {
        return ResponseEntity.ok().body(tenantService.updateTenantLogo(tenantDTO));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody TenantRegisterDTO register){
        return ResponseEntity.ok(tenantService.registerTenant(register));
    }
    /**
     * @api {GET} /api/tenant/getById 根据id获取租户实体
     * @apiGroup Tenant
     * @apiParam {Long} tenantId 租户Id
     * @apiSuccessExample {json} Success-Response:
     * {
            "i18n": null,
            "id": "907943971227361281",
            "createdDate": "2017-09-13T12:28:06Z",
            "lastUpdatedDate": "2017-09-13T04:28:06Z",
            "createdBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
            "lastUpdatedBy": null,
            "tenantName": "三全科技",
            "tenantShortName": null,
            "licenseLimit": null,
            "status": null,
            "enabled": true,
            "deleted": false
        }
     * @apiSuccess {Long} id 主键
     * @apiSuccess {DateTime} createdDate 创建时间
     * @apiSuccess {DateTime} lastUpdatedDate 最后修改时间
     * @apiSuccess {UUID} createdBy 创建人
     * @apiSuccess {UUID} lastUpdatedBy 最后修改人
     * @apiSuccess {String} tenantName 租户名
     * @apiSuccess {String} tenantShortName 租户简称
     * @apiSuccess {int} licenseLimit 租户挂载上限
     * @apiSuccess {String} status 租户状态
     * @apiSuccess {Boolean} enabled 是否启用
     * @apiSuccess {Boolean} deleted 是否删除
     * @param tenantId
     * @return Tenant
     */
    @RequestMapping(value = "/getById", method = RequestMethod.GET)
    public ResponseEntity<Tenant> findByTenantId(@RequestParam("tenantId") Long tenantId) {
        Tenant tenant = tenantService.findTenantById(tenantId);
        return ResponseEntity.ok(tenant);
    }


    @RequestMapping(value = "/i18n/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Tenant>> getTenantI18n(@PathVariable Long id) {

        return ResponseEntity.ok(baseI18nService.getI18nInfo(id, Tenant.class));
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured(AuthoritiesConstants.ROLE_TENANT_ADMIN)
    public ResponseEntity<Tenant> getTenant(){
        Tenant tenant=userService.findCurrentTenantByUSerOid(LoginInformationUtil.getCurrentUserOid());
        return ResponseEntity.ok(tenant);
    }

    /**
     * 根据付费标记和租户名称[模糊]分页查询
     * @param licensed 付费标记
     * @param keyword 租户名称
     * @param pageable 分页
     * @return 租户信息
     * @throws URISyntaxException 异常
     */
    @GetMapping(value = "/valid/all", produces = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize(("hasRole('" + AuthoritiesConstants.INTEGRATION_CLIENTS + "')"))
    public ResponseEntity<List<TenantDTO>> searchAllValidTenants(@RequestParam(value = "licensed", required = false) Boolean licensed,
                                                                   @RequestParam(value = "keyword", required = false) String keyword,
                                                                   Pageable pageable) throws URISyntaxException {
        Page<TenantDTO> page = tenantService.searchAllValidTenants(licensed, keyword, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tenant/valid/all");
        return new ResponseEntity<>(page.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 修改租户付费标记
     * @param tenantDTO 租户信息
     * @return 最新租户
     * @throws URISyntaxException 异常
     */
    @PutMapping(value = "/modify/license", produces = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize(("hasRole('" + AuthoritiesConstants.INTEGRATION_CLIENTS + "')"))
    public ResponseEntity<TenantDTO> modifyTenant(@RequestBody TenantDTO tenantDTO,
                                                    @RequestParam(value = "updateCascade",required = false) Boolean updateCascade) throws URISyntaxException{
        return ResponseEntity.ok(tenantService.modifyTenant(tenantDTO,updateCascade));
    }

    @GetMapping(value = "/by/one", produces = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize(("hasRole('" + AuthoritiesConstants.INTEGRATION_CLIENTS + "')"))
    public ResponseEntity<TenantDTO> findTenantById(@RequestParam("id") Long id) throws URISyntaxException {
        return ResponseEntity.ok(tenantService.getTenantDTOById(id));
    }

    /**
     * 租户定义条件分页查询
     * @param tenantName
     * @param tenantCode
     * @param fullName
     * @param mobile
     * @param email
     * @param login
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping(value = "/query/condition", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity listByCondition(@RequestParam(value = "tenantName", required = false) String tenantName,
                                                           @RequestParam(value = "tenantCode", required = false) String tenantCode,
                                                           @RequestParam(value = "employeeId", required = false) String employeeId,
                                                           @RequestParam(value = "fullName", required = false) String fullName,
                                                           @RequestParam(value = "mobile", required = false) String mobile,
                                                           @RequestParam(value = "email", required = false) String email,
                                                           @RequestParam(value = "login", required = false) String login,
                                                           Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<TenantDTO> result =null;// tenantService.listTenantDTOsByCondition(tenantName, tenantCode, employeeId, fullName, mobile, email, login, page);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tenant/query/condition");
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }



}

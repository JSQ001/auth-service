package com.hand.hcf.app.mdata.setOfBooks.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.dto.SetOfBooksDTO;
import com.hand.hcf.app.mdata.setOfBooks.dto.SetOfBooksPeriodDTO;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.mdata.system.constant.Constants;
import com.hand.hcf.app.core.service.BaseI18nService;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * Created by fanfuqiang 2018/11/21
 */
@RestController
@RequestMapping("/api/setOfBooks")
public class SetOfBooksResource {

    @Autowired
    private SetOfBooksService setOfBooksService;

    @Autowired
    private BaseI18nService baseI18nService;

    /**
     * @api {post} /api/setOfBooks 新建账套
     * @apiGroup SetOfBooks
     * @apiSuccess {Long} id   账套ID
     * @apiSuccess {String} setOfBooksCode   账套代码
     * @apiSuccess {String} setOfBooksName   账套名称
     * @apiSuccess {String} periodSetCode    会计期代码
     * @apiSuccess {String} functionalCurrencyCode   本位币
     * @apiSuccess {Long} accountSetId   科目表ID
     * @apiSuccess {Long} tenantId        租户ID
     * @apiParamExample {json} Request-Param:
     * {
     * "i18n": {
     * "setOfBooksName": [
     * {
     * "language": "zh_cn",
     * "value": "中文_name"
     * },
     * {
     * "language": "en",
     * "value": "en_name"
     * }
     * ]
     * },
     * "setOfBooksCode": "set_of_books_code_test",
     * "setOfBooksName": "set_of_books_name_test",
     * "periodSetCode": "period_set_code_test",
     * "functionalCurrencyCode": "BUD",
     * "accountSetId": 10001
     * }
     * @apiSuccessExample {json} Success-Result:
     * {
     * "i18n": {
     * "setOfBooksName": [
     * {
     * "language": "zh_cn",
     * "value": "中文_name"
     * },
     * {
     * "language": "en",
     * "value": "en_name"
     * }
     * ]
     * },
     * "id": "925904795066191874",
     * "createdDate": "2017-11-02T01:58:00Z",
     * "lastUpdatedDate": "2017-11-02T01:58:00Z",
     * "createdBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
     * "lastUpdatedBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
     * "setOfBooksCode": "set_of_books_code_test",
     * "setOfBooksName": "set_of_books_name_test",
     * "periodSetCode": "period_set_code_test",
     * "functionalCurrencyCode": "BUD",
     * "accountSetId": "10001",
     * "tenantId": 10001,
     * "enabled": null,
     * "deleted": null
     * }
     */
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SetOfBooks> addSetOfBooks(@RequestBody SetOfBooks setOfBooks) throws URISyntaxException {
        setOfBooks.setTenantId(OrgInformationUtil.getCurrentTenantId());
        return ResponseEntity.ok(setOfBooksService.addSetOfBooks(setOfBooks));
    }


    /**
     * @api {put} /api/setOfBooks 更新账套
     * @apiGroup SetOfBooks
     * @apiSuccess {Long} id   账套ID
     * @apiSuccess {String} setOfBooksCode   账套代码
     * @apiSuccess {String} setOfBooksName   账套名称
     * @apiSuccess {String} periodSetCode    会计期代码
     * @apiSuccess {String} functionalCurrencyCode   本位币
     * @apiSuccess {Long} accountSetId   科目表ID
     * @apiSuccess {Long} tenantId        租户ID
     * @apiSuccess {Boolean} isEnabled    启用标志
     * @apiParamExample {json} Request-Param:
     * {
     * "i18n": {
     * "setOfBooksName": [
     * {
     * "language": "zh_cn",
     * "value": "中文_name_test"
     * },
     * {
     * "language": "en",
     * "value": "en_name_test"
     * }
     * ]
     * },
     * "id":925904795066191874,
     * "setOfBooksCode": "set_of_books_code_test",
     * "setOfBooksName": "set_of_books_name_test",
     * "periodSetCode": "period_set_code_test",
     * "functionalCurrencyCode": "AMD",
     * "accountSetId": 20001
     * }
     * @apiSuccessExample {json} Success-Result:
     * {
     * "i18n": {
     * "setOfBooksName": [
     * {
     * "language": "zh_cn",
     * "value": "中文_name_test"
     * },
     * {
     * "language": "en",
     * "value": "en_name_test"
     * }
     * ]
     * },
     * "id": "925904795066191874",
     * "createdDate": null,
     * "lastUpdatedDate": null,
     * "createdBy": null,
     * "lastUpdatedBy": null,
     * "setOfBooksCode": "set_of_books_code_test",
     * "setOfBooksName": "set_of_books_name_test",
     * "periodSetCode": "period_set_code_test",
     * "functionalCurrencyCode": "AMD",
     * "accountSetId": "20001",
     * "tenantId": null,
     * "enabled": null,
     * "deleted": null
     * }
     */
    @RequestMapping(value = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SetOfBooks> updateSetOfBooks(@RequestBody SetOfBooks setOfBooks) throws URISyntaxException {
        return ResponseEntity.ok(setOfBooksService.updateSetOfBooks(setOfBooks));
    }


    //根据id逻辑删除

    /**
     * @api {delete} /api/setOfBooks/{id} 根据id逻辑删除账套
     * @apiGroup SetOfBooks
     * @apiParam {Long} id  待删除的id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> deleteSetOfBooks(@PathVariable Long id) {
        return ResponseEntity.ok(setOfBooksService.deleteSetOfBooks(id));
    }


    //账套查询 根据账套ID查询账套列表

    /**
     * @api {GET} /api/setOfBooks/{id} 根据账套ID查询账套
     * @apiGroup SetOfBooks
     * @apiParam {Long} [id] 账套ID
     * @apiSuccess {Object} SetOfBooks  账套实体
     * @apiSuccess {Long} id   账套ID
     * @apiSuccess {String} setOfBooksCode   账套代码
     * @apiSuccess {String} setOfBooksName   账套名称
     * @apiSuccess {String} periodSetCode    会计期代码
     * @apiSuccess {String} functionalCurrencyCode   本位币
     * @apiSuccess {Long}  accountSetId   科目表ID
     * @apiSuccess {Long} tenantId        租户ID
     * @apiSuccess {Boolean} enabled    启用标志
     * @apiSuccess {Boolean} deleted    删除标志
     * @apiSuccessExample {json} Success-Result
     * {
     * "i18n": {
     * "setOfBooksName": [
     * {
     * "language": "en",
     * "value": "en_name"
     * },
     * {
     * "language": "zh_cn",
     * "value": "中文_name"
     * }
     * ]
     * },
     * "id": "922640846015250433",
     * "createdDate": "2017-11-01T08:55:40Z",
     * "lastUpdatedDate": "2017-10-24T01:48:14Z",
     * "createdBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
     * "lastUpdatedBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
     * "setOfBooksCode": "DEFAULT_SOB",
     * "setOfBooksName": "默认账套",
     * "periodSetCode": "DEFAULT_CAL",
     * "functionalCurrencyCode": "AMD",
     * "accountSetId": "922640845113475074",
     * "tenantId": 10001,
     * "enabled": true,
     * "deleted": false
     * }
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SetOfBooks> findSetOfBooksById(@PathVariable Long id) throws URISyntaxException {
        return ResponseEntity.ok(setOfBooksService.findSetOfBooksById(id));
    }


    //分页查询 条件查询（根据账套代码和账套名称）

    /**
     * @api {GET} /api/setOfBooks/query 根据条件查询账套列表
     * @apiGroup SetOfBooks
     * @apiParam {String} [setOfBooksCode] 账套代码
     * @apiParam {String} [setOfBooksName] 账套名称
     * @apiParam {String} [roleType] 角色类型
     * @apiSuccess {Object[]} SetOfBooks  账套实体集合
     * @apiSuccess {Long} id   账套ID
     * @apiSuccess {String} setOfBooksCode   账套代码
     * @apiSuccess {String} setOfBooksName   账套名称
     * @apiSuccess {String} periodSetCode    会计期代码
     * @apiSuccess {String} functionalCurrencyCode   本位币
     * @apiSuccess {Long}  accountSetId   科目表ID
     * @apiSuccess {Long} tenantId        租户ID
     * @apiSuccess {Boolean} enabled    启用标志
     * @apiSuccess {Boolean} deleted    删除标志
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "i18n": {
     * "setOfBooksName": [
     * {
     * "language": "en",
     * "value": "英文"
     * },
     * {
     * "language": "zh_cn",
     * "value": "简体中文"
     * }
     * ]
     * },
     * "id": "922640846015250433",
     * "createdDate": "2017-11-01T08:55:40Z",
     * "lastUpdatedDate": "2017-10-24T01:48:14Z",
     * "createdBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
     * "lastUpdatedBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
     * "setOfBooksCode": "DEFAULT_SOB",
     * "setOfBooksName": "默认账套",
     * "periodSetCode": "DEFAULT_CAL",
     * "functionalCurrencyCode": "AMD",
     * "accountSetId": "922640845113475074",
     * "tenantId": 10001,
     * "enabled": true,
     * "deleted": false
     * }
     * ]
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SetOfBooks>> findSetOfBooksByCode(@RequestParam(required = false) String setOfBooksCode,
                                                                 @RequestParam(required = false) String setOfBooksName,
                                                                 @RequestParam(defaultValue = "true") Boolean enabled,
                                                                 @RequestParam(required = false) String roleType,
                                                                 Pageable pageable) throws URISyntaxException {
        Page page = new Page(pageable.getPageNumber() + 1, pageable.getPageSize());
        Page<SetOfBooks> result = setOfBooksService.findSetOfBooksByCode(setOfBooksCode, setOfBooksName, enabled, roleType, OrgInformationUtil.getCurrentTenantId(), page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/setOfBooks/query");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }


    //分页查询 账套DTO条件查询

    /**
     * @api {GET} /api/setOfBooks/query/dto 根据条件查询账套列表
     * @apiGroup SetOfBooksDTO
     * @apiParam {String} [setOfBooksCode] 账套代码
     * @apiParam {String} [setOfBooksName] 账套名称
     * @apiParam {String} [roleType] 角色类型
     * @apiSuccess {Object[]} SetOfBooksDTO  账套实体DTO集合
     * @apiSuccess {Long} setOfBooksId   账套ID
     * @apiSuccess {String} setOfBooksCode   账套代码
     * @apiSuccess {String} setOfBooksName   账套名称
     * @apiSuccess {Long} periodSetId        会计期ID
     * @apiSuccess {String} periodSetCode    会计期代码
     * @apiSuccess {Long}  accountSetId      科目表ID
     * @apiSuccess {String}  accountSetCode  科目表代码
     * @apiSuccess {String} functionalCurrencyCode   本位币
     * @apiSuccess {Boolean} enabled    启用标志
     * @apiSuccessExample {json} Success-Result
     * {
     * "setOfBooksId": "922640846015250433",
     * "setOfBooksCode": "DEFAULT_SOB",
     * "setOfBooksName": "默认账套",
     * "periodSetId": "922640846585675779",
     * "periodSetCode": "DEFAULT_CAL",
     * "accountSetId": "922640845113475074",
     * "accountSetCode": "DEFAULT_ACC",
     * "functionalCurrencyCode": AMD,
     * "enabled": true
     * }
     */
    @RequestMapping(value = "/query/dto", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SetOfBooksDTO>> findSetOfBooksDTO(@RequestParam(required = false) String setOfBooksCode,
                                                                 @RequestParam(required = false) String setOfBooksName,
                                                                 @RequestParam(required = false) Boolean enabled,
                                                                 @RequestParam(required = false) String roleType,
                                                                 Pageable pageable) throws URISyntaxException {
        Page page = new Page(pageable.getPageNumber() + 1, pageable.getPageSize());
        Page<SetOfBooksDTO> result = setOfBooksService.findSetOfBooksDTO(setOfBooksCode, setOfBooksName, enabled, roleType, OrgInformationUtil.getCurrentTenantId(), page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/setOfBooks/query/dto");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }


    /**
     * 头信息查询 账套期间查询
     *
     * @param setOfBooksId 账套ID
     * @param periodSetId  会计期代码
     * @return
     */
    @RequestMapping(value = "/query/head/dto", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SetOfBooksPeriodDTO> findSetOfBooksPeriodById(@RequestParam Long setOfBooksId, @RequestParam Long periodSetId) {
        return ResponseEntity.ok(setOfBooksService.findSetOfBooksPeriodById(setOfBooksId, periodSetId));
    }


    /**
     * 初始化账套信息
     *
     * @return
     */
    @RequestMapping(value = "/init", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @Timed
    // @PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Void> initSetOfBooks() {
        /*setOfBooksService.initSetOfBooks();*/
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/i18n/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, SetOfBooks>> getSetOfBooki18n(@PathVariable Long id) {

        return ResponseEntity.ok(baseI18nService.getI18nInfo(id, SetOfBooks.class));
    }

    @RequestMapping(value = "/by/tenant", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SetOfBooks>> getTenantAllSetOfBooks(@RequestParam(value = "roleType", required = false) String roleType) {
        List<SetOfBooks> result = null;
        if (roleType != null && Constants.ROLE_TENANT.equals(roleType)) {
            result = setOfBooksService.getListByTenantId(OrgInformationUtil.getCurrentTenantId());
        } else {
            result = setOfBooksService.getCompanyAvailableSetOfBooksId(OrgInformationUtil.getCurrentTenantId(), OrgInformationUtil.getCurrentCompanyId());
        }
        return ResponseEntity.ok(result);
    }


    @GetMapping("/selectSetOfBooksByUserOid")
    public ResponseEntity<SetOfBooks> selectSetOfBooksByUserOid(@RequestParam String userOid) {
        return ResponseEntity.ok(setOfBooksService.selectSetOfBooksByUserOid(userOid));
    }
}

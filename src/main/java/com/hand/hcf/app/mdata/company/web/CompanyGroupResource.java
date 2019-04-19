package com.hand.hcf.app.mdata.company.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.conver.CompanyGroupCover;
import com.hand.hcf.app.mdata.company.domain.CompanyGroup;
import com.hand.hcf.app.mdata.company.dto.CompanyGroupDTO;
import com.hand.hcf.app.mdata.company.service.CompanyGroupService;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by silence on 2017/9/18.
 */
@RestController
@RequestMapping("/api/company/group")
public class CompanyGroupResource {
    @Autowired
    private CompanyGroupService companyGroupService;
    @Autowired
    private SetOfBooksService setOfBooksService;

    /**
     * @api {post} /api/companyId/group 新增公司组
     * @apiGroup CompanyGroup
     * @apiSuccess {Long} id   公司组ID
     * @apiSuccess {String} companyGroupCode   公司组代码
     * @apiSuccess {String} companyGroupName   公司组描述
     * @apiSuccess {Long} setOfBooksId         账套ID
     * @apiSuccess {Boolean} enabled           启用标志
     * @apiParamExample {json} Request-Param:
     * {
     * "i18n": {
     * "companyGroupName": [
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
     * "companyGroupCode": "test_code_TEST",
     * "companyGroupName": "test_name_TEST",
     * "setOfBooksId": 925904795066191874,
     * "enabled": false
     * }
     * @apiSuccessExample {json} Success-Result
     * {
     * "i18n": {
     * "companyGroupName": [
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
     * "id": "925911824870797313",
     * "createdDate": "2017-11-02T02:25:56Z",
     * "lastUpdatedDate": "2017-11-02T02:25:56Z",
     * "createdBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
     * "lastUpdatedBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
     * "companyGroupCode": "test_code_TEST",
     * "companyGroupName": "test_name_TEST",
     * "description": null,
     * "setOfBooksId": 925904795066191874,
     * "tenantId": 10001,
     * "enabled": false,
     * "deleted": null
     * }
     */
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CompanyGroupDTO> addCompanyGroup(@RequestBody CompanyGroup companyGroup) throws URISyntaxException {

        final CompanyGroup result = companyGroupService.addCompanyGroup(companyGroup);
        //  转换DTO
        CompanyGroupDTO dto = CompanyGroupCover.toDTO(result);
        dto.setI18n(result.getI18n());
        //  set账套
        if (setOfBooksService.selectById(result.getSetOfBooksId()) != null) {
            SetOfBooks setOfBooks = setOfBooksService.selectById(result.getSetOfBooksId());
            dto.setSetOfBooksCode(setOfBooks.getSetOfBooksCode());
            dto.setSetOfBooksName(setOfBooks.getSetOfBooksName());
        }
        return ResponseEntity.ok(dto);
    }


    /**
     * @api {put} /api/companyId/group 更新公司组
     * @apiGroup CompanyGroup
     * @apiSuccess {Long} id   公司组ID
     * @apiSuccess {String} companyGroupCode   公司组代码
     * @apiSuccess {String} companyGroupName   公司组描述
     * @apiSuccess {Long} setOfBooksId         账套ID
     * @apiSuccess {Boolean} enabled           启用标志
     * @apiParamExample {json} Request-Param:
     * {
     * "id": 925911824870797313,
     * "companyGroupCode": "test_code_TEST",
     * "companyGroupName": "test_name_TEST",
     * "setOfBooksId": 925904795066191874
     * }
     * @apiSuccessExample {json} Success-Result
     * {
     * "i18n": null,
     * "id": "925911824870797313",
     * "createdDate": "2017-11-02T02:25:56Z",
     * "lastUpdatedDate": "2017-11-02T02:25:56Z",
     * "createdBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
     * "lastUpdatedBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
     * "companyGroupCode": "test_code_TEST",
     * "companyGroupName": "test_name_TEST",
     * "description": null,
     * "setOfBooksId": 925904795066191874,
     * "tenantId": 10001,
     * "enabled": true,
     * "deleted": false
     * }
     */
    @RequestMapping(value = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CompanyGroupDTO> updateCompanyGroup(@RequestBody CompanyGroup companyGroup) throws URISyntaxException {
        companyGroupService.updateCompanyGroup(companyGroup);
        CompanyGroupDTO result = companyGroupService.findTransCompanyGroupById(companyGroup.getId());
        return ResponseEntity.ok(result);
    }


    //根据id逻辑删除

    /**
     * @api {delete} /api/companyId/group/{id} 根据id逻辑删除公司组
     * @apiGroup CompanyGroup
     * @apiParam {Long} id  待删除的id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> deleteCompanyGroup(@PathVariable Long id) {
        return ResponseEntity.ok(companyGroupService.deleteCompanyGroup(id));
    }


    //公司组查询 通过公司组ID

    /**
     * @api {GET} /api/companyId/group/{id} 根据公司组ID查询公司组
     * @apiGroup CompanyGroup
     * @apiParam {Long} [id] 公司组ID
     * @apiSuccess {Object} CompanyGroup  公司组实体
     * @apiSuccess {Long} id   公司组ID
     * @apiSuccess {String} companyGroupCode   公司组代码
     * @apiSuccess {String} companyGroupName   公司组描述
     * @apiSuccess {Long} setOfBooksId         账套ID
     * @apiSuccess {Long} tenantId             租户ID
     * @apiSuccess {Boolean} enabled           启用标志
     * @apiSuccess {Boolean} deleted           删除标志
     * @apiSuccessExample {json} Success-Result
     * {
     * "i18n": {
     * "companyGroupName": [
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
     * "id": "912966380293246978",
     * "createdDate": "2017-09-27T09:05:22Z",
     * "lastUpdatedDate": "2017-09-27T09:05:22Z",
     * "createdBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
     * "lastUpdatedBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
     * "companyGroupCode": "test_code_001yy",
     * "companyGroupName": "test_name_001yy",
     * "description": null,
     * "setOfBooksId": 912229041627488258,
     * "tenantId": 10001,
     * "enabled": true,
     * "deleted": false
     * }
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CompanyGroupDTO> findCompanyGroupById(@PathVariable Long id) throws URISyntaxException {
        CompanyGroupDTO result = companyGroupService.findCompanyGroupById(id);
        return ResponseEntity.ok(result);
    }

    //公司组查询 通过公司组ID（多语言翻译后）

    /**
     * @api {GET} /api/companyId/group/trans/{id} 根据公司组ID查询公司组
     * @apiGroup CompanyGroup
     * @apiParam {Long} [id] 公司组ID
     * @apiSuccess {Object} CompanyGroup  公司组实体
     * @apiSuccess {Long} id   公司组ID
     * @apiSuccess {String} companyGroupCode   公司组代码
     * @apiSuccess {String} companyGroupName   公司组描述
     * @apiSuccess {Long} setOfBooksId         账套ID
     * @apiSuccess {Long} tenantId             租户ID
     * @apiSuccess {Boolean} enabled           启用标志
     * @apiSuccess {Boolean} deleted           删除标志
     * @apiSuccessExample {json} Success-Result
     * {
     * "i18n": {
     * "companyGroupName": [
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
     * "id": "912966380293246978",
     * "createdDate": "2017-09-27T09:05:22Z",
     * "lastUpdatedDate": "2017-09-27T09:05:22Z",
     * "createdBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
     * "lastUpdatedBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
     * "companyGroupCode": "test_code_001yy",
     * "companyGroupName": "中文_name",
     * "description": null,
     * "setOfBooksId": 912229041627488258,
     * "tenantId": 10001,
     * "enabled": true,
     * "deleted": false
     * }
     */
    @RequestMapping(value = "/trans/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CompanyGroupDTO> findTransCompanyGroupById(@PathVariable Long id) throws URISyntaxException {
        CompanyGroupDTO result = companyGroupService.findTransCompanyGroupById(id);
        return ResponseEntity.ok(result);
    }


    //分页查询 公司组条件查询 （根据公司组代码和公司组描述）

    /**
     * @api {GET} /api/companyId/group/query 公司组条件查询
     * @apiGroup CompanyGroup
     * @apiParam {Long} [setOfBooksId] 账套ID
     * @apiParam {String} [companyGroupCode] 公司组代码
     * @apiParam {String} [companyGroupName] 公司组描述
     * @apiSuccess {Object[]} CompanyGroup  公司组实体集合
     * @apiSuccess {Long} id   公司组ID
     * @apiSuccess {String} companyGroupCode   公司组代码
     * @apiSuccess {String} companyGroupName   公司组描述
     * @apiSuccess {Long} setOfBooksId         账套ID
     * @apiSuccess {Long} tenantId             租户ID
     * @apiSuccess {Boolean} enabled           启用标志
     * @apiSuccess {Boolean} deleted           删除标志
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "i18n": {
     * "companyGroupName": [
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
     * "id": "912966380293246978",
     * "createdDate": "2017-09-27T09:05:22Z",
     * "lastUpdatedDate": "2017-09-27T09:05:22Z",
     * "createdBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
     * "lastUpdatedBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
     * "companyGroupCode": "test_code_001yy",
     * "companyGroupName": "test_name_001yy",
     * "description": null,
     * "setOfBooksId": 912229041627488258,
     * "tenantId": 10001,
     * "enabled": true,
     * "deleted": false
     * }
     * ]
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyGroup>> findCompanyGroupByCode(@RequestParam(required = false) Long setOfBooksId,
                                                                     @RequestParam(required = false) String companyGroupCode,
                                                                     @RequestParam(required = false) String companyGroupName,
                                                                     Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<CompanyGroup> result = companyGroupService.findCompanyGroupByCode(setOfBooksId, companyGroupCode, companyGroupName, OrgInformationUtil.getCurrentTenantId(), page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/companyId/group/assign/query");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }


    //分页查询 启用状态的公司组DTO条件查询

    /**
     * @api {GET} /api/companyId/group/query/dto 公司组条件查询
     * @apiGroup CompanyGroupDTO
     * @apiParam {Long} [setOfBooksId] 账套ID
     * @apiParam {String} [companyGroupCode] 公司组代码
     * @apiParam {String} [companyGroupName] 公司组描述
     * @apiSuccess {Object[]} CompanyGroupDTO  公司组实体DTO集合
     * @apiSuccess {Long} id   公司组ID
     * @apiSuccess {String} companyGroupCode   公司组代码
     * @apiSuccess {String} companyGroupName   公司组描述
     * @apiSuccess {Long}   setOfBooksId       账套ID
     * @apiSuccess {String} setOfBooksCode     账套代码
     * @apiSuccess {Boolean} enabled           启用标志
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "id": "912966380293246978",
     * "companyGroupCode": "test_code_001yy",
     * "companyGroupName": "test_name_001yy",
     * "setOfBooksId": "912229041627488258",
     * "setOfBooksCode": "set_of_books_code_001skt",
     * "i18n": {
     * "companyGroupName": [
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
     * "enabled": false
     * }
     * ]
     */
    @RequestMapping(value = "/query/section/dto", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyGroupDTO>> findSectionCompanyGroupDTO(@RequestParam(required = false) Long setOfBooksId,
                                                                            @RequestParam(required = false) String companyGroupCode,
                                                                            @RequestParam(required = false) String companyGroupName,
                                                                            @RequestParam(required = false) Boolean enabled,
                                                                            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<CompanyGroupDTO> result = companyGroupService.findCompanyGroupByConditions(setOfBooksId, companyGroupCode, companyGroupName, enabled, OrgInformationUtil.getCurrentTenantId(), page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/companyId/group/assign/query/dto");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }


    //分页查询 公司组DTO条件查询

    /**
     * @api {GET} /api/companyId/group/query/dto 公司组条件查询
     * @apiGroup CompanyGroupDTO
     * @apiParam {Long} [setOfBooksId] 账套ID
     * @apiParam {String} [companyGroupCode] 公司组代码
     * @apiParam {String} [companyGroupName] 公司组描述
     * @apiSuccess {Object[]} CompanyGroupDTO  公司组实体DTO集合
     * @apiSuccess {Long} id   公司组ID
     * @apiSuccess {String} companyGroupCode   公司组代码
     * @apiSuccess {String} companyGroupName   公司组描述
     * @apiSuccess {Long}   setOfBooksId       账套ID
     * @apiSuccess {String} setOfBooksCode     账套代码
     * @apiSuccess {Boolean} enabled           启用标志
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "id": "912966380293246978",
     * "companyGroupCode": "test_code_001yy",
     * "companyGroupName": "test_name_001yy",
     * "setOfBooksId": "912229041627488258",
     * "setOfBooksCode": "set_of_books_code_001skt",
     * "i18n": {
     * "companyGroupName": [
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
     * "enabled": false
     * }
     * ]
     */
    @RequestMapping(value = "/query/dto", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyGroupDTO>> findCompanyGroupDTO(@RequestParam(required = false) Long setOfBooksId,
                                                                     @RequestParam(required = false) String companyGroupCode,
                                                                     @RequestParam(required = false) String companyGroupName,
                                                                     Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Long currentTenantID = OrgInformationUtil.getCurrentTenantId();
        Page<CompanyGroupDTO> result = companyGroupService.findCompanyGroupByConditions(setOfBooksId, companyGroupCode, companyGroupName, null, currentTenantID, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/companyId/group/assign/query/dto");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

}

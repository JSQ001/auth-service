package com.hand.hcf.app.mdata.company.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.domain.CompanyGroup;
import com.hand.hcf.app.mdata.company.domain.CompanyGroupAssign;
import com.hand.hcf.app.mdata.company.dto.CompanyDTO;
import com.hand.hcf.app.mdata.company.dto.CompanyGroupAssignDTO;
import com.hand.hcf.app.mdata.company.service.CompanyGroupAssignService;
import io.micrometer.core.annotation.Timed;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by silence on 2017/9/18.
 */
@RestController
@RequestMapping("/api/company/group/assign")
public class CompanyGroupAssignResource {

    @Autowired
    private CompanyGroupAssignService companyGroupAssignService;


    /**
     * @api {post} /api/companyId/group/assign 新建公司组分配明细
     * @apiGroup CompanyGroupAssign
     * @apiSuccess {Long} id   公司组分配明细ID
     * @apiSuccess {Long} companyGroupId   公司组ID
     * @apiSuccess {Long} companyId        公司ID
     * @apiSuccess {Long} tenantId         租户ID
     * @apiParamExample {json} Request-Param:
     * {
     * "companyGroupId": 925911824870797313,
     * "companyId": 1
     * }
     * @apiSuccessExample {json} Success-Result
     * {
     * "i18n": null,
     * "id": "925915799502196738",
     * "createdDate": "2017-11-02T02:41:43Z",
     * "lastUpdatedDate": "2017-11-02T02:41:43Z",
     * "createdBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
     * "lastUpdatedBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
     * "companyGroupId": 925911824870797313,
     * "companyId": 1,
     * "tenantId": 10001,
     * "enabled": null,
     * "deleted": null
     * }
     */
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CompanyGroupAssign> addCompanyGroupAssign(@RequestBody CompanyGroupAssign companyGroupAssign) throws URISyntaxException {
        return ResponseEntity.ok(companyGroupAssignService.addCompanyGroupAssign(companyGroupAssign));
    }


    //批量 新增公司组分配明细

    /**
     * @api {delete} /api/companyId/group/assign/batch 批量新增公司组分配明细
     * @apiGroup CompanyGroupAssign
     * @apiSuccess {Object[]} CompanyGroupAssign  公司组分配明细实体集合
     * @apiSuccess {Long} id   公司组分配明细ID
     * @apiSuccess {Long} companyGroupId   公司组ID
     * @apiSuccess {Long} companyId        公司ID
     * @apiSuccess {Long} tenantId         租户ID
     * @apiParam {Array} [CompanyGroupAssign]
     */
    @RequestMapping(value = "/batch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> addCompanyGroupAssignBatch(@RequestBody List<CompanyGroupAssign> list) throws URISyntaxException {
        return ResponseEntity.ok(companyGroupAssignService.addCompanyGroupAssignBatch(list));
    }


    //根据id逻辑删除

    /**
     * @api {delete} /api/companyId/group/assign/{id} 根据id逻辑删除公司组分配明细
     * @apiGroup CompanyGroupAssign
     * @apiParam {Long} id  待删除的id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> deleteCompanyGroupAssign(@PathVariable Long id) {
        return ResponseEntity.ok(companyGroupAssignService.deleteCompanyGroupAssign(id));
    }


    /**
     * 批量 删除公司组分配明细
     *
     * @param list  公司组分配明细ID的集合
     * @return ResponseEntity
     */
    /**
     * @api {delete} /api/companyId/group/assign/batch 批量删除公司组分配明细
     * @apiGroup CompanyGroupAssign
     * @apiParam {Array} [ids]  待删除的id数组
     */
    @RequestMapping(value = "/batch", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> deleteCompanyGroupAssignBatch(@RequestBody List<Long> list) {
        return ResponseEntity.ok(companyGroupAssignService.deleteCompanyGroupAssignBatch(list));
    }


    //分页查询 公司组分配明细DTO查询

    /**
     * @api {GET} /api/companyId/group/assign/query/dto 公司组条件查询
     * @apiGroup CompanyGroupAssignDTO
     * @apiParam {Long} [companyGroupId] 公司组ID
     * @apiSuccess {Object[]} CompanyGroupAssignDTO  公司组分配明细DTO集合
     * @apiSuccess {Long} id   公司组分配明细ID
     * @apiSuccess {Long} companyId    公司ID
     * @apiSuccess {Long} companyCode  公司代码
     * @apiSuccess {Long} companyName  公司名称
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "id": "911142448997150722",
     * "companyId": "2",
     * "companyCode": "Test_code_02",
     * "companyName": "上海汉得信息技术股份有限公司"
     * }
     * ]
     */
    @RequestMapping(value = "/query/dto", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyGroupAssignDTO>> findCompanyGroupAssignById(@RequestParam Long companyGroupId, Pageable pageable) throws URISyntaxException {
        Page page = new Page(pageable.getPageNumber() + 1, pageable.getPageSize());
        //  获取公司组下的公司
        Page<CompanyGroupAssign> result = companyGroupAssignService.findCompanyGroupAssignById(companyGroupId, page);
        List<CompanyGroupAssign> list = result.getRecords();
        //  创建DTO集合
        List<CompanyGroupAssignDTO> resultList = new ArrayList<CompanyGroupAssignDTO>();
        List<CompanyGroupAssignDTO> dtolist = new ArrayList<>();
        //  判断是否为空
        if (CollectionUtils.isNotEmpty(list)) {
            //  遍历
            list.stream().forEach((CompanyGroupAssign companyGroupAssign) -> {
                resultList.add(companyGroupAssignService.transCompanyGroupAssignDTO(companyGroupAssign));
            });
            dtolist = resultList.stream().sorted(Comparator.comparing(CompanyGroupAssignDTO::getCompanyCode)).collect(Collectors.toList());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/companyId/group/assign/query/dto");
        return new ResponseEntity<>(dtolist, headers, HttpStatus.OK);
    }


    /**
     * 预算项目查询 根据公司ID查询公司组
     *
     * @param companyId
     * @return ResponseEntity
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/query/budget/group/{companyId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyGroup>> findCompanyGroupByCompanyId(@PathVariable Long companyId) throws URISyntaxException {
        return ResponseEntity.ok(companyGroupAssignService.findCompanyGroupByCompanyId(companyId));
    }


    /**
     * 预算项目查询 根据公司组ID查询公司
     *
     * @param companyGroupId
     * @return ResponseEntity
     */
    @RequestMapping(value = "/query/budget/company/dto/{companyGroupId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyGroupAssignDTO>> findCompanyGroupByCompanyGroupId(@PathVariable Long companyGroupId) throws URISyntaxException {
        //  获取公司组下的公司
        List<CompanyGroupAssign> list = companyGroupAssignService.findCompanyGroupByCompanyGroupId(companyGroupId);
        return ResponseEntity.ok(companyGroupAssignService.CompanyGroupAssignAdapter(list));
    }


    /**
     * 预算项目查询 根据公司ID集合查询公司信息
     *
     * @param list
     * @return ResponseEntity
     * @throws URISyntaxException
     */
//    @RequestMapping(value = "/query/budget/companies", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Timed
//    public ResponseEntity<List<BudgetInfoDTO>> findCompanyById(@RequestBody List<Long> list) throws URISyntaxException {
//        return ResponseEntity.ok(companyGroupAssignService.findCompanyById(list));
//    }


    /**
     * 预算项目查询 根据公司组ID集合查询每个ID下的公司
     *
     * @param list
     * @return ResponseEntity
     */
//    @RequestMapping(value = "/query/budget/companyId/groups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Timed
//    public ResponseEntity<List<BudgetInfoDTO>> findCompaniesByGroupId(@RequestBody List<Long> list) throws URISyntaxException {
//        return ResponseEntity.ok(companyGroupAssignService.findCompaniesByGroupId(list));
//    }


    /**
     * 预算项目查询 单个公司ID查询公司
     *
     * @param companyId
     * @return ResponseEntity
     * @throws URISyntaxException
     */
//    @RequestMapping(value = "/query/budget/companyId/{companyId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Timed
//    public ResponseEntity<CompanyGroupAssignBudgetDTO> findCompanyById(@PathVariable Long companyId) throws URISyntaxException {
//        //  查询公司
//        Company companyId = companyGroupAssignService.findOneCompanyById(companyId);
//        //  转化DTO
//        CompanyGroupAssignBudgetDTO dto = CompanyGroupAssignBudgetCover.toDTO(companyId);
//        //  set字段的Name值
//        if (dto.getSetOfBooksId() != null) {
//            dto.setSetOfBooksName(setOfBooksService.selectById(dto.getSetOfBooksId()).getSetOfBooksName());
//        }
//        if (dto.getLegalEntityId() != null) {
//            dto.setLegalEntityName(legalEntityService.getLegalEntity(dto.getLegalEntityId()).getEntityName());
//        }
//        if (dto.getCompanyLevelId() != null) {
//            dto.setCompanyLevelName(companyLevelService.selectById(dto.getCompanyLevelId()).getDescription());
//        }
//        if (dto.getParentCompanyId() != null) {
//            dto.setParentCompanyName(companyService.findOne(dto.getParentCompanyId()).getName());
//        }
//        if (dto.getCompanyTypeId() != null) {
//            SysCodeValueDTO customEnumerationItemDTO = customEnumerationService.getCustomEnumerationItemId(dto.getCompanyTypeId());
//            if (null != customEnumerationItemDTO) {
//                dto.setCompanyTypeName(customEnumerationItemDTO.getName());
//            }
//        }
//        return ResponseEntity.ok(dto);
//    }


    /**
     * 预算项目查询 查询所有公司信息
     *
     * @param pageable 分页对象
     * @return ResponseEntity
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/query/budget/all/company", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Company>> findAllCompany(Pageable pageable) throws URISyntaxException {
        Page page = new Page(pageable.getPageNumber() + 1, pageable.getPageSize());
        Page<Company> result = companyGroupAssignService.findAllCompany(page);
        List<Company> list = result.getRecords();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/companyId/group/assign/query/budget/all/companyId");
        return new ResponseEntity<>(list, headers, HttpStatus.OK);
    }



    /**
     * 预算项目查询 条件查询公司
     *
     * @param companyCode
     * @param companyName
     * @return ResponseEntity
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/query/budget/company/condition", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Company>> findCompanyByCodeOrName(@RequestParam(required = false) String companyCode, @RequestParam(required = false) String companyName) throws URISyntaxException {
        return ResponseEntity.ok(companyGroupAssignService.findCompanyByCodeOrName(companyCode, companyName));
    }


    /**
     * 预算项目查询 查询某区间的公司
     *
     * @param companyFrom
     * @param companyTo
     * @return ResponseEntity
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/query/budget/company/interval", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Company>> findCompaniesByInterval(@RequestParam String companyFrom, @RequestParam String companyTo) throws URISyntaxException {
        return ResponseEntity.ok(companyGroupAssignService.findCompaniesByInterval(companyFrom, companyTo));
    }


    /**
     * 预算项目查询 查询某租户ID下的公司
     *
     * @param tenantId
     * @return ResponseEntity
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/query/budget/company/tenant", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyDTO>> findCompanyByTenantId(@RequestParam Long tenantId) throws URISyntaxException {
        return ResponseEntity.ok(companyGroupAssignService.findCompanyByTenantId(tenantId));
    }


}

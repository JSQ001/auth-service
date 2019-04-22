package com.hand.hcf.app.mdata.accounts.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.accounts.domain.Accounts;
import com.hand.hcf.app.mdata.accounts.domain.AccountsHierarchy;
import com.hand.hcf.app.mdata.accounts.dto.AccountsHierarchyDTO;
import com.hand.hcf.app.mdata.accounts.service.AccountsHierarchyService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
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

@RestController
@RequestMapping("/api/accounts/hierarchy")
public class AccountsHierarchyResource {
    @Autowired
    private AccountsHierarchyService accountsHierarchyService;

    /**
     *
     * @api {post} /api/accounts/hierarchy/batch/insert  批量-新增科目层级
     * @apiGroup  AccountsHierarchy
     * @apiSuccess {Long} id   科目层级ID
     * @apiSuccess {Long} parentAccountId  父科目ID
     * @apiSuccess {Long} subAccountId  子科目ID
     * @apiSuccess {Long} tenantId  租户ID
     * @apiParamExample {json} Request-Param:
    [
    {
    "id": "943322687843979265",
    "createdDate": "2017-12-20T03:30:29Z",
    "lastUpdatedDate": "2017-12-20T03:30:29Z",
    "createdBy": "363d8ebf-28f8-48d9-aae7-c0e37a46e682",
    "lastUpdatedBy": "363d8ebf-28f8-48d9-aae7-c0e37a46e682",
    "parentAccountId": "10001",
    "subAccountId": "20005",
    "tenantId": 933327677580263425,
    "enabled": null,
    "deleted": null
    }
    ]
     */
    @RequestMapping(value = "/batch/insert",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AccountsHierarchy>> addAccountsHierarchyBatch(@RequestBody List<AccountsHierarchy> list) throws URISyntaxException {
        for (AccountsHierarchy accountsHierarchy : list){
            accountsHierarchy.setTenantId(OrgInformationUtil.getCurrentTenantId());
        }
        return ResponseEntity.ok(accountsHierarchyService.insertAccountsHierarchyBatch(list));
    }

    //根据id逻辑删除
    /**
     * @api {delete} /api/accounts/hierarchy/{id} 根据id逻辑删除科目层级
     * @apiGroup AccountsHierarchy
     * @apiParam {Long} id  待删除的id
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE,produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> deleteAccountsHierarchy(@PathVariable Long id) {
        return ResponseEntity.ok(accountsHierarchyService.deleteAccountsHierarchy(id));
    }

    /**
     * @api {delete} /api/accounts/hierarchy/batch/delete  批量-根据id逻辑删除科目层级
     * @apiGroup AccountsHierarchy
     * @apiParam {List} list  待删除的id集合
     */
    @RequestMapping(value = "/batch/delete",method = RequestMethod.DELETE,produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> deleteAccountsHierarchyBatch(@RequestBody List<Long> list) {
        return ResponseEntity.ok(accountsHierarchyService.deleteAccountsHierarchyBatch(list));
    }

    //分页查询 某汇总科目下的科目条件查询 （根据科目代码和科目名称）
    /**
     * @api {GET} /api/accounts/hierarchy/parent/query 科目层级条件查询
     * @apiGroup AccountsHierarchy
     * @apiParam  {Long} [parentAccountId] 父科目ID
     * @apiParam  {String} [info] 科目代码或科目名称
     * @apiSuccess {Long} id   科目层级ID
     * @apiSuccess {Long} parentAccountId   父科目ID
     * @apiSuccess {Long} subAccountId   子科目ID
     * @apiSuccess {String} accountCode   科目代码
     * @apiSuccess {String} accountName   科目名称
     * @apiSuccess {String} accountType   科目类型
     * @apiSuccess {String} accountTypeName   科目类型名称
     * @apiSuccessExample {json} Success-Result
    [
    {
    "id": "943301442402287618",
    "parentAccountId": "943296479928287233",
    "subAccountId": "943302768888676354",
    "accountCode": "code_test_001",
    "accountName": "en_name",
    "accountType": "BUD"
    },
    {
    "id": "943301547066949633",
    "parentAccountId": "943296479928287233",
    "subAccountId": "943302805433647106",
    "accountCode": "code_test_002",
    "accountName": "en_name",
    "accountType": "BUD"
    }
    ]
     */
    @RequestMapping(value = "/parent/query",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Accounts>> findParentAccountsHierarchyDTO(@RequestParam Long parentAccountId,
                                                                         @RequestParam(required = false) String info,
                                                                         Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<Accounts> result = accountsHierarchyService.findParentAccountsHierarchyDTO(parentAccountId,info,page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/accounts/hierarchy/parent/query");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    //分页查询 科目条件查询 （根据科目代码和科目名称）
    /**
     * @api {GET} /api/accounts/hierarchy/child/query 科目层级条件查询
     * @apiGroup AccountsHierarchy
     * @apiParam  {Long} [accountSetId] 科目表ID
     * @apiParam  {Long} [parentAccountId] 父科目ID
     * @apiParam  {String} [accountCode] 科目代码
     * @apiParam  {String} [accountName] 科目名称
     * @apiParam  {String} [codeFrom] 科目代码从
     * @apiParam  {String} [codeTo] 科目代码至
     * @apiSuccess {Long} id   科目层级ID
     * @apiSuccess {Long} parentAccountId   父科目ID
     * @apiSuccess {Long} subAccountId   子科目ID
     * @apiSuccess {String} accountCode   科目代码
     * @apiSuccess {String} accountName   科目名称
     * @apiSuccess {String} accountType   科目类型
     * @apiSuccess {String} accountTypeName   科目类型名称
     * @apiSuccessExample {json} Success-Result
    [
    {
    "id": null,
    "parentAccountId": null,
    "subAccountId": "943302768888676354",
    "accountCode": "code_test_001",
    "accountName": "en_name",
    "accountType": "BUD"
    },
    {
    "id": null,
    "parentAccountId": null,
    "subAccountId": "943302805433647106",
    "accountCode": "code_test_002",
    "accountName": "en_name",
    "accountType": "BUD"
    }
    ]
     */
    @RequestMapping(value = "/child/query",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AccountsHierarchyDTO>> findChildAccountsHierarchyDTO(@RequestParam Long accountSetId,
                                                                                    @RequestParam Long parentAccountId,
                                                                                    @RequestParam(required = false) String accountCode,
                                                                                    @RequestParam(required = false) String accountName,
                                                                                    @RequestParam(required = false) String codeFrom,
                                                                                    @RequestParam(required = false) String codeTo,
                                                                                    Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<AccountsHierarchyDTO> result = accountsHierarchyService.findChildAccountsHierarchyDTO(accountSetId,parentAccountId,accountCode,accountName,codeFrom,codeTo,page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/accounts/hierarchy/child/query");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }
}

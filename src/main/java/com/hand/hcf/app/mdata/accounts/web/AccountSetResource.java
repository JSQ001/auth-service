package com.hand.hcf.app.mdata.accounts.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.accounts.domain.AccountSet;
import com.hand.hcf.app.mdata.accounts.service.AccountSetService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.core.util.PageUtil;
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
@RequestMapping("/api/account/set")
public class AccountSetResource {
    @Autowired
    private AccountSetService accountSetService;

    /**
     *
     * @api {post} /api/accounts/set 新增科目表
     * @apiGroup AccountSet
     * @apiSuccess {Long} id   科目表ID
     * @apiSuccess {String} accountSetCode   科目表代码
     * @apiSuccess {String} accountSetDesc   科目表描述
     * @apiSuccess {Long} tenantId           租户ID
     * @apiParamExample {json} Request-Param:
     {
        "i18n": {
        "accountSetDesc": [
        {
        "language": "zh_cn",
        "value": "中文_name"
        },
        {
        "language": "en",
        "value": "en_name"
        }
        ]
        },
        "accountSetCode":"Test_Code_TEST",
        "accountSetDesc":"Test_Desc_TEST"
     }
     * @apiSuccessExample {json} Success-Result
    {
        "i18n": {
        "accountSetDesc": [
        {
        "language": "zh_cn",
        "value": "中文_name"
        },
        {
        "language": "en",
        "value": "en_name"
        }
        ]
        },
        "id": "925909980622745602",
        "createdDate": "2017-11-02T02:18:36Z",
        "lastUpdatedDate": "2017-11-02T02:18:36Z",
        "createdBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
        "lastUpdatedBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
        "accountSetCode": "Test_Code_TEST",
        "accountSetDesc": "Test_Desc_TEST",
        "tenantId": 10001,
        "enabled": null,
        "deleted": null
     }
     */
    @RequestMapping(value = "",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AccountSet> addAccountSet(@RequestBody AccountSet accountSet) throws URISyntaxException{
        accountSet.setTenantId(OrgInformationUtil.getCurrentTenantId());
        return ResponseEntity.ok(accountSetService.addAccountSet(accountSet));
    }

    /**
     *
     * @api {put} /api/accounts/set 更新科目表
     * @apiGroup AccountSet
     * @apiSuccess {Long} id   科目表ID
     * @apiSuccess {String} accountSetCode   科目表代码
     * @apiSuccess {String} accountSetDesc   科目表描述
     * @apiSuccess {Long} tenantId           租户ID
     * @apiParamExample {json} Request-Param:
    {
        "id": 925909980622745602,
        "accountSetCode": "Test_Code_RESULT",
        "accountSetDesc": "Test_Desc_RESULT"
    }
     * @apiSuccessExample {json} Success-Result
    {
        "i18n": null,
        "id": "925909980622745602",
        "createdDate": "2017-11-02T02:18:36Z",
        "lastUpdatedDate": "2017-11-02T02:18:36Z",
        "createdBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
        "lastUpdatedBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
        "accountSetCode": "Test_Code_RESULT",
        "accountSetDesc": "Test_Desc_RESULT",
        "tenantId": 10001,
        "enabled": true,
        "deleted": false
    }
     */
    @RequestMapping(value = "",method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AccountSet> updateAccountSet(@RequestBody AccountSet accountSet) throws URISyntaxException{
        return ResponseEntity.ok(accountSetService.updateAccountSet(accountSet));
    }

    //根据id逻辑删除
    /**
     * @api {delete} /api/accounts/set/{id} 根据id逻辑删除科目表
     * @apiGroup AccountSet
     * @apiParam {Long} id  待删除的id
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE,produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> deleteAccountSet(@PathVariable Long id) {
        return ResponseEntity.ok(accountSetService.deleteAccountSet(id));
    }

    //科目表查询 通过科目表ID
    /**
     * @api {GET} /api/accounts/set/{id} 根据科目表ID查询科目表
     * @apiGroup AccountSet
     * @apiParam  {Long} [id] 科目表ID
     * @apiSuccess {Object} AccountSet  科目表实体
     * @apiSuccess {Long} id   科目表ID
     * @apiSuccess {String} accountSetCode   科目表代码
     * @apiSuccess {String} accountSetDesc   科目表描述
     * @apiSuccess {Long} tenantId           租户ID
     * @apiSuccess {Boolean} enabled    启用标志
     * @apiSuccess {Boolean} deleted    删除标志
     * @apiSuccessExample {json} Success-Result
    {
        "i18n": {
        "accountSetDesc": [
        {
        "language": "en",
        "value": "en_name"
        },
        {
        "language": "zh_cn",
        "value": "中文_name"
        }
        ]
        },
        "id": "920248221706485762",
        "createdDate": "2017-10-17T11:20:48Z",
        "lastUpdatedDate": "2017-10-17T11:20:48Z",
        "createdBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
        "lastUpdatedBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
        "accountSetCode": "Test_Code_0019999",
        "accountSetDesc": "Test_Desc_001",
        "tenantId": 10001,
        "enabled": true,
        "deleted": false
    }
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AccountSet> findAccountSetById(@PathVariable Long id) throws URISyntaxException {
        return ResponseEntity.ok(accountSetService.findAccountSetById(id));
    }

    //分页查询 科目表条件查询 （根据科目表代码和科目表描述）
    /**
     * @api {GET} /api/accounts/set/query 科目表条件查询
     * @apiGroup AccountSet
     * @apiParam  {String} [accountSetCode] 科目表代码
     * @apiParam  {String} [accountSetDesc] 科目表描述
     * @apiSuccess {Object[]} AccountSet  科目表实体集合
     * @apiSuccess {Long} id   科目表ID
     * @apiSuccess {String} accountSetCode   科目表代码
     * @apiSuccess {String} accountSetDesc   科目表描述
     * @apiSuccess {Long} tenantId           租户ID
     * @apiSuccess {Boolean} enabled    启用标志
     * @apiSuccess {Boolean} deleted    删除标志
     * @apiSuccessExample {json} Success-Result
     [
        {
            "i18n": {
            "accountSetDesc": [
            {
            "language": "en",
            "value": "en_name"
            },
            {
            "language": "zh_cn",
            "value": "中文_name"
            }
            ]
            },
            "id": "912935779079139330",
            "createdDate": "2017-09-27T07:03:46Z",
            "lastUpdatedDate": "2017-10-13T02:26:11Z",
            "createdBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
            "lastUpdatedBy": null,
            "accountSetCode": "Test_Code_002",
            "accountSetDesc": "Test_Desc_002",
            "tenantId": 10001,
            "enabled": true,
            "deleted": false
        }
     ]
     */
    @RequestMapping(value = "/query",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AccountSet>> findAccountSetByCodeOrDesc(@RequestParam(required = false) String accountSetCode,
                                                                       @RequestParam(required = false) String accountSetDesc,
                                                                       Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<AccountSet> result = accountSetService.findAccountSetByCodeOrDesc(accountSetCode,accountSetDesc,page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/accounts/set/query");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }
}

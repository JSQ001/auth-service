package com.hand.hcf.app.mdata.accounts.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.accounts.domain.Accounts;
import com.hand.hcf.app.mdata.accounts.dto.AccountsDTO;
import com.hand.hcf.app.mdata.accounts.service.AccountsService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.core.util.PageUtil;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountsResource {
    @Autowired
    private AccountsService accountsService;
    /**
     *
     * @api {post} /api/accounts 新增科目表明细
     * @apiGroup Accounts
     * @apiSuccess {Long} id   科目表明细ID
     * @apiSuccess {Long} accountSetId   科目表ID
     * @apiSuccess {String} accountCode   科目代码
     * @apiSuccess {String} accountName   科目名称
     * @apiSuccess {String} accountDesc   科目描述
     * @apiSuccess {String} accountType   科目类型
     * @apiSuccess {String} balanceDirection   记账方向
     * @apiSuccess {String} reportType   报表类型
     * @apiSuccess {Boolean} summaryFlag  汇总标志
     * @apiSuccess {Long} tenantId       租户ID
     * @apiParamExample {json} Request-Param:
    {
    "i18n": {
    "accountName": [
    {
    "language": "zh_cn",
    "value": "中文_name"
    },
    {
    "language": "en",
    "value": "en_name"
    }
    ],
    "accountDesc": [
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
    "id": "943302874757103618",
    "createdDate": "2017-12-20T02:11:45Z",
    "lastUpdatedDate": "2017-12-20T02:11:45Z",
    "createdBy": "363d8ebf-28f8-48d9-aae7-c0e37a46e682",
    "lastUpdatedBy": "363d8ebf-28f8-48d9-aae7-c0e37a46e682",
    "accountSetId": "10001",
    "accountCode": "code_test_004",
    "accountName": "name_test_004",
    "accountDesc": "BUD",
    "accountType": "BUD",
    "balanceDirection": "cr",
    "reportType": "BUD",
    "summaryFlag": true,
    "tenantId": 933327677580263425,
    "enabled": null,
    "deleted": null
    }
     */
    @RequestMapping(value = "",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Accounts> addAccounts(@RequestBody Accounts accounts) throws URISyntaxException {
        accounts.setTenantId(OrgInformationUtil.getCurrentTenantId());
        return ResponseEntity.ok(accountsService.insertAccounts(accounts));
    }


    /**
     *
     * @api {put} /api/accounts 更新科目表明细
     * @apiGroup Accounts
     * @apiSuccess {Long} id   科目表明细ID
     * @apiSuccess {Long} accountSetId   科目表ID
     * @apiSuccess {String} accountCode   科目代码
     * @apiSuccess {String} accountName   科目名称
     * @apiSuccess {String} accountDesc   科目描述
     * @apiSuccess {String} accountType   科目类型
     * @apiSuccess {String} balanceDirection   记账方向
     * @apiSuccess {String} reportType   报表类型
     * @apiSuccess {Boolean} summaryFlag  汇总标志
     * @apiSuccess {Long} tenantId       租户ID
     * @apiParamExample {json} Request-Param:
    {
    "i18n": {
    "accountName": [
    {
    "language": "zh_cn",
    "value": "中文_name"
    },
    {
    "language": "en",
    "value": "en_name"
    }
    ],
    "accountDesc": [
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
    "id": "943295368840380418",
    "createdDate": "2017-12-20T01:41:56Z",
    "lastUpdatedDate": "2017-12-20T01:41:56Z",
    "createdBy": "363d8ebf-28f8-48d9-aae7-c0e37a46e682",
    "lastUpdatedBy": "363d8ebf-28f8-48d9-aae7-c0e37a46e682",
    "accountSetId": "10001",
    "accountCode": "code_test_update",
    "accountName": "name_test_update",
    "accountDesc": "BUD",
    "accountType": "BUD",
    "balanceDirection": "cr",
    "reportType": "BUD",
    "summaryFlag": true,
    "tenantId": 933327677580263425,
    "enabled": null,
    "deleted": null
    }
     */
    @RequestMapping(value = "",method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Accounts> updateAccounts(@RequestBody Accounts accounts) throws URISyntaxException{
        return ResponseEntity.ok(accountsService.updateAccounts(accounts));
    }

    //分页查询 科目表明细条件查询 （根据科目代码和科目名称）
    /**
     * @api {GET} /api/accounts/query 科目表明细条件查询
     * @apiGroup Accounts
     * @apiParam  {Long} [accountSetId]  科目表ID
     * @apiParam  {String} [accountType]  科目类型
     * @apiParam  {String} [info]  科目代码或科目名称
     * @apiSuccess {String} accountCode   科目代码
     * @apiSuccess {String} accountName   科目名称
     * @apiSuccess {String} accountDesc   科目描述
     * @apiSuccess {String} accountType   科目类型
     * @apiSuccess {String} accountTypeName   科目类型名称
     * @apiSuccess {String} balanceDirection   记账方向
     * @apiSuccess {String} balanceDirectionName   记账方向名称
     * @apiSuccess {String} reportType   报表类型
     * @apiSuccess {String} reportTypeName   报表类型名称
     * @apiSuccess {Boolean} summaryFlag  汇总标志
     * @apiSuccess {Boolean} enabled    启用标志
     * @apiSuccessExample {json} Success-Result
    [
    {
    "i18n": {
    "accountName":
    [
    {
    "language": "en",
    "value": "en_name"
    },
    {
    "language": "zh_cn",
    "value": "中文_name"
    }
    ],
    "accountDesc":
    [
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
    "id": "943296479928287233",
    "createdDate": "2017-12-20T01:46:21Z",
    "lastUpdatedDate": "2017-12-20T01:46:21Z",
    "createdBy": "363d8ebf-28f8-48d9-aae7-c0e37a46e682",
    "lastUpdatedBy": "363d8ebf-28f8-48d9-aae7-c0e37a46e682",
    "accountSetId": "10001",
    "accountCode": "code_test",
    "accountName": "中文_name",
    "accountDesc": "中文_name",
    "accountType": "BUD",
    "balanceDirection": "cr",
    "reportType": "BUD",
    "summaryFlag": true,
    "tenantId": 933327677580263425,
    "enabled": true,
    "deleted": false
    }
    ]
     */
    @RequestMapping(value = "/query",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AccountsDTO>> findAccounts(@RequestParam("accountSetId") Long accountSetId,
                                                          @RequestParam(value = "accountType",required = false) String accountType,
                                                          @RequestParam(value ="info", required = false) String info,
                                                          Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<AccountsDTO> result = accountsService.findAccounts(accountSetId,accountType,info,page);
        int count = accountsService.findAccountsCount(accountSetId,accountType,info);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("X-Total-Count-Enable",""+count);//启用条数
        headers.add("Link","/api/accounts/query");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    //根据账套ID查询所有科目
    /**
     * @api /api/accounts/query/accounts/setOfBooksId
     * @apiGroup Accounts
     * @apiParam {Long} setOfBooksId  账套ID
     */
    @RequestMapping(value = "/query/accounts/setOfBooksId",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Accounts>> findAccountsBySetOfBooksId(@RequestParam Long setOfBooksId,
                                                                     @RequestParam(required = false) String accountCode,
                                                                     @RequestParam(required = false) String accountName,
                                                                     Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        Page<Accounts> result = accountsService.findAccountsBySetOfBooksId(setOfBooksId,accountCode,accountName,null,page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/accounts/query/accounts/setOfBooksId");
        return new ResponseEntity<>(result.getRecords() == null ? new ArrayList<>() : result.getRecords(), headers, HttpStatus.OK);
    }
}

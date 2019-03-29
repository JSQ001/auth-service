package com.hand.hcf.app.mdata.bank.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.bank.dto.BanTranPoolDTO;
import com.hand.hcf.app.mdata.bank.service.BankTransactionService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/24 10:04
 */
@RestController
@RequestMapping("/api/bankcard")
public class BankTransactionController {

    @Autowired
    private BankTransactionService bankTransactionService;
    /**
     * @api {PUT} /api/bankcard/transaction/add/remark/{transactionID}?remark=  增加商务卡备注
     * @apiGroup bankCard
     * @apiParam {Long} transactionID 交易ID
     * @apiParam {String} [remark] 备注 默认为空
     * @apiSuccessExample {json} Success-Response:
     * {
     * "success": true,
     * "code": "0000",
     * "rows": 1
     * }
     * @apiSuccess {int} rows 成功更新条数
     * @apiErrorExample
     */
    @RequestMapping(value = "/transaction/add/remark/{transactionID}", method = RequestMethod.PUT)
    public ResponseEntity addTransactionRemark(@PathVariable(name = "transactionID", required = true) Long transactionID,
                                               @RequestParam(name = "remark", required = false, defaultValue = "") String remark) {
        return ResponseEntity.ok(bankTransactionService.updateTransactionRemark(transactionID, remark));
    }

    /**
     * @api {GET} /api/bankcard/transactions/{bankCard}/{used}?ownerOID= & page=& size= X用户X行商务卡消费列表展示
     * @apiGroup bankCard
     * @apiParam {String} bankCard bank code
     * @apiParam {UUID} ownerOID 费用所属人OID (默认当前登陆人)
     * @apiParam {Boolean} used 是否使用，ture-已使用 false-未使用
     * @apiParam {int} [page] 当前页数 默认0
     * @apiParam {int} [size] 每页条数 默认10
     * @apiParam {Long} [currMaxID] 每页最大条数 默认0
     * @apiSuccessExample {json} Success-Response:
     * {
     * "success": true,
     * "code": "0000",
     * "rows": [
     * {
     * "id": null,
     * "bilMon": "201710",
     * "bilDate": "2017-10-25",
     * "crdNum": "5525349301460396",
     * "trsDate": "",
     * "trxTim": "101613",
     * "oriCurAmt": 26,
     * "oriCurCod": "CNY",
     * "posDate": "2017-10-20",
     * "posCurAmt": 26,
     * "posCurCod": "CNY",
     * "acpName": "支付宝-上海福满家便利有限公司",
     * "trsCod": "00",
     * "overTime": false,
     * "remark":"办公用品购买"
     * "bankName": "招行"
     * }
     * ],
     * "total": 1
     * }
     * @apiSuccess {Long} id 消费数据主键
     * @apiSuccess {String} bilMonth 账单月
     * @apiSuccess {String} bilDate 账单日
     * @apiSuccess {String} crdNum 卡号-只展示后四位
     * @apiSuccess {String} trsDate 交易日
     * @apiSuccess {String} trxTim 交易时间
     * @apiSuccess {String} oriCurAmt 交易金额
     * @apiSuccess {String} oriCurCod 交易币种
     * @apiSuccess {String} posDate 入账日
     * @apiSuccess {String} posCurAmt 入账金额
     * @apiSuccess {Stirng} posCurCod 入账货币
     * @apiSuccess {String} acpName 商户名称
     * @apiSuccess {String} trsCod 交易类型 00-一般消费,01-预借现金,12-预借现金退货,20-一般消费退货,60-还款及费用
     * @apiSuccess {Boolean} overTime 是否逾期? true-逾期 ，false-未逾期
     * @apiSuccess {String} approvedDeadLineDate 审核通过时间期限
     * @apiSuccess {String} remark 备注
     * @apiSuccess {String} bankName 银行名称
     */
    @RequestMapping(value = "/transactions/{bankCard}/{used}", method = RequestMethod.GET)
    public ResponseEntity<Page<BanTranPoolDTO>> getCurrUserTransactions(@PathVariable(name = "bankCard",required = true) String bankCard,
                                                                        @RequestParam(name = "ownerOID",required = false)UUID ownerOID,
                                                                        @PathVariable(name = "used" ,required = true) Boolean used,
                                                                        @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                        @RequestParam(name = "size", defaultValue = "10",required = false) Integer size,
                                                                        @RequestParam(name = "currMaxID",defaultValue = "0")Long currMaxID,
                                                                        @RequestParam(name = "trsDate",required = false,defaultValue = "") String trsDate,
                                                                        @RequestParam(name = "trxTime",required = false,defaultValue = "") String trxTime) {
        if (null == ownerOID){
            ownerOID = OrgInformationUtil.getCurrentUserOid();
        }
        return ResponseEntity.ok(bankTransactionService.getUserTransactions(bankCard, ownerOID,currMaxID,trsDate,trxTime, used, new Page(page + 1, size)));
    }
    ///api/bankcard/user/status/CMBC此接口在artemis中没找到
}

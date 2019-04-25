package com.hand.hcf.app.payment.externalApi;

import org.springframework.stereotype.Service;

/**
 * @description: 调用合同模块三方接口
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2018/12/25
 */
@Service
public class PaymentContractService {
    //bo.liu 合同
    /*@Autowired
    private ContractClient contractClient;

    *//**
     *通过合同单据id列表查询合同单据头列表
     * @param ids
     * @return
     *//*
    public List<ContractHeaderCO> listContractHeadersByIds(List<Long> ids){
        return contractClient.listContractHeadersByIds(ids);
    }

    *//**
     * 按照合同行ID查询合同资金计划行
     * @param headerId 合同头ID
     * @param lineId   合同资金计划行ID
     * @return
     *//*
    public ContractHeaderLineCO getContractLine(Long headerId, Long lineId){
        return contractClient.getContractLine(headerId, lineId);
    }*/
}

package com.hand.hcf.app.prepayment.externalApi;

import com.hand.hcf.app.common.co.ContractDocumentRelationCO;
import com.hand.hcf.app.common.co.ContractHeaderLineCO;
import com.hand.hcf.app.prepayment.utils.ContractOperationType;
import com.hand.hcf.app.prepayment.utils.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 刘亮 on 2018/2/2.
 */
@Service
public class ContractModuleInterface {


    private static final Logger log = LoggerFactory.getLogger(ContractModuleInterface.class);

    //jiu.zhao 合同
    //private static ContractClient contractService;

    /*public ContractModuleInterface(ContractClient contractService) {
        this.contractService = contractService;
    }*/




    public static Boolean contractDocumentRelationBatch(List<ContractDocumentRelationCO> list, String type, Boolean isWorkflow) {

//        String createMappingURL = INSERT_CONTRACT_RELATION;
//
//        String rollBackMappingURL = GET_CONTRACT_ROLL_BACK;
//
//        String logicalDeleteMappingURL = GET_CONTRACT_LOGICAL_DELETE;

        /*Boolean aBoolean = null;
        ExceptionDetail exceptionDetail = new ExceptionDetail();
        switch (type) {
            case ContractOperationType.CREATE:
//                    exceptionDetail = contractRestService.doRest(contractBaseURL + createMappingURL, ExceptionDetail.class, list, HttpMethod.POST, null, false);
                aBoolean = contractService.saveOrUpdateContractDocumentRelationBatch(list);
                break;
            case ContractOperationType.ROLLBACK:
//                    exceptionDetail = contractRestService.doRest(contractBaseURL + rollBackMappingURL, ExceptionDetail.class, list, HttpMethod.DELETE, null, false);
                aBoolean = contractService.deleteRollBackRelationBatch(list);

                break;
            case ContractOperationType.LOGICAL_DELETE:
//                    exceptionDetail = contractRestService.doRest(contractBaseURL + logicalDeleteMappingURL, ExceptionDetail.class, list, HttpMethod.DELETE, null, false);
                aBoolean = contractService.deleteContractDocumentRelationBatch(list);
                break;
        }
        if (!isWorkflow) {//如果是本模块调用，则直接抛出具体异常
            if(!aBoolean){
                throw new BizException("error rest for contract module", "调用合同模块异常！");
            }
        }else{
            if(!aBoolean){
                exceptionDetail.setErrorCode("error rest for contract module" );
                exceptionDetail.setMessage("调用合同模块异常！");
            }else{
                exceptionDetail.setErrorCode("0000");
            }
        }*/

        Boolean aBoolean = null;

        //jiu.zhao 合同
        /*switch (type) {
            case ContractOperationType.CREATE:
                aBoolean = contractService.saveOrUpdateContractDocumentRelationBatch(list);
                break;
            case ContractOperationType.ROLLBACK:
                aBoolean = contractService.deleteRollBackRelationBatch(list);
                break;
            case ContractOperationType.LOGICAL_DELETE:
                aBoolean = contractService.deleteContractDocumentRelationBatch(list);
                break;
        }*/
        if (!isWorkflow) {//如果是本模块调用，则直接抛出具体异常
            if(!aBoolean){
                throw new BizException(RespCode.PREPAY_ERROR_REST_FOR_CONTRACT_MODULE);
            }
        }else{
            if(!aBoolean){
                throw new BizException(RespCode.PREPAY_ERROR_REST_FOR_CONTRACT_MODULE);
            }
        }
        return aBoolean;
    }


    /**
     * 根据合同头id或者行id获取合同详细信息
     *
     * @param contractHeaderId 合同头id(必传)
     * @param contractLineId   合同行id
     * @return DTO
     */
    public static ContractHeaderLineCO getContractInfoById(Long contractHeaderId, Long contractLineId) {

        //return contractService.getContractLine(contractHeaderId, contractLineId);
        //jiu.zhao 合同
        return null;
//        String mappingURL = GET_CONTRACT_HEADER_LINE_INFO_BY_ID + "?headerId=" + contractHeaderId;
//        if (contractLineId != null) {
//            mappingURL = mappingURL + "&lineId=" + contractLineId;
//        }
//        ContractHeaderLineDTO headerLineDTO = null;
//        try {
//            headerLineDTO = contractRestService.doRest(contractBaseURL + mappingURL, ContractHeaderLineDTO.class, null, HttpMethod.GET, null, false);
//        } catch (Exception e) {
//            throw new BizException(RespCode.GET_CONTRACT_HEAD_AND_LINE_ERROR);
//        }
//        return headerLineDTO;
    }


}

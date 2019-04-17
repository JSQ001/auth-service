package com.hand.hcf.app.base.system.service;

import com.hand.hcf.app.base.system.domain.ErrorMessage;
import com.hand.hcf.app.base.system.persistence.ErrorMessageMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import org.springframework.stereotype.Service;

/**
 * @description: 报错信息service
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2018/10/18
 */
@Service
public class ErrorMessageService extends BaseService<ErrorMessageMapper,ErrorMessage> {
    private ErrorMessageMapper errorMessageMapper;

    public ErrorMessageService(ErrorMessageMapper errorMessageMapper){
        this.errorMessageMapper = errorMessageMapper;
    }

    /**
     * 新增 报错信息
     * @param errorMessage
     * @return
     */
    public ErrorMessage createErrorMessage(ErrorMessage errorMessage){
        //数据校验
        if (null != errorMessage.getId()){
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        if (null == errorMessage.getModuleCode() || "".equals(errorMessage.getModuleCode())){
            throw new BizException(RespCode.MODULE_CODE_NULL);
        }
        if (null == errorMessage.getLanguage() || "".equals(errorMessage.getLanguage())){
            throw new BizException(RespCode.LANGUAGE_CODE_NULL);
        }
        if (null == errorMessage.getErrorCode() || "".equals(errorMessage.getErrorCode())){
            throw new BizException(RespCode.ERROR_CODE_NULL);
        }
        if (null == errorMessage.getErrorMessage() || "".equals(errorMessage.getErrorMessage())){
            throw new BizException(RespCode.ERROR_MESSAGE_NULL);
        }
        errorMessageMapper.insert(errorMessage);
        return errorMessage;
    }

    /**
     * 更新 报错信息
     * @param errorMessage
     * @return
     */
    public ErrorMessage updateErrorMessage(ErrorMessage errorMessage){
        if (null == errorMessage.getId()){
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        if (null == errorMessage.getModuleCode() || "".equals(errorMessage.getModuleCode())){
            throw new BizException(RespCode.MODULE_CODE_NULL);
        }
        if (null == errorMessage.getLanguage() || "".equals(errorMessage.getLanguage())){
            throw new BizException(RespCode.LANGUAGE_CODE_NULL);
        }
        if (null == errorMessage.getErrorCode() || "".equals(errorMessage.getErrorCode())){
            throw new BizException(RespCode.ERROR_CODE_NULL);
        }
        if (null == errorMessage.getErrorMessage() || "".equals(errorMessage.getErrorMessage())){
            throw new BizException(RespCode.ERROR_MESSAGE_NULL);
        }
        errorMessageMapper.updateById(errorMessage);
        return errorMessage;
    }
}

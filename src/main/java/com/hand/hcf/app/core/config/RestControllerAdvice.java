

package com.hand.hcf.app.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hcf.app.core.domain.enumeration.MessageTypeEnum;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.app.core.exception.core.UnauthenticatedException;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.core.service.MessageService;
import com.hand.hcf.app.core.util.FeignUtil;
import com.hand.hcf.app.core.util.RespCode;
import com.hand.hcf.app.core.web.dto.MessageDTO;
import com.hand.hcf.app.core.web.exception.ExceptionDetail;
import com.hand.hcf.app.core.web.exception.ExceptionErrorCode;
import com.hand.hcf.app.core.web.util.HttpRequestUtil;
import com.hand.hcf.app.core.web.util.RequestContext;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.naming.ServiceUnavailableException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Restful API Error Handling Class
 * 1) MethodArgumentNotValidException -> 400
 * 2) ValidationException -> 400
 * 3) ObjectNotFoundException -> 404
 * 4) UnauthenticatedException -> 401
 * 5) AccessDeniedException -> 401
 * 6) Throwable -> 500
 * 7) ServiceUnavailableException -> 503
 * <p>
 * TODO: Add usage instruction
 * <p>
 *
 */
@ControllerAdvice
@Slf4j
public class RestControllerAdvice {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MessageService messageService;

    protected void logRequest(ExceptionErrorCode errorCode, Throwable e) {
        if (log.isErrorEnabled()) {
            if (RequestContext.getReqMethod() == null) {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                RequestContext.clear();
                HttpRequestUtil.resolveContext(request);
            }
            String resStatus = errorCode.getHttpStatus().value() + " " + errorCode.getHttpStatus().getReasonPhrase();
            log.error("REQUEST FAIL [" + StringUtils.leftPad((System.currentTimeMillis() - RequestContext.getTimeStart()) + "", 5) + "]: " + RequestContext.getLogString() + ", res=" + resStatus, e);
        }
    }

    ResponseEntity<ExceptionDetail> getResponse(ExceptionErrorCode errorCode, Throwable e) {

        return getResponse(errorCode,e,null,null,null);
    }

    ResponseEntity<ExceptionDetail> getResponse(ExceptionErrorCode errorCode, Throwable e, List<ValidationError> errors) {
        return getResponse(errorCode,e,errors,null,null,null);
    }

    ResponseEntity<ExceptionDetail> getResponse(ExceptionErrorCode errorCode, Throwable e,String errorMessage,String bizErrorCode) {
        return getResponse(errorCode,e,null,errorMessage,bizErrorCode,null);
    }

    ResponseEntity<ExceptionDetail> getResponse(ExceptionErrorCode errorCode, Throwable e,String errorMessage,String bizErrorCode,String category) {
        return getResponse(errorCode,e,null,errorMessage,bizErrorCode,category);
    }

    ResponseEntity<ExceptionDetail> getResponse(ExceptionErrorCode errorCode, Throwable e, List<ValidationError> errors,String errorMessage) {
        return getResponse(errorCode,e,errors,errorMessage,null,null);
    }

    StringBuilder getValidError(List<ValidationError> errors ){
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < errors.size(); i++) {
            if (i != 0){
                message.append("\n");
            }
            String errorMsg = messageService.getMessageDetailByCode(errors.get(i).getMessage());
            if (StringUtils.isEmpty(errorMsg)){
                message.append(errors.get(i).getMessage());
            }else{
                errors.get(i).setMessage(errorMsg);
                message.append(errorMsg);
            }
        }
        return message;
    }

    ResponseEntity<ExceptionDetail> getResponse(ExceptionErrorCode errorCode, Throwable e, List<ValidationError> errors,String errorMessage,String bizErrorCode,String category) {
        log.error(errorCode.toString(), e);
        logRequest(errorCode, e);
        ExceptionDetail detail = new ExceptionDetail();
        detail.setPath(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getServletPath());
        detail.setError(errorCode.name());
        String message=null;
        if (StringUtils.isEmpty(errorMessage)){
            if (errors!=null) {
              message=  getValidError(errors).toString();
            }
            if (StringUtils.isEmpty(message)) {
                if (StringUtils.isEmpty(e.getMessage())) {
                    message = e.toString() + "\n" + e.getStackTrace()[0];
                } else {
                    message = e.getMessage();
                }
            }
        } else {
            message=errorMessage;
        }
        detail.setMessage(message);
        detail.setCategory(category);

        detail.setBizErrorCode(bizErrorCode==null ? RespCode.SYS_FAILED : bizErrorCode);
        if (errors != null) {
            detail.setValidationErrors(errors);
        }
        return new ResponseEntity<>(detail, errorCode.getHttpStatus());
    }

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ExceptionDetail> handleBizException(BizException e) {
        String errorMsg = null;
        MessageDTO messageDTOByCode = messageService.getMessageDTOByCode(e.getCode(), e.getArgs());
        if(messageDTOByCode != null){
            errorMsg = messageDTOByCode.getKeyDescription();
        }else{
            errorMsg = e.getMsg();
        }
        log.error("bizErrorCode: " + e.getCode());
        log.error("message: " + errorMsg);
        return getResponse(ExceptionErrorCode.VALIDATION_ERROR, e, errorMsg,e.getCode(),messageDTOByCode == null ? MessageTypeEnum.ERROR.getKey():messageDTOByCode.getCategory());

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        List<ValidationError> validationErrors = new ArrayList<>();
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        validationErrors.addAll(errors.stream().map(error -> new ValidationError((FieldError) error)).collect(Collectors.toList()));

        log.error(e.getMessage());
        return getResponse(ExceptionErrorCode.VALIDATION_ERROR, e, validationErrors);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionDetail> handleValidationException(ValidationException e) {

        return getResponse(ExceptionErrorCode.VALIDATION_ERROR, e, e.getValidationErrors());
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ExceptionDetail> handleObjectNotFound(ObjectNotFoundException e) {

        return getResponse(ExceptionErrorCode.OBJECT_NOT_FOUND, e);

    }

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ExceptionDetail> handUnauthenticated(UnauthenticatedException e) {

        return getResponse(ExceptionErrorCode.UNAUTHENTICATED, e);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionDetail> handAccessDeniedException(AccessDeniedException e) {

        return getResponse(ExceptionErrorCode.SECURITY_VIOLATION, e);
    }


    @ExceptionHandler(HystrixBadRequestException.class)
    public ResponseEntity<ExceptionDetail> handFeignException(HystrixBadRequestException e) throws IOException {
        log.error("message:" + e.getMessage(), e);
        ExceptionDetail detail = FeignUtil.getExceptionDetail(e);

        return new ResponseEntity<>(detail, ExceptionErrorCode.valueOf(detail.getError()).getHttpStatus());
    }


    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<ExceptionDetail> handleDataException(DataIntegrityViolationException e) {
        //String errMsg=e.getLocalizedMessage();
        String errMsgc=e.getCause().getLocalizedMessage();
        //String errMsgrc=e.getRootCause().getLocalizedMessage();
        return getResponse(ExceptionErrorCode.VALIDATION_ERROR, e,errMsgc,RespCode.SYS_FAILED);
    }


    @ExceptionHandler(value = {DataAccessException.class})
    public ResponseEntity<ExceptionDetail> handleDataException(DataAccessException e) {
        String errMsgc=e.getCause().getLocalizedMessage();
        return getResponse(ExceptionErrorCode.VALIDATION_ERROR, e,errMsgc,RespCode.SYS_FAILED);
    }

    @ExceptionHandler(value = {ServiceUnavailableException.class,
            ServiceUnavailableException.class})
    public ResponseEntity<ExceptionDetail> handleServiceUnavailableException(Throwable e) {

        return getResponse(ExceptionErrorCode.SERVICE_UNAVAILABLE, e);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionDetail> handleException(Throwable e) {

        return getResponse(ExceptionErrorCode.SYSTEM_EXCEPTION, e);
    }


}

package com.hand.hcf.app.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hcf.app.core.web.exception.ExceptionDetail;
import com.netflix.hystrix.exception.HystrixBadRequestException;

public class FeignUtil {

    private static final ObjectMapper objectMapper=new ObjectMapper();

    public static ExceptionDetail getExceptionDetail(HystrixBadRequestException e)  {
        try {
           return objectMapper.readValue(e.getMessage(), ExceptionDetail.class);
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }

    }

}

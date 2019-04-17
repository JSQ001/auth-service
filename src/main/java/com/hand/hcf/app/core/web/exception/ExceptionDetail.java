package com.hand.hcf.app.core.web.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hand.hcf.app.core.exception.core.ValidationError;
import lombok.Data;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"message", "error", "validationErrors", "category"})
@Data
public class ExceptionDetail {

    private String error;
    private String message;
    private String category;
    private String bizErrorCode;
    private List<ValidationError> validationErrors;
    private String path;
    private String timestamp= ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);


}

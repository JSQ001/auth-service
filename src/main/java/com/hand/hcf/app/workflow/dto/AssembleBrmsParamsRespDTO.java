package com.hand.hcf.app.workflow.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class AssembleBrmsParamsRespDTO implements Serializable{
    private List<FormValueDTO> formValueDTOS;
    private Map<String,Object> entityData;
    private UUID formOid;
}


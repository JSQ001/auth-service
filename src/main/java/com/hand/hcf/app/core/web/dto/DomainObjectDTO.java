package com.hand.hcf.app.core.web.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public class DomainObjectDTO implements Serializable{
    protected Long id;
    protected Boolean enabled;
    protected Boolean deleted;
    protected ZonedDateTime createdDate;
    protected Long createdBy;
    protected ZonedDateTime lastUpdatedDate;
    protected Long lastUpdatedBy;
    protected Integer versionNumber;
}


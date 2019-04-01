package com.hand.hcf.app.common.co;

import lombok.Data;

import java.util.List;

@Data
public class ApplicationCO {
    private ApplicationHeaderCO applicationHeader;
    private List<ApplicationLineCO> applicationLines;

}

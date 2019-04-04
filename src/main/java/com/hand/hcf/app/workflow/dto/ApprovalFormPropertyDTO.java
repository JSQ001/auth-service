package com.hand.hcf.app.workflow.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Created by Wkit on 2017/4/11.
 */
@Getter
@Setter
public class ApprovalFormPropertyDTO {
    private UUID formOid;
    private Integer enabled = 0;
    private Integer closeDay = 0;
    private Integer participantEnable = 0;
    private Integer changeEnabled = 0;
    private Integer restartEnabled = 0;
    private Integer restartCloseDay = 0;
    private String manageType = "1001";
    private String controlFields = "{\"discount\":{\"show\":false,\"required\":false,\"control\":false},\"ticketPrice\":{\"show\":false,\"required\":false,\"control\":false},\"seatClass\":{\"show\":false,\"required\":false,\"control\":false},\"takeOffBeginTime\":{\"show\":false,\"required\":false,\"control\":false},\"takeOffEndTime\":{\"show\":false,\"required\":false,\"control\":false},\"arrivalBeginTime\":{\"show\":false,\"required\":false,\"control\":false},\"arrivalEndTime\":{\"show\":false,\"required\":false,\"control\":false}}";
    private Integer uniformBookingEnable = 1;
    private Integer participantsScope = 0;
    private String travelTOApplication = "Y";
    private String expenseTOApplication = "Y";
    private Integer synchronizeEnabled = 1;
}

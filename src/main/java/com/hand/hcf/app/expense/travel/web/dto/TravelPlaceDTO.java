package com.hand.hcf.app.expense.travel.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelPlaceDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long placeId;

    private String placeName;
}

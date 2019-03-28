package com.hand.hcf.app.prepayment.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by 韩雪 on 2018/3/5.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JudgeUserDTO {
    private Long userId;

    private List<Long> idList;
}

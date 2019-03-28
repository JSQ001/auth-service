package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p></p>
 *
 * @Author: bin.xie
 * @Date: Created in 14:38 2018/7/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JudgeUserCO {
    private Long userId;

    private List<Long> idList;
}
package com.hand.hcf.app.workflow.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author : mawei
 * @description : TODO
 * @since : 2018/3/4
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckAuditNoticeDTO {
  private UUID entityOid;
  private Long id;
}

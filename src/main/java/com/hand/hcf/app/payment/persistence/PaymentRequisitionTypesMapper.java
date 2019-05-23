package com.hand.hcf.app.payment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.payment.domain.PaymentRequisitionTypes;
import com.hand.hcf.app.payment.web.dto.PaymentRequisitionTypesAndUserGroupDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @Author: bin.xie
 * @Description: 付款申请单Mapper接口
 * @Date: Created in 11:13 2018/1/22
 * @Modified by
 */
public interface PaymentRequisitionTypesMapper extends BaseMapper<PaymentRequisitionTypes> {
     List<PaymentRequisitionTypes> selectAcpReqTypesByCompanyId(@Param("setOfBooksId") Long setOfBooksId, @Param("companyId") Long companyId);

    List<PaymentRequisitionTypes> selectAcpReqTypesByCompanyIdEnable(@Param("setOfBooksId") Long setOfBooksId, @Param("enabled") Boolean enabled);

    List<PaymentRequisitionTypes> selectByUser(RowBounds page,
                                               @Param("setOfBooksId") Long setOfBooksId,
                                               @Param("companyId") Long companyId,
                                               @Param("acpReqTypeCode") String acpReqTypeCode,
                                               @Param("description") String description,
                                               @Param("departmentId") Long departmentId);

    List<PaymentRequisitionTypesAndUserGroupDTO> selectByUserGroup(@Param("setOfBooksId") Long setOfBooksId,
                                                                   @Param("companyId") Long companyId);


}

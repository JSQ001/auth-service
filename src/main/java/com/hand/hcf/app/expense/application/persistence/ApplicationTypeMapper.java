package com.hand.hcf.app.expense.application.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.expense.application.domain.ApplicationType;
import com.hand.hcf.app.expense.application.web.dto.ApplicationTypeAndUserGroupDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/8
 */
public interface ApplicationTypeMapper extends BaseMapper<ApplicationType> {

    List<ApplicationType> selectByUser(@Param("departmentId") Long departmentId,
                                       @Param("companyId") Long companyId,
                                       @Param("setOfBooksId") Long setOfBooksId);

    List<ApplicationTypeAndUserGroupDTO> selectByUserGroup(@Param("setOfBooksId")Long setOfBooksId,
                                                           @Param("companyId")Long companyId);

    /**
     * 报账单表单关联申请单类型LOV查询
     * @param setOfBooksId
     * @param range
     * @param typeName
     * @param ids
     * @param rowBounds
     * @return
     */
    List<ApplicationType> listTypesByReportCondition(@Param("setOfBooksId") Long setOfBooksId,
                                                     @Param("range")String range,
                                                     @Param("typeName")String typeName,
                                                     @Param("ids")List<Long> ids,
                                                     RowBounds rowBounds);

    List<ApplicationType> queryApplicationTypeByCond(
            @Param("setOfBooksId") Long setOfBooksId,
            @Param("range") String range,
            @Param("code") String code,
            @Param("name") String name,
            @Param("enabled") Boolean enabled,
            @Param("idList") List<Long> idList,
            Page page
    );

    /**
     * 根据账套id和启用字段查询已创建的申请单类型
     * @param setOfBooksId
     * @param enabled
     * @return
     */
    List<ApplicationType> queryCreatedType(@Param("setOfBooksId") Long setOfBooksId,
                                           @Param("enabled") Boolean enabled);
}

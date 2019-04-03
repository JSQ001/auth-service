package com.hand.hcf.app.expense.application.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.application.domain.ApplicationLineDist;
import com.hand.hcf.app.expense.application.web.dto.ApplicationHeaderAbbreviateDTO;
import com.hand.hcf.app.expense.application.web.dto.ApplicationLineAbbreviateDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/28
 */
public interface ApplicationLineDistMapper extends BaseMapper<ApplicationLineDist> {

    /**
     * 查申请单行表，用分摊行对象接，用于保存分摊行
     *
     * @param id
     */
    List<ApplicationLineDist> listLinesByHeaderId(@Param("id") Long id);

    /**
     * 报账单关联申请单 获取分摊行相关信息
     * @param dto
     * @return
     */
    List<ApplicationLineAbbreviateDTO> selectByApplicationHeaderId(ApplicationHeaderAbbreviateDTO dto);
}

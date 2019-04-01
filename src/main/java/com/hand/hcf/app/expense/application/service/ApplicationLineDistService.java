package com.hand.hcf.app.expense.application.service;

import com.hand.hcf.app.expense.application.domain.ApplicationLineDist;
import com.hand.hcf.app.expense.application.persistence.ApplicationLineDistMapper;
import com.hand.hcf.app.expense.application.web.dto.ApplicationHeaderAbbreviateDTO;
import com.hand.hcf.app.expense.application.web.dto.ApplicationLineAbbreviateDTO;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 *     分摊行
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/28
 */
@Service
public class ApplicationLineDistService extends BaseService<ApplicationLineDistMapper, ApplicationLineDist> {

    @Transactional(rollbackFor = Exception.class)
    public List<ApplicationLineDist> createDist(Long id) {
        List<ApplicationLineDist> distList = baseMapper.listLinesByHeaderId(id);
        if (CollectionUtils.isEmpty(distList)){
            throw new BizException(RespCode.EXPENSE_APPLICATION_LINE_IS_NULL);
        }
        this.insertBatch(distList);
        return distList;
    }

    /**
     * 报账单关联申请单 获取分摊行相关信息
     * @param dto
     * @return
     */
    public List<ApplicationLineAbbreviateDTO> selectByApplicationHeaderId(ApplicationHeaderAbbreviateDTO dto){
        return baseMapper.selectByApplicationHeaderId(dto);
    }
}

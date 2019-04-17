package com.hand.hcf.app.expense.application.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.application.domain.ApplicationHeader;
import com.hand.hcf.app.expense.application.domain.ApplicationLineDist;
import com.hand.hcf.app.expense.application.enums.ClosedTypeEnum;
import com.hand.hcf.app.expense.application.persistence.ApplicationLineDistMapper;
import com.hand.hcf.app.expense.application.web.dto.ApplicationHeaderAbbreviateDTO;
import com.hand.hcf.app.expense.application.web.dto.ApplicationLineAbbreviateDTO;
import com.hand.hcf.app.expense.common.utils.RespCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

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
        List<ApplicationLineDist> collect = distList.stream().peek(e -> e.setId(null)).collect(Collectors.toList());
        this.insertBatch(collect);
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

    @Transactional(rollbackFor = Exception.class)
    public void updateClosedByHeaders(List<ApplicationHeader> applicationHeaders) {
        if (!CollectionUtils.isEmpty(applicationHeaders)) {
            Wrapper<ApplicationLineDist> updateWrapper = this.getWrapper()
                    .in("header_id", applicationHeaders
                            .stream()
                            .map(ApplicationHeader::getId).collect(Collectors.toList()));
            String setString = "closed_flag = " + ClosedTypeEnum.CLOSED.getId();
            this.updateForSet(setString, updateWrapper);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateClosedByLineId(Long lineId) {
        Wrapper<ApplicationLineDist> updateWrapper = this.getWrapper()
                .eq("line_id", lineId);
        String setString = "closed_flag = " + ClosedTypeEnum.CLOSED.getId();
        this.updateForSet(setString, updateWrapper);
    }
}

package com.hand.hcf.app.ant.mdata.location.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.ant.mdata.location.domain.PayeeSettingLine;
import com.hand.hcf.app.ant.mdata.location.dto.PayeeSettingLineDTO;
import com.hand.hcf.app.ant.mdata.location.persistence.PayeeSettingLineMapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.PageUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zihao.yang
 * @create 2019-6-13 11:27:23
 * @remark
 */
@Service
public class PayeeSettingLineService extends BaseService<PayeeSettingLineMapper, PayeeSettingLine>{

    @Autowired
    private PayeeSettingLineMapper payeeSettingLineMapper;


    /**
     * 根据头id分页查询收款方配置行信息
     */
    public Page<PayeeSettingLineDTO> queryLineByHeaderId(Long payeeSettingHeaderId, Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);

        //  分页查询收款方
        List<PayeeSettingLineDTO> rsPayeeSettingLineDTOList = payeeSettingLineMapper.queryLineByHeaderId(payeeSettingHeaderId, mybatisPage);
        mybatisPage.setRecords(rsPayeeSettingLineDTOList);
        return mybatisPage;
    }

    /**
     * 更新收款方配置行表信息
     */
    @Transactional(rollbackFor = Exception.class)
    public PayeeSettingLine saveOrUpdateLine(PayeeSettingLineDTO payeeSettingLineDTO) {
        //DTO转domain
        PayeeSettingLine payeeSettingLine = new PayeeSettingLine();
        BeanUtils.copyProperties(payeeSettingLineDTO, payeeSettingLine);
        insertOrUpdate(payeeSettingLine);
        return payeeSettingLine;
    }

    /**
     * 根据收款方头表ID删除付款方行信息
     * @param headerId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLineByHeadId(Long headerId){
        List<PayeeSettingLine> payeeSettingLineList = selectList(new EntityWrapper<PayeeSettingLine>().eq("header_id", headerId));
        if(CollectionUtils.isNotEmpty(payeeSettingLineList)){
            List<Long> collect = payeeSettingLineList.stream().map(PayeeSettingLine::getId).collect(Collectors.toList());
            return deleteBatchIds(collect);
        }
        return true;
    }

    /**
     * 根据ID删除收款方行记录
     *
     * @param lineId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteLineById(Long lineId) {
        PayeeSettingLine payeeSettingLine = selectById(lineId);
        if (payeeSettingLine != null) {
            // 删除行信息
            deleteById(lineId);
        }
    }
}

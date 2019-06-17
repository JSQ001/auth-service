package com.hand.hcf.app.ant.mdata.location.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.mdata.location.domain.PayeeSettingHeader;
import com.hand.hcf.app.ant.mdata.location.dto.PayeeSettingHeaderDTO;
import com.hand.hcf.app.ant.mdata.location.dto.PayeeSettingLineDTO;
import com.hand.hcf.app.ant.mdata.location.persistence.PayeeSettingHeaderMapper;
import com.hand.hcf.app.ant.mdata.location.persistence.PayeeSettingLineMapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.PageUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author zihao.yang
 * @create 2019-6-13 11:27:23
 * @remark
 */
@Service
public class PayeeSettingHeaderService extends BaseService<PayeeSettingHeaderMapper, PayeeSettingHeader>{

    @Autowired
    private PayeeSettingHeaderMapper payeeSettingHeaderMapper;
    @Autowired
    private PayeeSettingLineService payeeSettingLineService;
    @Autowired
    private PayeeSettingLineMapper payeeSettingLineMapper;

    /**
     * 根据条件查询收款方配置头
     */
    public Page<PayeeSettingHeaderDTO> queryHeader(Long payeeSettingHeaderId,
                                      String payeeCountryCode, String payeeCityCode,
                                      String payerCountryCode, String payerCityCode,
                                      Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);

        //  分页查询收款方
        List<PayeeSettingHeaderDTO> rsPayeeSettingHeaderDTOList = payeeSettingHeaderMapper.queryHeader(payeeSettingHeaderId,
                payeeCountryCode, payeeCityCode,
                payerCountryCode, payerCityCode,
                mybatisPage);

        mybatisPage.setRecords(rsPayeeSettingHeaderDTOList);
        return mybatisPage;
    }

    /**
     * 更新收款方配置头表信息
     */
    @Transactional(rollbackFor = Exception.class)
    public PayeeSettingHeader saveOrUpdateHeader(PayeeSettingHeaderDTO payeeSettingHeaderDTO) {
        //DTO转domain
        PayeeSettingHeader payeeSettingHeader = new PayeeSettingHeader();
        BeanUtils.copyProperties(payeeSettingHeaderDTO, payeeSettingHeader);
        insertOrUpdate(payeeSettingHeader);
        return payeeSettingHeader;
    }

    /**
     * 根据ID删除收款方头记录
     *
     * @param headerId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteHeaderById(Long headerId) {
        PayeeSettingHeader payeeSettingHeader = selectById(headerId);
        if (payeeSettingHeader != null) {
            // 删除头信息
            deleteById(headerId);
            // 删除行信息
            payeeSettingLineService.deleteLineByHeadId(headerId);
        }
    }

    /**
     * 根据条件查询收款方配置头
     */
    public Page<PayeeSettingHeaderDTO> queryHeaderForReport(
                                                   String payeeCountryCode, String payeeCityCode,
                                                   String payerCountryCode, String payerCityCode,
                                                   Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);

        //  分页查询收款方
        List<PayeeSettingHeaderDTO> rsPayeeSettingHeaderDTOList = payeeSettingHeaderMapper.queryHeader(null,
                payeeCountryCode, payeeCityCode,
                payerCountryCode, payerCityCode,
                mybatisPage);

        //根据头id获取收款方行表配置
        if(rsPayeeSettingHeaderDTOList != null && rsPayeeSettingHeaderDTOList.size() > 0){
            List<PayeeSettingLineDTO> rsPayeeSettingLineDTOList
                    = payeeSettingLineMapper.queryLineByHeaderId(rsPayeeSettingHeaderDTOList.get(0).getId(), mybatisPage);
            rsPayeeSettingHeaderDTOList.get(0).setPayeeSettingLineDTOList(rsPayeeSettingLineDTOList);
        }

        mybatisPage.setRecords(rsPayeeSettingHeaderDTOList);
        return mybatisPage;
    }
}

package com.hand.hcf.app.ant.mdata.location.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.mdata.location.domain.PayeeHeader;
import com.hand.hcf.app.ant.mdata.location.dto.PayeeHeaderDTO;
import com.hand.hcf.app.ant.mdata.location.persistence.PayeeHeaderMapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zihao.yang
 * @create 2019-6-13 11:27:23
 * @remark
 */
@Service
public class PayeeService extends BaseService<PayeeHeaderMapper, PayeeHeader>{

    @Autowired
    private PayeeHeaderMapper payeeHeaderMapper;

    /**
     * 根据条件查询收款方头
     */
    public Page<PayeeHeaderDTO> queryForHeader(Long payeeHeaderId, String payeeType,
                                      String payeeCountryCode, String payeeCityCode,
                                      String payerCountryCode, String payerCityCode,
                                      String payeeCode, String payeeName,
                                      Pageable pageable) {
        Page mybatisPage = PageUtil.getPage(pageable);

        //  分页查询收款方
        List<PayeeHeaderDTO> rsPayeeHeaderDTOList = payeeHeaderMapper.selectForReport(payeeHeaderId, payeeType,
                payeeCountryCode, payeeCityCode,
                payerCountryCode, payerCityCode,
                payeeCode, payeeName,
                mybatisPage);

        mybatisPage.setRecords(rsPayeeHeaderDTOList);
        return mybatisPage;
    }
}

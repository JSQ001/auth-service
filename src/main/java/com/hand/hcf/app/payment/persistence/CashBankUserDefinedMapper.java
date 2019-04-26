package com.hand.hcf.app.payment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.payment.domain.CashBankUserDefined;
import com.hand.hcf.app.payment.web.dto.BankQueryAllDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @author dong.liu on 2017-11-07
 */
public interface CashBankUserDefinedMapper extends BaseMapper<CashBankUserDefined> {
    @Select({"<script>",
            "select * from (select address,country_code,country_name,province_code,province_name,city_code,city_name,district_code,district_name,swift_code,bank_code,bank_name " ,
            "from csh_banks_datas union all" ,
            "select address,country_code,country_name,province_code,province_name,city_code,city_name,district_code,district_name,swift_code,bank_code,",
            "bank_name from csh_banks_user_defined where tenant_id = #{tenantId} ) bank where 1=1",
            "<when test = 'bankCode!=null'>",
            "and bank.bank_code like '%' #{bankCode}  '%'",
            "</when>",
            "<when test = 'bankName!=null'>",
            "and bank.bank_name like '%' #{bankName}  '%'",
            "</when>",
            "</script>"
    })
    public List<BankQueryAllDTO> getAllBankByCond(
            RowBounds rowBounds,
            @Param("bankCode") String bankCode,
            @Param("bankName") String bankName,
            @Param("tenantId") Long tenantId
    );
}
package com.hand.hcf.app.ant.withholdingReimburse.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.ant.withholdingReimburse.dto.WithholdingReimburse;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface WithholdingReimburseMapper extends BaseMapper<WithholdingReimburse> {
}

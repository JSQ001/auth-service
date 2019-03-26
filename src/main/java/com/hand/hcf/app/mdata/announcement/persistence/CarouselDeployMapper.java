package com.hand.hcf.app.mdata.announcement.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.announcement.domain.Carousel;
import com.hand.hcf.app.mdata.announcement.domain.CarouselDeploy;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

/**
 * @Author: zhu.zhao
 * @Description:
 * @Date: 2019/1/14 10:27
 */
public interface CarouselDeployMapper extends BaseMapper<CarouselDeploy> {

    List<Carousel> selectCompanyCarousels(@Param("companyOid") UUID companyOid,
                                          @Param("enabled") Boolean enabled);
}

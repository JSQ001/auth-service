package com.hand.hcf.app.mdata.area.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.area.domain.Level;
import com.hand.hcf.app.mdata.area.dto.InternationalAreaDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Created by chenliangqin on 16/11/15.
 */
@Component
public interface LevelMapper extends BaseMapper<Level>{

    List<InternationalAreaDTO> getInternationalLevel(@Param("levelOid") UUID levelOid);

    Level findOneByLevelOidAndDeletedFalse(@Param("levelOid") UUID levelOid);
}

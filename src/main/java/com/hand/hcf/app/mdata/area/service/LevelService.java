package com.hand.hcf.app.mdata.area.service;

import com.hand.hcf.app.mdata.area.domain.Level;
import com.hand.hcf.app.mdata.area.dto.InternationalAreaDTO;
import com.hand.hcf.app.mdata.area.dto.LevelDTO;
import com.hand.hcf.app.mdata.area.persistence.LevelMapper;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 城市级别业务逻辑层
 * Created by chenliangqin on 16/11/16.
 */
@Service
@Transactional
public class LevelService extends BaseService<LevelMapper,Level> {

    private final Logger log = LoggerFactory.getLogger(LevelService.class);

    @Autowired
    private  LevelMapper levelMapper;

    public Level findOneByLevelOidAndDeletedFalse(UUID levelOid) {
        return  levelMapper.findOneByLevelOidAndDeletedFalse(levelOid);
    }

    @Transactional(readOnly = true)
    public LevelDTO getCityLevelV2(UUID levelOid, String language) {
        log.debug("Request to get Level : {},language:{}", levelOid, language);
        Level level = this.findOneByLevelOidAndDeletedFalse(levelOid);
        if (level == null) {
            throw new BizException(RespCode.LEVEL_6035001);
        }
        LevelDTO levelDTO = toDTO(level);

        List<InternationalAreaDTO> areasDTOs = levelMapper.getInternationalLevel(levelOid);
        areasDTOs.stream().map(u -> {
            u.setLevel(level);
            return u;
        }).collect(Collectors.toList());
        levelDTO.setInternationalAreaDTOS(areasDTOs);
        return levelDTO;
    }

    public static LevelDTO toDTO(Level level) {
        LevelDTO levelDTO = new LevelDTO();
        if (level != null) {
            BeanUtils.copyProperties(level, levelDTO);
        }
        return levelDTO;
    }
}

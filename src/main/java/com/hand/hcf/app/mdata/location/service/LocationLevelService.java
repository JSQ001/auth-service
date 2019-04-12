package com.hand.hcf.app.mdata.location.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.location.domain.LocationLevel;

import com.hand.hcf.app.mdata.location.domain.LocationLevelAssign;
import com.hand.hcf.app.mdata.parameter.persistence.LocationLevelMapper;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *      地区级别服务类
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/3/26
 */
@Service
public class LocationLevelService extends BaseService<LocationLevelMapper, LocationLevel> {

    @Autowired
    private SetOfBooksService setOfBooksService;

    @Autowired
    private LocationLevelAssignService locationLevelAssignService;

    /**
     * 条件查询地点级别信息
     * @param setOfBooksId 账套id
     * @param code 代码
     * @param name 名称
     * @param enabled 状态
     * @param page 分页参数
     */
    public List<LocationLevel> queryByCondition(Long setOfBooksId,
                                                String code,
                                                String name,
                                                Boolean enabled,
                                                Page page) {
        EntityWrapper<LocationLevel> wrapper = this.getWrapper();
        Wrapper<LocationLevel> eq = wrapper.eq("set_of_books_id", setOfBooksId)
                .like(StringUtils.hasText(code), "code", code)
                .like(StringUtils.hasText(name), "name", name)
                .eq(enabled != null, "enabled", enabled);
        List<LocationLevel> locationLevels = baseMapper.selectPage(page, eq);
        if (!CollectionUtils.isEmpty(locationLevels)){
            SetOfBooks setOfBooks = setOfBooksService.selectById(setOfBooksId);
            locationLevels.forEach(e -> {
                e.setSetOfBooksName(setOfBooks.getSetOfBooksName());
            });
        }
        return locationLevels;
    }

    /**
     * 根据id获得地点级别信息
     * @param id
     * @return
     */
    public LocationLevel getLocationLevelById(Long id){
        LocationLevel locationLevel = this.selectById(id);
        if(!StringUtils.isEmpty(locationLevel)){
            SetOfBooks setOfBooks = setOfBooksService.selectById(locationLevel.getSetOfBooksId());
            if(!StringUtils.isEmpty(setOfBooks)){
                locationLevel.setSetOfBooksName(setOfBooks.getSetOfBooksName());
            }
        }
        return locationLevel;
    }

    /**
     * 维护地点级别信息
     * @param locationLevel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public LocationLevel insertOrUpdateLocationLevel(LocationLevel locationLevel) {
        Long locationLevelId = locationLevel.getId();
        if(StringUtils.isEmpty(locationLevelId)  ) {
            // 账套下地点级别代码校验唯一性
            int count = this.selectCount(new EntityWrapper<LocationLevel>()
                    .eq("code", locationLevel.getCode())
                    .eq("set_of_books_id", locationLevel.getSetOfBooksId())
            );
            if (count != 0) {
                throw new BizException(RespCode.LOCATION_LEVEL_CODE_REPEAT);
            }
            this.insert(locationLevel);
        }else{
            LocationLevel oldLocationLevel = this.selectById(locationLevelId);
            if(oldLocationLevel == null) {
                throw new BizException(RespCode.LOCATION_LEVEL_NOT_EXIST);
            }
            //地点级别禁用时，清除该地点级别下已添加的所有地点
            if(!locationLevel.getEnabled()){
                locationLevelAssignService.deleteLocationLevelAssignByLevelId(locationLevelId);
            }
            this.updateById(locationLevel);
        }
        SetOfBooks setOfBooks = setOfBooksService.selectById(locationLevel.getSetOfBooksId());
        if(setOfBooks != null){
            locationLevel.setSetOfBooksName(setOfBooks.getSetOfBooksName());
        }
        return locationLevel;
    }

    /**
     * 根据地点id或级别ID或级别代码获取地点级别
     * @param locationId
     * @param levelId
     * @param levelCode
     * @return
     */
    public LocationLevel getLocationLevelByLocationIdOrLevelIdOrLevelCode(Long locationId, Long levelId, String levelCode) {
        List<Long> levelIds = null;
        if (locationId != null) {
            levelIds = locationLevelAssignService.selectList(
                    new EntityWrapper<LocationLevelAssign>().eq("location_id", locationId)
            ).stream().map(LocationLevelAssign::getLevelId).collect(Collectors.toList());
        }
        LocationLevel locationLevel = this.selectOne(
                new EntityWrapper<LocationLevel>()
                        .in(!CollectionUtils.isEmpty(levelIds), "id", levelIds)
                        .eq(levelId != null, "id", levelId)
                        .eq(levelCode != null, "code", levelCode)
        );
        return locationLevel;
    }
}

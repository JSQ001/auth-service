package com.hand.hcf.app.base.user.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.dto.VersionStatisticsDTO;
import com.hand.hcf.app.base.user.domain.UserVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserVersionMapper extends BaseMapper<UserVersion> {

    /**
     * 统计小版本信息
     *
     * @param platform
     * @param appVersion
     * @param subAppVersion
     * @param page
     * @return
     */
    List<VersionStatisticsDTO> searchSubVersionStatistics(@Param("platform") String platform,
                                                          @Param("appVersion") String appVersion,
                                                          @Param("subAppVersion") String subAppVersion,
                                                          Page<VersionStatisticsDTO> page);
}

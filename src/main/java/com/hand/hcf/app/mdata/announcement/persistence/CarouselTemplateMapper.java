package com.hand.hcf.app.mdata.announcement.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.mdata.announcement.domain.CarouselTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: zhu.zhao
 * @Description:
 * @Date: 2019/1/14 10:27
 */
public interface CarouselTemplateMapper extends BaseMapper<CarouselTemplate> {

    /**
     * 根据公告图片类型查询[enabled true; deleted false]
     * @param type
     * @param pagination
     * @return
     */
    List<CarouselTemplate> selectByType(@Param("type") String type,
                                        Pagination pagination);

    /**
     * 根据指定的图片类型和附件id查询
     * @param type
     * @param attachmentIds
     * @return
     */
    List<CarouselTemplate> selectByTypeAndAttachmentIds(@Param("type") String type,
                                                        @Param("attachmentIds") List<Long> attachmentIds);
}

package com.hand.hcf.app.mdata.responsibilityCenter.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.ResponsibilityCenter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ResponsibilityCenterMapper extends BaseMapper<ResponsibilityCenter> {
    /**
     * 获取当前所选账套下所有启用的责任中心，如果选择了公司，则只能选到分配给此公司的责任中心
     * @param setOfBooksId 账套id
     * @param info
     * @param codeFrom
     * @param codeTo
     * @param page
     * @return
     */
    List<ResponsibilityCenter> pageByResponsibilityCenterConditionByIds(@Param("setOfBooksId") Long setOfBooksId,
                                                                        @Param("info") String info,
                                                                        @Param("codeFrom") String codeFrom,
                                                                        @Param("codeTo") String codeTo,
                                                                        @Param("resCenterIdList") List<Long> resCenterIdList,
                                                                        @Param("enabled") Boolean enabled,
                                                                        Page page);
    /**
     * 分页查询责任中心
     *
     * @param keyword 责任中心代码或者名称
     * @param codeFrom 责任中心代码从
     * @param codeTo 责任中心代码至
     * @param setOfBooksId 账套Id
     * @param responsibilityCenterCode 责任中心代码
     * @param responsibilityCenterName 责任中心名称
     * @param enabled 启用禁用
     * @param page 分页
     * @return
     */
    List<ResponsibilityCenter> pageResponsibilityCenterBySetOfBooksId(@Param("keyword") String keyword,
                                                                      @Param("codeFrom") String codeFrom,
                                                                      @Param("codeTo") String codeTo,
                                                                      @Param("setOfBooksId") Long setOfBooksId,
                                                                      @Param("responsibilityCenterCode") String responsibilityCenterCode,
                                                                      @Param("responsibilityCenterName") String responsibilityCenterName,
                                                                      @Param("enabled") Boolean enabled,
                                                                      Page page);
}

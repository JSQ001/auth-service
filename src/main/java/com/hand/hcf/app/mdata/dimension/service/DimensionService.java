package com.hand.hcf.app.mdata.dimension.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.dimension.domain.Dimension;
import com.hand.hcf.app.mdata.dimension.persistence.DimensionMapper;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseI18nService;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.util.TypeConversionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class DimensionService extends BaseService<DimensionMapper, Dimension> {

    private final Logger log = LoggerFactory.getLogger(DimensionService.class);

    @Autowired
    private DimensionMapper dimensionMapper;
    @Autowired
    private SetOfBooksService setOfBooksService;

    @Autowired
    private DimensionItemService dimensionItemService;

    @Autowired
    private DimensionItemGroupService dimensionItemGroupService;

    @Autowired
    private BaseI18nService baseI18nService;

    //最多维度个数
    private static final int MAX_DIMENSION_NUM = 20;

    /**
     * 新建维度
     * @param dimension
     * @return
     */
    @Transactional
    public Dimension insertDimension(Dimension dimension) {
        log.debug("REST request to save dimension : {}", dimension);
        if (setOfBooksService.findSetOfBooksById(dimension.getSetOfBooksId()) == null) {
            throw  new BizException(RespCode.DIMENSION_SETOFBOOKS_NOT_EXIST);
        }
        if (TypeConversionUtils.isNotEmpty(dimension.getId())) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        //必输字段不能为空
        if (TypeConversionUtils.isEmpty(dimension.getDimensionCode())
                || TypeConversionUtils.isEmpty(dimension.getDimensionName())
                || TypeConversionUtils.isEmpty(dimension.getSetOfBooksId())
                || TypeConversionUtils.isEmpty(dimension.getDimensionSequence())
                ) {
            throw  new BizException(RespCode.SYS_FIELD_IS_NULL);
        }
        //维度数量校验、重复性校验
        check(dimension);

        dimensionMapper.insert(dimension);
        return dimension;
    }

    /**
     * 修改维度
     * @param dimension
     * @return
     */
    @Transactional
    public Dimension updateDimension(Dimension dimension) {
        log.debug("REST request to update dimension : {}", dimension);
        if (TypeConversionUtils.isEmpty(dimension.getId())) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        check(dimension);
        dimensionMapper.updateById(dimension);
        return dimension;
    }

    /**
     *  根据账套和其他条件查询维度
     * @param setOfBooksId
     * @param dimensionCode
     * @param dimensionName
     * @param enabled
     * @param page
     * @return
     */
    public List<Dimension> pageDimensionsBySetOfBooksIdAndCond(Long setOfBooksId,
                                                               String dimensionCode,
                                                               String dimensionName,
                                                               Boolean enabled,
                                                               Page page) {
        //如果未传入账套id，获取当前账套下的维度
        if (setOfBooksId == null) {
            setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        }
        List<Dimension> result = dimensionMapper.selectPage(
                page,
                new EntityWrapper<Dimension>()
                        .eq("set_of_books_id",setOfBooksId)
                        .like(TypeConversionUtils.isNotEmpty(dimensionCode), "dimension_code",dimensionCode)
                        .like(TypeConversionUtils.isNotEmpty(dimensionName), "dimension_name", dimensionName)
                        .eq(TypeConversionUtils.isNotEmpty(enabled), "enabled", enabled)
                        .orderBy("enabled",false)
                        .orderBy("dimension_sequence")
        );
        SetOfBooks setOfBooks = setOfBooksService.findSetOfBooksById(setOfBooksId);
        if (setOfBooks != null) {
            result.stream().forEach(e -> e.setSetOfBooksName(setOfBooks.getSetOfBooksName()));
        }
        return result;
    }

    //插入、更新时字段检验
    private void check(Dimension dimension){
        List<Dimension>  dimensionList = dimensionMapper.selectList(
                new EntityWrapper<Dimension>()
                        .eq("set_of_books_id",dimension.getSetOfBooksId())
                        .ne(TypeConversionUtils.isNotEmpty(dimension.getId()), "id", dimension.getId())
        );
        //插入时判断维度数量
        if (TypeConversionUtils.isEmpty(dimension.getId()) && dimensionList.size() >= MAX_DIMENSION_NUM) {
            throw  new BizException(RespCode.DIMENSION_QUANTITY_MORE_THEN_20);
        }
        //维度编码重复性校验
        if (TypeConversionUtils.isNotEmpty(dimension.getDimensionCode())) {
            if (dimensionList
                    .stream()
                    .filter(d -> d.getDimensionCode().equals(dimension.getDimensionCode()))
                    .count() > 0) {
                throw  new BizException(RespCode.DIMENSION_CODE_REPEAT);
            }
        }
        //维度名称重复性校验
        if (TypeConversionUtils.isNotEmpty(dimension.getDimensionName())) {
            if (dimensionList
                    .stream()
                    .filter(d -> d.getDimensionName().equals(dimension.getDimensionName()))
                    .count() > 0) {
                throw new BizException(RespCode.DIMENSION_NAME_REPEAT);
            }
        }
        //维度序号校验
        if (TypeConversionUtils.isNotEmpty(dimension.getDimensionSequence())) {
            if (dimension.getDimensionSequence() < 1 || dimension.getDimensionSequence() > 20) {
                throw  new BizException(RespCode.DIMENSION_SEQUENCE_MUST_BETWEEN_1_AND_20);
            }
            if (dimensionList
                    .stream()
                    .filter(d -> d.getDimensionSequence().equals(dimension.getDimensionSequence()))
                    .count() > 0) {
                throw  new BizException(RespCode.DIMENSION_SEQUENCE_REPEAT);
            }
        }
    }

    /**
     * 查询账套下未定义的维度序号
     * @param setOfBooksId
     * @return
     */
    public List<Integer> listUnselectedSequenceBySetOfBooksId(Long setOfBooksId) {
        List<Dimension>  dimensionList = dimensionMapper.selectList(
                new EntityWrapper<Dimension>()
                        .eq("set_of_books_id",setOfBooksId)
        );
        List<Integer> total = IntStream.rangeClosed(1, 20).boxed().collect(Collectors.toList());
        total.removeAll(dimensionList.stream().map(Dimension::getDimensionSequence).collect(Collectors.toList()));
        return total;
    }

    /**
     * 根据维度id删除维度、维值、维值组
     * @param dimensionId
     */
    @Transactional
    public void deleteDimensionById(Long dimensionId) {
        Dimension dimension = dimensionMapper.selectById(dimensionId);
        if (dimension == null) {
            return;
        }
        dimension.setDeleted(true);
        String randomNumeric = RandomStringUtils.randomNumeric(6);
        dimension.setDimensionCode(dimension.getDimensionCode() + "_DELETED_" + randomNumeric);
        dimension.setDimensionName(dimension.getDimensionName() + "_DELETED_" + randomNumeric);
        dimensionMapper.updateById(dimension);

        dimensionItemService.deleteByDimensionId(dimensionId);
        dimensionItemGroupService.deleteByDimensionId(dimensionId);
    }

    /**
     * 根据id查询维度详情
     * @param dimensionId
     * @return
     */
    public Dimension getDimensionById(Long dimensionId) {
        Dimension dimension = dimensionMapper.selectById(dimensionId);
        if (dimension == null) {
            return dimension;
        }
        SetOfBooks setOfBooks = setOfBooksService.findSetOfBooksById(dimension.getSetOfBooksId());
        if (setOfBooks != null) {
            dimension.setSetOfBooksName(setOfBooks.getSetOfBooksName());
        }
        dimension.setI18n(baseI18nService.getI18nMap(Dimension.class, dimension.getId()));
        return dimension;
    }

    public List<Dimension> getDimensionsByIds(List<Long> dimensionIds) {
        if (dimensionIds == null || dimensionIds.size() == 0) {
            return new ArrayList<>();
        } else {
            return dimensionMapper.selectList(new EntityWrapper<Dimension>().in("id", dimensionIds));
        }
    }

    public List<Dimension> listDimensionsByCompanyId(Long companyId) {
        return dimensionMapper.listDimensionsByCompanyId(OrgInformationUtil.getCurrentSetOfBookId(), companyId, true);
    }

    public List<Dimension> listDimensionsBySetOfBooksIdConditionByIgnoreIds(Long setOfBooksId,
                                                                            String dimensionCode,
                                                                            String dimensionName,
                                                                            Boolean enabled,
                                                                            List<Long> ignoreIds) {
        //如果未传入账套id，获取当前账套下的维度
        if (setOfBooksId == null) {
            setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        }
        List<Dimension> result = dimensionMapper.selectList(
                new EntityWrapper<Dimension>()
                        .eq("set_of_books_id",setOfBooksId)
                        .like(TypeConversionUtils.isNotEmpty(dimensionCode), "dimension_code",dimensionCode)
                        .like(TypeConversionUtils.isNotEmpty(dimensionName), "dimension_name", dimensionName)
                        .eq(TypeConversionUtils.isNotEmpty(enabled), "enabled", enabled)
                        .notIn(CollectionUtils.isNotEmpty(ignoreIds), "id", ignoreIds)
                        .orderBy("dimension_sequence")
        );
        SetOfBooks setOfBooks = setOfBooksService.findSetOfBooksById(setOfBooksId);
        if (setOfBooks != null) {
            result.stream().forEach(e -> e.setSetOfBooksName(setOfBooks.getSetOfBooksName()));
        }
        return result;
    }
}


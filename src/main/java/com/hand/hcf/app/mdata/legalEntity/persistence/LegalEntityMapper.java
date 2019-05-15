package com.hand.hcf.app.mdata.legalEntity.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.mdata.legalEntity.domain.LegalEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

/**
 * 法人实体数据存储层
 * Created by Strive on 17/9/4.
 */
public interface LegalEntityMapper extends BaseMapper<LegalEntity> {
    /**
     * 根据租户id查询法人实体信息
     * @param tenantId：租户id
     * @return
     */
    List<LegalEntity> findLegalEntityIdAndNameByTenantId(@Param("tenantId") Long tenantId);

    /**
     * 根据租户id分页查询法人实体信息
     * @param tenantId：租户id
     * @param isEnabled：是否啟用
     * @param isDesc：是否降序
     * @param keyword：法人实体名称
     * @param page：分页对象
     * @return
     */
    List<LegalEntity> findLegalEntityByTenantId(@Param("tenantId") Long tenantId, @Param("isEnabled") Boolean isEnabled, @Param("isDesc") Boolean isDesc, @Param("keyword") String keyword, Pagination page);

    /**
     * 根据法人实体oid并降序查询法人实体信息
     * @param legalEntityOids：法人实体oid集合
     * @return
     */
    List<LegalEntity> findLegalEntityByLegalEntityOidInOrderByEnableDesc(@Param("legalEntityOids") List<UUID> legalEntityOids, Pagination page);

    /**
     * 根据租户id查询不是包含此法人实体oid并根据状态降序查询法人实体信息
     * @param tenantId：租户id
     * @param keyword：关键词
     * @param legalEntityOid：法人实体oid
     * @param page：分页对象
     * @return
     */
    List<LegalEntity> findByTenantIdAndLegalEntityOidNotOrderByEnableDesc(@Param("tenantId") Long tenantId, @Param("keyword") String keyword, @Param("legalEntityOid") UUID legalEntityOid, Pagination page);

    /**
     * 根据租户id和是否启用统计法人实体条数信息
     * @param tenantId：租户id
     * @param enable：是否启用
     * @return：条数
     */
    Long countByTenantIdAndEnable(@Param("tenantId") Long tenantId, @Param("enable") boolean enable);

    /**
     * 根据法人实体id统计子法人实体条数
     * @param legalEntityId：法人实体id
     * @return
     */
    Long countSubLegalEntityByLegalEntityId(Long legalEntityId);

    /**
     * 查询法人实体信息还未生成二维码信息
     * @return
     */
    List<LegalEntity> findLegalEntityByAttachmentIdIsNull();

    /**
     * 根据法人实体id查询上级法人路径集合
     * @param legalEntityId
     * @return
     */
    List<String> findRootSiblingLegalEntityPathList(Long legalEntityId);

    /**
     * 根据法人实体id和上级法人id查询为此上级法人下的子级法人路径（不包含传递进来的法人实体id）
     * @param legalEntityId：法人实体id
     * @param parentLegalEntityId：上级法人实体id
     * @return
     */
    List<String> findSiblingCompanyPathList(@Param("legalEntityId") Long legalEntityId, @Param("parentLegalEntityId") Long parentLegalEntityId);

    /**
     * 根据上级法人实体id查询子级法人路径集合
     * @param parentLegalEntityId
     * @return
     */
    List<String> findChildrenLegalEntityPath(Long parentLegalEntityId);

    /**
     * 根据法人实体id获取状态
     * @param legalEntityId：法人实体id
     * @return
     */
    boolean getLegalEntityState(Long legalEntityId);

    /**
     * 根据角色id查询法人实体信息
     * @param roleId：角色id
     * @return
     */
    List<LegalEntity> findLegalEntityByRoleId(Long roleId);

    /**
     * 根据租户id、关键词模糊查询法人实体
     * @param tenantId：租户id
     * @param keyword：关键词
     * @param page：分页对象
     * @return
     */
    List<LegalEntity> findByTenantIdAndNameAndEnabledTrue(@Param("tenantId") Long tenantId, @Param("keyword") String keyword, Pagination page);

    /**
     * 根据账套id分页查询法人实体信息
     * @param setOfBooksId：账套id
     * @param isEnabled：是否启用
     * @param page：分页对象
     * @return
     */
    List<LegalEntity> findLegalEntityBySetOfBooksId(@Param("setOfBooksId") Long setOfBooksId, @Param("isEnabled") Boolean isEnabled, Pagination page);


    List<LegalEntity> findLegalEntityBySetOfBooksIdDataAuth(@Param("setOfBooksId") Long setOfBooksId,
                                                            @Param("isEnabled") Boolean isEnabled ,
                                                            @Param("dataAuthLabel") String dataAuthLabel);

    List<BasicCO> pageLegalEntityByInfoResultBasic(@Param("tenantId") Long tenantId,
                                                   @Param("code") String code,
                                                   @Param("name") String name,
                                                   Pagination myBatisPage);
}

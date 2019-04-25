package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.common.co.CashFlowItemCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.DataAuthorityUtil;
import com.hand.hcf.app.payment.domain.CashDefaultFlowItem;
import com.hand.hcf.app.payment.domain.CashFlowItem;
import com.hand.hcf.app.payment.persistence.CashFlowItemMapper;
import com.hand.hcf.app.payment.utils.RespCode;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

;

/**
 * Created by 韩雪 on 2017/9/6.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CashFlowItemService extends BaseService<CashFlowItemMapper,CashFlowItem> {

    private final CashDefaultFlowItemService cashDefaultFlowItemService;

    private final MapperFacade mapperFacade;

    public CashFlowItemService(CashDefaultFlowItemService cashDefaultFlowItemService, MapperFacade mapperFacade){
        this.cashDefaultFlowItemService = cashDefaultFlowItemService;
        this.mapperFacade = mapperFacade;
    }

    /**
     * 新增 现金流量项表
     *
     * @param cashFlowItem
     * @return
     */
    public CashFlowItem createCashFlowItem(CashFlowItem cashFlowItem){
        if(cashFlowItem.getId() != null){
            throw new BizException(RespCode.PAYMENT_CASH_FLOW_ITEM_ALREADY_EXISTS);
        }
        //一个账套id 下，现金流量项代码flow_code不允许重复
        if (baseMapper.selectList(
                new EntityWrapper<CashFlowItem>()
                        .eq("set_of_book_id",cashFlowItem.getSetOfBookId())
                        .eq("flow_code",cashFlowItem.getFlowCode())
        ).size() > 0 ){
            throw new BizException(RespCode.PAYMENT_CASH_FLOW_ITEM_NOT_ALLOWED_TO_REPEAT);
        }

        this.insert(cashFlowItem);
        return cashFlowItem;
    }

    /**
     * 修改 现金流量项表
     *
     * @param cashFlowItem
     * @return
     */
    public CashFlowItem updateCashFlowItem(CashFlowItem cashFlowItem){
        CashFlowItem cfi = baseMapper.selectById(cashFlowItem.getId());
        if(cfi == null){
            throw new BizException(RespCode.PAYMENT_CASH_FLOW_ITEM_NOT_EXIST);
        }

        if (false == cashFlowItem.getEnabled() && cfi.getEnabled() == true){
            List<CashDefaultFlowItem> cdfiList = cashDefaultFlowItemService.selectList(
                    new EntityWrapper<CashDefaultFlowItem>()
                            .eq("deleted",false)
                            .eq("cash_flow_item_id",cashFlowItem.getId())
                            .eq("enabled",true)
            );
            if (cdfiList.size() > 0){
                cdfiList.stream().forEach(cashDefaultFlowItem -> {
                    cashDefaultFlowItem.setEnabled(false);
                    cashDefaultFlowItemService.updateById(cashDefaultFlowItem);
                });
            }
        }
        baseMapper.updateById(cashFlowItem);
        return cashFlowItem;
    }

    /**
     * 删除 现金流量项表(逻辑删除)
     *
     * @param id
     */
    public void deleteCashFlowItem(Long id){
        CashFlowItem cashFlowItem = baseMapper.selectById(id);
        if(cashFlowItem == null){
            throw new BizException(RespCode.PAYMENT_CASH_FLOW_ITEM_NOT_EXIST);
        }
        cashFlowItem.setFlowCode(cashFlowItem.getFlowCode() + "_DELETED_" + RandomStringUtils.randomNumeric(6));
        cashFlowItem.setDeleted(Boolean.TRUE);
        this.updateById(cashFlowItem);
    }

    /**
     * 自定义条件查询 现金流量项表(分页)
     *
     * @param setOfBookId
     * @param flowCode
     * @param description
     * @param isEnabled
     * @param page
     * @return
     */
    public List<CashFlowItem> getCashFlowItemByCond(Long setOfBookId, String flowCode, String description, Boolean isEnabled, Page page, boolean dataAuthFlag){

        String dataAuthLabel = null;
        if(dataAuthFlag){
            Map<String,String> map = new HashMap<>();
            map.put(DataAuthorityUtil.TABLE_NAME,"csh_cash_flow_item");
            map.put(DataAuthorityUtil.SOB_COLUMN,"set_of_book_id");
            dataAuthLabel = DataAuthorityUtil.getDataAuthLabel(map);
        }
        return baseMapper.selectPage(page,
                new EntityWrapper<CashFlowItem>()
                        .where("deleted = false")
                        .like(flowCode != null, "flow_code",flowCode, SqlLike.DEFAULT)
                        .like(description != null, "description",description, SqlLike.DEFAULT)
                        .eq(isEnabled != null, "enabled",isEnabled)
                        .eq("set_of_book_id",setOfBookId)
                        .and(!StringUtils.isEmpty(dataAuthLabel), dataAuthLabel)
                        .orderBy("enabled",false)
                        .orderBy("flow_code")
        );
    }

    /**
     * 自定义条件查询 现金流量项表(不分页)
     *
     * @param setOfBookId
     * @param flowCode
     * @param description
     * @param isEnabled
     * @return
     */
    public List<CashFlowItem> getCashFlowItemAllByCond(Long setOfBookId, String flowCode, String description, Boolean isEnabled){
        return baseMapper.selectList(
                new EntityWrapper<CashFlowItem>()
                        .where("deleted = false")
                        .like(flowCode != null, "flow_code",flowCode, SqlLike.DEFAULT)
                        .like(description != null, "description",description, SqlLike.DEFAULT)
                        .eq(isEnabled != null, "enabled",isEnabled)
                        .eq("set_of_book_id",setOfBookId)
                        .orderBy("flow_code")
        );
    }

    /**
     * 批量新增 现金流量项表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashFlowItem> createCashFlowItemBatch(List<CashFlowItem> list){
        list.stream().forEach(cashFlowItem -> {
            createCashFlowItem(cashFlowItem);
        });
        return list;
    }

    /**
     * 批量修改 现金流量项表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashFlowItem> updateCashFlowItemBatch(List<CashFlowItem> list){
        list.stream().forEach(cashFlowItem -> {
            updateCashFlowItem(cashFlowItem);
        });
        return list;
    }

    /**
     * 批量删除 现金流量项表
     *
     * @param list
     * @return
     */
    @Transactional
    public void deleteCashFlowItemBatch(List<Long> list){
        list.stream().forEach(id -> {
            deleteCashFlowItem(id);
        });
    }


    /**
     * 根据id或者条件查询现金流量项
     *
     * @param selectId
     * @param setOfBooksId
     * @param code
     * @param name
     * @param page
     * @return
     */
    public Page<BasicCO> listCashFlowItemByIdOrCond(Long selectId, Long setOfBooksId, String code, String name, Page page){
        List<BasicCO> list = new ArrayList<>();

        if (selectId != null){
            CashFlowItem cashFlowItem = baseMapper.selectById(selectId);
            if (cashFlowItem != null){
                BasicCO basicCO =BasicCO
                        .builder()
                        .id(cashFlowItem.getId())
                        .name(cashFlowItem.getDescription())
                        .code(cashFlowItem.getFlowCode())
                        .build();
                list.add(basicCO);
            }
        }else {
            List<CashFlowItem> cashFlowItemList =  baseMapper.selectPage(page,
                    new EntityWrapper<CashFlowItem>()
                            .eq("deleted",false)
                            .eq("enabled",true)
                            .eq(setOfBooksId != null,"set_of_book_id",setOfBooksId)
                            .like(code != null,"flow_code",code)
                            .like(name != null,"description",name)
                            .orderBy("flow_code")
            );
            if (cashFlowItemList.size() > 0){
                cashFlowItemList.stream().forEach(cashFlowItem -> {
                    BasicCO basicCO =BasicCO
                            .builder()
                            .id(cashFlowItem.getId())
                            .name(cashFlowItem.getDescription())
                            .code(cashFlowItem.getFlowCode())
                            .build();
                    list.add(basicCO);
                });
            }
        }
        page.setRecords(list);
        return page;
    }

    /**
     * 根据code查询现金流项
     * @param code
     * @return
     */
    public CashFlowItemCO getCashFlowItemByCode(String code) {
        CashFlowItem cashFlowItem = this.selectOne(
                new EntityWrapper<CashFlowItem>()
                        .eq(code != null, "flow_code", code)
        );
        return mapperFacade.map(cashFlowItem, CashFlowItemCO.class);
    }


    /**
     * 根据代码、名称分页查询某个账套下，启用的不在id范围内的现金流量项
     *
     * @param setOfBookId
     * @param flowCode
     * @param description
     * @param enabled
     * @param existIdList
     * @param page
     * @return
     */
    public Page<CashFlowItemCO> getUndistributedCashFlowItemByCond(Long setOfBookId,String flowCode,String description,Boolean enabled,List<Long> existIdList,Page page){
        Page<CashFlowItemCO> result = new Page<>();

        List<CashFlowItemCO> cashFlowItemCOList = new ArrayList<>();
        if (enabled == null){
            enabled = true;
        }
        Page<CashFlowItem> cashFlowItemPage = this.selectPage(page,
                new EntityWrapper<CashFlowItem>()
                        .eq(setOfBookId != null,"set_of_book_id",setOfBookId)
                        .eq("enabled",enabled)
                        .like(flowCode != null,"flow_code",flowCode)
                        .like(description != null,"description",description)
                        .notIn("id",existIdList)
                        .orderBy("flow_code",true)
        );
        if (CollectionUtils.isNotEmpty(cashFlowItemPage.getRecords())){
            cashFlowItemPage.getRecords().forEach(cashFlowItem -> {
                CashFlowItemCO cashFlowItemCO = new CashFlowItemCO();
                mapperFacade.map(cashFlowItem,cashFlowItemCO);
                cashFlowItemCOList.add(cashFlowItemCO);
            });
        }

        result.setRecords(cashFlowItemCOList);
        return result;
    }
}

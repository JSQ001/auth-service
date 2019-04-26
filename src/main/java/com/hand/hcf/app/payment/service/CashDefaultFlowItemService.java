package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.payment.domain.CashDefaultFlowItem;
import com.hand.hcf.app.payment.domain.CashFlowItem;
import com.hand.hcf.app.payment.domain.CashTransactionClass;
import com.hand.hcf.app.payment.persistence.CashDefaultFlowItemMapper;
import com.hand.hcf.app.payment.persistence.CashFlowItemMapper;
import com.hand.hcf.app.payment.persistence.CashTransactionClassMapper;
import com.hand.hcf.app.payment.utils.RespCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 韩雪 on 2017/9/7.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CashDefaultFlowItemService extends BaseService<CashDefaultFlowItemMapper,CashDefaultFlowItem> {

    private final CashFlowItemMapper cashFlowItemMapper;

    private final CashTransactionClassMapper cashTransactionClassMapper;


    public CashDefaultFlowItemService(CashFlowItemMapper cashFlowItemMapper,
                                      CashTransactionClassMapper cashTransactionClassMapper){
        this.cashFlowItemMapper = cashFlowItemMapper;
        this.cashTransactionClassMapper = cashTransactionClassMapper;
    }

    /**
     * 新增 现金事务分类关联现金流量表
     *
     * @param cashDefaultFlowItem
     * @return
     */
    public CashDefaultFlowItem createCashDefaultFlowItem(CashDefaultFlowItem cashDefaultFlowItem){
        if (cashDefaultFlowItem.getId() != null){
            throw new BizException(RespCode.PAYMENT_CASH_DEFAULT_FLOW_ITEM_ALREADY_EXISTS);
        }
        //如果defaultFlag没有值，则将其设置为false
        if (cashDefaultFlowItem.getDefaultFlag() == null){
            cashDefaultFlowItem.setDefaultFlag(false);
        }

        //一个现金事务分类ID 下，现金流量项ID不允许重复
        if (baseMapper.selectList(
                new EntityWrapper<CashDefaultFlowItem>()
                        .eq("transaction_class_id",cashDefaultFlowItem.getTransactionClassId())
                        .eq("cash_flow_item_id",cashDefaultFlowItem.getCashFlowItemId())
        ).size() > 0){
            throw new BizException(RespCode.PAYMENT_CASH_DEFAULT_FLOW_ITEM_NOT_ALLOWED_TO_REPEAT);
        }

        //如果该数据的defaultFlag为true，则判断已有的数据中是否有defaultFlag字段为true的数据，如果有的话，则将原来有的数据的defaultFlag设为false
        //一个现金事务分类关联的现金流量项中只能有一个默认的
        if (cashDefaultFlowItem.getDefaultFlag() == true){
            List<CashDefaultFlowItem> cdfis = baseMapper.selectList(
                    new EntityWrapper<CashDefaultFlowItem>()
                            .where("deleted = false")
                            .eq("default_flag",true)
                            .eq("transaction_class_id",cashDefaultFlowItem.getTransactionClassId())
            );
            if (cdfis.size() == 1){
                CashDefaultFlowItem item = cdfis.get(0);
                item.setDefaultFlag(false);
                this.updateById(item);
            }
        }
        this.insert(cashDefaultFlowItem);
        return cashDefaultFlowItem;
    }

    /**
     * 修改 现金事务分类关联现金流量表
     *
     * @param cashDefaultFlowItem
     * @return
     */
    public CashDefaultFlowItem updateCashDefaultFlowItem(CashDefaultFlowItem cashDefaultFlowItem){
        if(cashDefaultFlowItem.getId() == null){
            throw new BizException(RespCode.PAYMENT_CASH_DEFAULT_FLOW_ITEM_NOT_EXIST);
        }
        CashDefaultFlowItem flowItem = baseMapper.selectById(cashDefaultFlowItem);
        //如果该数据的defaultFlag为true，则判断已有的数据中是否有defaultFlag字段为true的数据，如果有的话，则将原来有的数据的defaultFlag设为false
        //一个现金事务分类关联的现金流量项中只能有一个默认的
        if (cashDefaultFlowItem.getDefaultFlag() !=null && cashDefaultFlowItem.getDefaultFlag() == true){
            List<CashDefaultFlowItem> cdfis = baseMapper.selectList(
                    new EntityWrapper<CashDefaultFlowItem>()
                            .where("deleted = false")
                            .eq("default_flag",true)
                            .eq("transaction_class_id",flowItem.getTransactionClassId())
            );
            if (cdfis.size() == 1 && !cdfis.get(0).getId().equals(cashDefaultFlowItem.getId())){
                CashDefaultFlowItem item = cdfis.get(0);
                item.setDefaultFlag(false);
                this.updateById(item);
            }
        }

        this.updateById(cashDefaultFlowItem);
        return cashDefaultFlowItem;
    }

    /**
     * 删除 现金事务分类关联现金流量表(逻辑删除)
     *
     * @param id
     */
    public void deleteCashDefaultFlowItem(Long id){
        CashDefaultFlowItem cashDefaultFlowItem = baseMapper.selectById(id);
        if(cashDefaultFlowItem == null){
            throw new BizException(RespCode.PAYMENT_CASH_DEFAULT_FLOW_ITEM_NOT_EXIST);
        }

        baseMapper.deleteById(cashDefaultFlowItem.getId());
    }

    /**
     * 根据ID查询 现金事务分类关联现金流量表
     *
     * @param id
     * @return
     */
    public CashDefaultFlowItem getCashDefaultFlowItem(Long id){
        CashDefaultFlowItem cashDefaultFlowItem = baseMapper.selectById(id);
        if(cashDefaultFlowItem == null){
            throw new BizException(RespCode.PAYMENT_CASH_DEFAULT_FLOW_ITEM_NOT_EXIST);
        }

        CashFlowItem cashFlowItem = cashFlowItemMapper.selectById(cashDefaultFlowItem.getCashFlowItemId());
        if (cashFlowItem == null){
            throw new BizException(RespCode.PAYMENT_CASH_FLOW_ITEM_NOT_EXIST);
        }
        cashDefaultFlowItem.setCashFlowItemCode(cashFlowItem.getFlowCode());
        cashDefaultFlowItem.setCashFlowItemName(cashFlowItem.getDescription());



        return cashDefaultFlowItem;
    }

    /**
     * 自定义条件查询 现金事务分类关联现金流量表(分页)
     *
     * @param transactionClassId
     * @param defaultFlag
     * @param isEnabled
     * @param page
     * @return
     */
    public List<CashDefaultFlowItem> getCashDefaultFlowItemByCond(Long transactionClassId, Boolean defaultFlag, Boolean isEnabled, Page page){
//        List<CashDefaultFlowItem> cashDefaultFlowItemList = baseMapper.selectPage(page,
//                new EntityWrapper<CashDefaultFlowItem>()
//                        .where("deleted = false")
//                        .eq(defaultFlag != null, "default_flag",defaultFlag)
//                        .eq(isEnabled != null, "enabled",isEnabled)
//                        .eq(transactionClassId != null, "transaction_class_id",transactionClassId)
//                        .orderBy("id")
//        );
//        if (cashDefaultFlowItemList != null) {
//            for (CashDefaultFlowItem cashDefaultFlowItem : cashDefaultFlowItemList) {
//
//                CashFlowItem cfi = cashFlowItemMapper.selectById(cashDefaultFlowItem.getCashFlowItemId());
//                if (cfi == null) {
//                    throw new BizException(RespCode.PAYMENT_CASH_FLOW_ITEM_NOT_EXIST);
//                }
//                cashDefaultFlowItem.setCashFlowItemCode(cfi.getFlowCode());
//                cashDefaultFlowItem.setCashFlowItemName(cfi.getDescription());
//            }
//        }

        return baseMapper.getCashDefaultFlowItemByCond(defaultFlag,isEnabled,transactionClassId,page);
    }

    /**
     * 自定义条件查询 现金事务分类关联现金流量表(不分页)
     *
     * @param transactionClassId
     * @param defaultFlag
     * @param isEnabled
     * @return
     */
    public List<CashDefaultFlowItem> getCashDefaultFlowItemAllByCond(Long transactionClassId, Boolean defaultFlag, Boolean isEnabled){
        List<CashDefaultFlowItem> cashDefaultFlowItemList = baseMapper.selectList(
                new EntityWrapper<CashDefaultFlowItem>()
                        .where("deleted = false")
                        .eq(defaultFlag != null, "default_flag",defaultFlag)
                        .eq(isEnabled != null, "enabled",isEnabled)
                        .eq(transactionClassId != null, "transaction_class_id",transactionClassId)
                        .orderBy("created_date")
        );
        if (cashDefaultFlowItemList != null) {
            for (CashDefaultFlowItem cashDefaultFlowItem : cashDefaultFlowItemList) {
                CashFlowItem cfi = cashFlowItemMapper.selectById(cashDefaultFlowItem.getCashFlowItemId());
                if (cfi == null) {
                    throw new BizException(RespCode.PAYMENT_CASH_FLOW_ITEM_NOT_EXIST);
                }
                cashDefaultFlowItem.setCashFlowItemCode(cfi.getFlowCode());
                cashDefaultFlowItem.setCashFlowItemName(cfi.getDescription());
            }
        }

        return cashDefaultFlowItemList;
    }

    /**
     * 查询尚未分配的现金流量项
     *
     * @param setOfBookId
     * @param transactionClassId
     * @param flowCode
     * @param description
     * @param page
     * @return
     */
    public List<CashFlowItem> getNotSaveFlowItem(Long setOfBookId,Long transactionClassId,String flowCode,String description,Page page){
        List<CashFlowItem> list = baseMapper.getNotSaveFlowItem(setOfBookId,transactionClassId,flowCode,description,page);
        return list;


    }

    /**
     * 批量新增 现金事务分类关联现金流量表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashDefaultFlowItem> createCashDefaultFlowItemBatch(List<CashDefaultFlowItem> list){
        list.stream().forEach(cashDefaultFlowItem -> {
            createCashDefaultFlowItem(cashDefaultFlowItem);
        });
        return list;
    }

    /**
     * 批量修改 现金事务分类关联现金流量表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashDefaultFlowItem> updateCashDefaultFlowItemBatch(List<CashDefaultFlowItem> list){
        list.stream().forEach(cashDefaultFlowItem -> {
            updateCashDefaultFlowItem(cashDefaultFlowItem);
        });
        return list;
    }

    /**
     * 批量删除 现金事务分类关联现金流量表
     *
     * @param list
     */
    @Transactional
    public void deleteCashDefaultFlowItemBatch(List<Long> list){
        list.stream().forEach(id -> {
            deleteCashDefaultFlowItem(id);
        });
    }

    /**
     * 给artemis、prepayment 提供
     * 根据现金事务分类ID->transactionClassId，返回现金事务分类code、现金事务分类name，
     * 以及该现金事务分类下的默认现金流量项的code和name
     *
     * @param transactionClassId
     * @return
     */
    public CashDefaultFlowItem getCashDefaultFlowItemByTransactionClassId(Long transactionClassId){
        List<CashDefaultFlowItem> cashDefaultFlowItemList = baseMapper.selectList(
                new EntityWrapper<CashDefaultFlowItem>()
                .eq(transactionClassId != null,"transaction_class_id",transactionClassId)
                .eq("default_flag",true)
        );

        if (cashDefaultFlowItemList.size() == 1){
            CashDefaultFlowItem cashDefaultFlowItem = cashDefaultFlowItemList.get(0);
            //返回现金事务分类code、name
            CashTransactionClass cashTransactionClass = cashTransactionClassMapper.selectById(cashDefaultFlowItem.getTransactionClassId());
            if(cashTransactionClass != null){
                cashDefaultFlowItem.setTransactionClassCode(cashTransactionClass.getClassCode());
                cashDefaultFlowItem.setTransactionClassName(cashTransactionClass.getDescription());
            }
            //返回现金流量项code、name
            CashFlowItem cashFlowItem = cashFlowItemMapper.selectById(cashDefaultFlowItem.getCashFlowItemId());
            if (cashFlowItem != null){
                cashDefaultFlowItem.setCashFlowItemCode(cashFlowItem.getFlowCode());
                cashDefaultFlowItem.setCashFlowItemName(cashFlowItem.getDescription());
            }
            return cashDefaultFlowItem;
        }

        return null;
    }
}

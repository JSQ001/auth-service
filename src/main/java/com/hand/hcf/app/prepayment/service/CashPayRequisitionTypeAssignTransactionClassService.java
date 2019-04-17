package com.hand.hcf.app.prepayment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CashTransactionClassCO;
import com.hand.hcf.app.common.co.CashTransactionClassForOtherCO;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionTypeAssignTransactionClass;
import com.hand.hcf.app.prepayment.externalApi.PaymentModuleInterface;
import com.hand.hcf.app.prepayment.persistence.CashPayRequisitionTypeAssignTransactionClassMapper;
import com.hand.hcf.app.prepayment.persistence.CashPayRequisitionTypeMapper;
import com.hand.hcf.app.prepayment.utils.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by 韩雪 on 2017/10/25.
 */
@Service
@Transactional
public class CashPayRequisitionTypeAssignTransactionClassService extends BaseService<CashPayRequisitionTypeAssignTransactionClassMapper,CashPayRequisitionTypeAssignTransactionClass> {
    @Autowired
    private  CashPayRequisitionTypeMapper cashPayRequisitionTypeMapper;
    @Autowired
    private PaymentModuleInterface paymentModuleInterface;

    /**
     * 批量新增 预付款单类型关联的现金事务分类表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashPayRequisitionTypeAssignTransactionClass> createCashPayRequisitionTypeAssignTransactionClassBatch(List<CashPayRequisitionTypeAssignTransactionClass> list){
        list.stream().forEach(cashSobPayReqTypeAssignTransactionClass -> {
            if (cashSobPayReqTypeAssignTransactionClass.getId() != null){
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_TRANSACTIONCLASS_ALREADY_EXISTS);
            }

            //设置条件
            EntityWrapper<CashPayRequisitionTypeAssignTransactionClass> ew = new EntityWrapper<>();
            ew.where("sob_pay_req_type_id = {0}", cashSobPayReqTypeAssignTransactionClass.getSobPayReqTypeId());
            //一个预付款单类型ID 下，现金事务分类ID不允许重复;通过sobPayReqTypeId查询list集合
            List<CashPayRequisitionTypeAssignTransactionClass> cprtatcs = baseMapper.selectList(ew);
            //对查询结果进行校验
            if (cprtatcs.stream().anyMatch(u -> u.getTransactionClassId().equals(cashSobPayReqTypeAssignTransactionClass.getTransactionClassId()))) {
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_TRANSACTIONCLASS_NOT_ALLOWED_TO_REPEAT);
            }
            baseMapper.insert(cashSobPayReqTypeAssignTransactionClass);
        });
        return list;
    }

    /**
     * 批量修改 预付款单类型关联的现金事务分类表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashPayRequisitionTypeAssignTransactionClass> updateCashPayRequisitionTypeAssignTransactionClassBatch(List<CashPayRequisitionTypeAssignTransactionClass> list){
        list.stream().forEach(cashSobPayReqTypeAssignTransactionClass -> {
            if (cashSobPayReqTypeAssignTransactionClass.getId() == null){
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_TRANSACTIONCLASS_NOT_EXIST);
            }
            baseMapper.updateById(cashSobPayReqTypeAssignTransactionClass);
        });
        return list;
    }

    /**
     * 批量删除 预付款单类型关联的现金事务分类表(物理删除)
     *
     * @param list
     */
    @Transactional
    public void deleteCashPayRequisitionTypeAssignTransactionClassBatch(List<Long> list){
        list.stream().forEach(id -> {
            baseMapper.deleteById(id);
        });
    }

    /**
     * 根据预付款单类型ID->sobPayReqTypeId 查询出与之对应的现金事务分类表中的数据，前台显示现金事务分类表代码以及现金事务分类表名称(分页)
     *
     * @param sobPayReqTypeId
     * @param page
     * @return
     */
    public List<CashPayRequisitionTypeAssignTransactionClass> getCashPayRequisitionTypeAssignTransactionClassByCond(Long sobPayReqTypeId, Page page){
        List<CashPayRequisitionTypeAssignTransactionClass> list = baseMapper.selectPage(page,
                new EntityWrapper<CashPayRequisitionTypeAssignTransactionClass>()
                        .eq("sob_pay_req_type_id",sobPayReqTypeId)
        );

        List<Long> cashTransactionClassIdList = list.stream().map(cashPayRequisitionTypeAssignTransactionClass -> cashPayRequisitionTypeAssignTransactionClass.getTransactionClassId()).collect(toList());
        //jiu.zhao 支付
        /*if (cashTransactionClassIdList != null){
            List<CashTransactionClassCO> cashTransactionClassDTOList = paymentModuleInterface.listCashTransactionClassByIdList(cashTransactionClassIdList);

            list.stream().forEach(c1 -> {
                cashTransactionClassDTOList.stream().forEach(c2 -> {
                    if (c2.getId().equals(c1.getTransactionClassId())){
                        c1.setTransactionClassCode(c2.getClassCode());
                        c1.setTransactionClassName(c2.getDescription());
                    }
                });
            });
        }*/

        return list;
    }

    /**
     * 根据预付款单类型ID->sobPayReqTypeId 查询出与之对应的现金事务分类表中的数据，前台显示现金事务分类表代码以及现金事务分类表名称(不分页)
     *
     * @param sobPayReqTypeId
     * @return
     */
    public List<CashPayRequisitionTypeAssignTransactionClass> getCashPayRequisitionTypeAssignTransactionClassAllByCond(Long sobPayReqTypeId){
        List<CashPayRequisitionTypeAssignTransactionClass> list = baseMapper.selectList(
                new EntityWrapper<CashPayRequisitionTypeAssignTransactionClass>()
                        .eq("sob_pay_req_type_id",sobPayReqTypeId)
        );

        List<Long> cashTransactionClassIdList = list.stream().map(cashPayRequisitionTypeAssignTransactionClass -> cashPayRequisitionTypeAssignTransactionClass.getTransactionClassId()).collect(toList());
        //jiu.zhao 支付
        /*if (cashTransactionClassIdList != null){
            List<CashTransactionClassCO> cashTransactionClassDTOList = paymentModuleInterface.listCashTransactionClassByIdList(cashTransactionClassIdList);

            list.stream().forEach(c1 -> {
                cashTransactionClassDTOList.stream().forEach(c2 -> {
                    if (c1.getTransactionClassId().equals(c2.getId())){
                        c1.setTransactionClassCode(c2.getClassCode());
                        c1.setTransactionClassName(c2.getDescription());
                    }
                });
            });
        }*/

        return list;
    }

    /**
     * 获取当前账套下，启用的、现金事务类型为PREPAYMENT(预付款) 的 现金事务分类
     *
     * @param setOfBookId
     * @return
     */
    public List<CashTransactionClassCO> getCashTransactionClassBySetOfBookId(Long setOfBookId){
        /*List<CashTransactionClassCO> cashTransactionClassCOList = paymentModuleInterface.listCashTransactionClassBySetOfBookId(setOfBookId);
        return cashTransactionClassCOList;*/
        //jiu.zhao 支付
        return null;
    }

    /**
     * 根据所选范围 查询现金事务分类(分页)
     *
     * @param forOtherCO
     * @param page
     * @return
     */

    public Page<CashTransactionClassCO> getCashPayRequisitionTypeAssignTransactionClassByCond(CashTransactionClassForOtherCO forOtherCO, Page page) throws Exception{
        List<CashTransactionClassCO> list = new ArrayList<>();

        //sobPayReqTypeId不为null，说明是更新
        if (null != forOtherCO.getSobPayReqTypeId()){
            //之前该条预付款单类型关联的现金事务分类是部分的
            if(false == cashPayRequisitionTypeMapper.selectById(forOtherCO.getSobPayReqTypeId()).getAllClass() ){
                List<Long> transactionClassIdList = baseMapper.selectList(
                        new EntityWrapper<CashPayRequisitionTypeAssignTransactionClass>()
                                .eq("sob_pay_req_type_id",forOtherCO.getSobPayReqTypeId())
                ).stream().map(CashPayRequisitionTypeAssignTransactionClass::getTransactionClassId).collect(toList());
                forOtherCO.setTransactionClassIdList(transactionClassIdList);
            }
        }

        //全部：all、已选：selected、未选：notChoose
        //jiu.zhao 支付
        /*if (forOtherCO.getRange().equals("selected")){
            list = paymentModuleInterface.listTransactionClassByRange(forOtherCO,page);
            list.stream().forEach(cashTransactionClassDTO -> cashTransactionClassDTO.setAssigned(true));
        }else if (forOtherCO.getRange().equals("notChoose")){
            list = paymentModuleInterface.listTransactionClassByRange(forOtherCO,page);
            list.stream().forEach(cashTransactionClassDTO -> cashTransactionClassDTO.setAssigned(false));
        }else if (forOtherCO.getRange().equals("all")){

            List<CashTransactionClassCO> transactionClassDTOList = paymentModuleInterface.listTransactionClassByRange(forOtherCO, page);
            if (transactionClassDTOList.size() < page.getSize() * page.getCurrent()){
                for (int i = (page.getCurrent()-1)*page.getSize();i < transactionClassDTOList.size();i++){
                        list.add(transactionClassDTOList.get(i));
                }
            }else {
                for (int i = (page.getCurrent() - 1) * page.getSize(); i < page.getCurrent() * page.getSize(); i++) {
                        list.add(transactionClassDTOList.get(i));
                }
            }
        }*/

        if(list != null){
            page.setRecords(list);
        }
        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteDataByTypeId(Long typeId){
        baseMapper.delete(new EntityWrapper<CashPayRequisitionTypeAssignTransactionClass>().eq("sob_pay_req_type_id", typeId));
    }
}

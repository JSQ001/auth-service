package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.common.co.CashTransactionClassForOtherCO;
import com.hand.hcf.app.common.co.SetOfBooksInfoCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.DataAuthorityUtil;
import com.hand.hcf.app.payment.domain.CashTransactionClass;
import com.hand.hcf.app.payment.domain.PaymentSystemCustomEnumerationType;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.persistence.CashTransactionClassMapper;
import com.hand.hcf.app.payment.utils.RespCode;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by 韩雪 on 2017/9/7.
 */
@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class CashTransactionClassService extends BaseService<CashTransactionClassMapper,CashTransactionClass> {

    private final PaymentOrganizationService organizationService;

    /**
     * 新增 现金事务分类表
     *
     * @param cashTransactionClass
     * @return
     */
    public CashTransactionClass createCashTransactionClass(CashTransactionClass cashTransactionClass){
        if(cashTransactionClass.getId() != null){
            throw new BizException(RespCode.PAYMENT_CASH_TRANSACTION_CLASS_ALREADY_EXISTS);
        }
        //一个账套id 下，现金事务分类代码class_code不允许重复
        if (baseMapper.selectList(
                new EntityWrapper<CashTransactionClass>()
                        .eq("set_of_book_id",cashTransactionClass.getSetOfBookId())
                        .eq("class_code",cashTransactionClass.getClassCode())
        ).size() > 0){
            throw new BizException(RespCode.PAYMENT_CASH_TRANSACTION_CLASS_NOT_ALLOWED_TO_REPEAT);
        }

        baseMapper.insert(cashTransactionClass);
        return cashTransactionClass;
    }

    /**
     * 修改 现金事务分类表
     *
     * @param cashTransactionClass
     * @return
     */
    public CashTransactionClass updateCashTransactionClass(CashTransactionClass cashTransactionClass){
        if (cashTransactionClass.getId() == null){
            throw new BizException(RespCode.PAYMENT_CASH_TRANSACTION_CLASS_NOT_EXIST);
        }

        this.updateById(cashTransactionClass);
        return cashTransactionClass;
    }

    /**
     * 删除 现金事务分类表(逻辑删除)
     *
     * @param id
     */
    public void deleteCashTransactionClass(Long id){
        CashTransactionClass cashTransactionClass = baseMapper.selectById(id);
        if(cashTransactionClass == null){
            throw new BizException(RespCode.PAYMENT_CASH_TRANSACTION_CLASS_NOT_EXIST);
        }
        cashTransactionClass.setClassCode(cashTransactionClass.getClassCode() + "_DELETED_" + RandomStringUtils.randomNumeric(6));
        cashTransactionClass.setDeleted(Boolean.TRUE);
        this.updateById(cashTransactionClass);
    }

    /**
     * 根据ID查询 现金事务分类表
     *
     * @param id
     * @return
     */
    public CashTransactionClass getCashTransactionClass(Long id){
        CashTransactionClass cashTransactionClass = baseMapper.selectById(id);

        //返回账套code、账套name
        SetOfBooksInfoCO standardCO = organizationService.getSetOfBooksById(cashTransactionClass.getSetOfBookId());
        if (standardCO != null) {
            cashTransactionClass.setSetOfBookCode(standardCO.getSetOfBooksCode());
            cashTransactionClass.setSetOfBookName(standardCO.getSetOfBooksName());
        }
        //返回现金交易事务类型name
        if (cashTransactionClass.getTypeCode() != null) {
            cashTransactionClass.setTypeName(organizationService.getSysCodeValueByCodeAndValue(PaymentSystemCustomEnumerationType.CSH_TRANSACTION_TYPE,
                    cashTransactionClass.getTypeCode()).getName());
        }

        return cashTransactionClass;
    }

    /**
     * 自定义条件查询 现金事务分类表(分页)
     *
     * @param setOfBookId
     * @param classCode
     * @param description
     * @param typeCode
     * @param isEnabled
     * @param page
     * @return
     */
    public List<CashTransactionClass> getCashTransactionClassByCond(Long setOfBookId,String classCode,String description,String typeCode,Boolean isEnabled,Page page, boolean dataAuthFlag){

        String dataAuthLabel = null;
        if(dataAuthFlag){
            Map<String,String> map = new HashMap<>();
            map.put(DataAuthorityUtil.TABLE_NAME, "csh_transaction_class");
            map.put(DataAuthorityUtil.SOB_COLUMN, "set_of_book_id");
            dataAuthLabel = DataAuthorityUtil.getDataAuthLabel(map);
        }

        List<CashTransactionClass> list = baseMapper.selectPage(page,
                new EntityWrapper<CashTransactionClass>()
                        .where("deleted = false")
                        .eq(typeCode != null, "type_code",typeCode)
                        .like(classCode != null, "class_code",classCode, SqlLike.DEFAULT)
                        .like(description != null, "description",description, SqlLike.DEFAULT)
                        .eq(isEnabled != null, "enabled",isEnabled)
                        .eq("set_of_book_id",setOfBookId)
                        .and(!StringUtils.isEmpty(dataAuthLabel), dataAuthLabel)
                        .orderBy("enabled",false)
                        .orderBy("type_code")
                        .orderBy("class_code")
        );
        for (CashTransactionClass cashTransactionClass : list){
            //返回账套code、账套name
            SetOfBooksInfoCO standardCO = organizationService.getSetOfBooksById(cashTransactionClass.getSetOfBookId());
            if (standardCO != null) {
                cashTransactionClass.setSetOfBookCode(standardCO.getSetOfBooksCode());
                cashTransactionClass.setSetOfBookName(standardCO.getSetOfBooksName());
            }

            //返回现金交易事务类型name
            if (cashTransactionClass.getTypeCode() != null) {
                cashTransactionClass.setTypeName(organizationService.getSysCodeValueByCodeAndValue(PaymentSystemCustomEnumerationType.CSH_TRANSACTION_TYPE,
                        cashTransactionClass.getTypeCode()).getName());
            }
        }

        return list;
    }

    /**
     * 自定义条件查询 现金事务分类表(不分页)
     *
     * @param setOfBookId
     * @param classCode
     * @param description
     * @param typeCode
     * @return
     */
    public List<CashTransactionClass> getCashTransactionClassAllByCond(Long setOfBookId,String classCode,String description,String typeCode,Boolean isEnabled){
        List<CashTransactionClass> list = baseMapper.selectList(
                new EntityWrapper<CashTransactionClass>()
                        .where("deleted = false")
                        .eq(typeCode != null, "type_code",typeCode)
                        .like(classCode != null, "class_code",classCode, SqlLike.DEFAULT)
                        .like(description != null, "description",description, SqlLike.DEFAULT)
                        .eq(isEnabled != null, "enabled",isEnabled)
                        .eq("set_of_book_id",setOfBookId)
                        .orderBy("type_code")
                        .orderBy("class_code")
        );
        for (CashTransactionClass cashTransactionClass : list) {
            //返回账套code、账套name
            SetOfBooksInfoCO standardCO = organizationService.getSetOfBooksById(cashTransactionClass.getSetOfBookId());
            if (standardCO != null) {
                cashTransactionClass.setSetOfBookCode(standardCO.getSetOfBooksCode());
                cashTransactionClass.setSetOfBookName(standardCO.getSetOfBooksName());
            }

            //返回现金交易事务类型name
            if (cashTransactionClass.getTypeCode() != null) {
                cashTransactionClass.setTypeName(organizationService.getSysCodeValueByCodeAndValue(PaymentSystemCustomEnumerationType.CSH_TRANSACTION_TYPE,
                        cashTransactionClass.getTypeCode()).getName());
            }
        }

        return list;
    }

    /**
     * 批量新增 现金事务分类表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashTransactionClass> createCashTransactionClassBatch(List<CashTransactionClass> list){
        list.stream().forEach(cashTransactionClass -> {
            createCashTransactionClass(cashTransactionClass);
        });
        return list;
    }

    /**
     * 批量修改 现金事务分类表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashTransactionClass> updateCashTransactionClassBatch(List<CashTransactionClass> list){
        list.stream().forEach(cashTransactionClass -> {
            updateCashTransactionClass(cashTransactionClass);
        });
        return list;
    }

    /**
     * 批量删除 现金事务分类表(逻辑删除)
     *
     * @param list
     */
    @Transactional
    public void deleteCashTransactionClassBatch(List<Long> list){
        list.stream().forEach(id -> {
            deleteCashTransactionClass(id);
        });
    }



    /**
     * 获取当前账套下的，启用的、现金事务类型为PREPAYMENT(预付款) 的 现金事务分类
     *
     * @param setOfBookId
     * @return
     */
    public List<CashTransactionClass> listCashTransactionClassBySetOfBookId(Long setOfBookId){
        return baseMapper.selectList(
                new EntityWrapper<CashTransactionClass>()
                        .where("deleted = false")
                        .eq("set_of_book_id",setOfBookId)
                        .eq("type_code","PREPAYMENT")
                        .eq("enabled",true)
                        .orderBy("class_code",true)
        );
    }

    /**
     * 根据现金事务分类ID集合查询详情
     *
     * @param list
     * @return
     */
    public List<CashTransactionClass> listCashTransactionClassByIdList(List<Long> list){
        return baseMapper.selectList(
                new EntityWrapper<CashTransactionClass>()
                        .in("id",list)
                        .eq("enabled",true)
                        .eq("deleted",false)
                        .orderBy("class_code",true)
        );
    }

    public CashTransactionClass getCashTransactionClassById(Long id){


        CashTransactionClass transactionClass = baseMapper.getById(id);
        return transactionClass;
    }

    /**
     * 给artemis提供，对公报销单部分
     * 获取某个表单下，当前账套下、启用的、PAYMENT类型的 已分配的、未分配的、全部的 现金事物分类
     *
     * @param forArtemisCO
     * @param page
     * @return
     */
    public Page<CashTransactionClass> listCashTransactionClassByRange(CashTransactionClassForOtherCO forArtemisCO, Page page){
        List<CashTransactionClass> classList = new ArrayList<>();
        //全部：all、已选：selected、未选：notChoose
        if (forArtemisCO.getRange().equals("selected")){
            if ( CollectionUtils.isEmpty(forArtemisCO.getTransactionClassIdList()) ){
                page.setRecords(classList);
                return page;
            }else {
                return this.selectPage(page,
                        new EntityWrapper<CashTransactionClass>()
                                .eq("deleted", false)
                                .eq("enabled", true)
                                .eq("set_of_book_id", forArtemisCO.getSetOfBookId())
                                .eq("type_code", "PAYMENT")
                                .in("id", forArtemisCO.getTransactionClassIdList())
                                .like(forArtemisCO.getClassCode() != null, "class_code", forArtemisCO.getClassCode())
                                .like(forArtemisCO.getDescription() != null, "description", forArtemisCO.getDescription())
                                .orderBy("class_code")
                );
            }
        }else if (forArtemisCO.getRange().equals("notChoose")){
            return this.selectPage(page,
                    new EntityWrapper<CashTransactionClass>()
                            .eq("deleted",false)
                            .eq("enabled",true)
                            .eq("set_of_book_id",forArtemisCO.getSetOfBookId())
                            .eq("type_code","PAYMENT")
                            .notIn("id",forArtemisCO.getTransactionClassIdList())
                            .like(forArtemisCO.getClassCode() != null,"class_code",forArtemisCO.getClassCode())
                            .like(forArtemisCO.getDescription() != null,"description",forArtemisCO.getDescription())
                            .orderBy("class_code")
            );
        }else if (forArtemisCO.getRange().equals("all")){
            List<CashTransactionClass> list = new ArrayList<>();
            if ( !CollectionUtils.isEmpty(forArtemisCO.getTransactionClassIdList()) ){
                List<CashTransactionClass> list1 = this.selectList(
                        new EntityWrapper<CashTransactionClass>()
                                .eq("deleted", false)
                                .eq("enabled", true)
                                .eq("set_of_book_id", forArtemisCO.getSetOfBookId())
                                .eq("type_code", "PAYMENT")
                                .in("id", forArtemisCO.getTransactionClassIdList())
                                .like(forArtemisCO.getClassCode() != null, "class_code", forArtemisCO.getClassCode())
                                .like(forArtemisCO.getDescription() != null, "description", forArtemisCO.getDescription())
                                .orderBy("class_code")
                );
                list1.stream().forEach(cashTransactionClass -> {
                    cashTransactionClass.setAssigned(true);
                    list.add(cashTransactionClass);
                });
            }
            List<CashTransactionClass> list2 = this.selectList(
                    new EntityWrapper<CashTransactionClass>()
                            .eq("deleted",false)
                            .eq("enabled",true)
                            .eq("set_of_book_id",forArtemisCO.getSetOfBookId())
                            .eq("type_code","PAYMENT")
                            .notIn("id",forArtemisCO.getTransactionClassIdList())
                            .like(forArtemisCO.getClassCode() != null,"class_code",forArtemisCO.getClassCode())
                            .like(forArtemisCO.getDescription() != null,"description",forArtemisCO.getDescription())
                            .orderBy("class_code")
            );
            list2.stream().forEach(cashTransactionClass -> {
                cashTransactionClass.setAssigned(false);
                list.add(cashTransactionClass);
            });
            page.setTotal(list.size());
            page.setRecords(list);
            return page;
        }
        return page;
    }

    /**
     * 给prepayment提供，对公报销单部分
     * 获取某个表单下，当前账套下 已分配的、未分配的 现金事物分类
     *
     * @param forArtemisCO
     * @param page
     * @return
     */
    public Page<CashTransactionClass> listCashTransactionClassForPerPayByRange(CashTransactionClassForOtherCO forArtemisCO, Page page){
        List<CashTransactionClass> classList = new ArrayList<>();
        //全部：all、已选：selected、未选：notChoose
        if (forArtemisCO.getRange().equals("selected")){
            if ( CollectionUtils.isEmpty(forArtemisCO.getTransactionClassIdList()) ){
                page.setRecords(classList);
                return page;
            }else {
                return this.selectPage(page,
                        new EntityWrapper<CashTransactionClass>()
                                .eq("deleted", false)
                                .eq("enabled", true)
                                .eq("set_of_book_id", forArtemisCO.getSetOfBookId())
                                .eq("type_code", "PREPAYMENT")
                                .in("id", forArtemisCO.getTransactionClassIdList())
                                .like(forArtemisCO.getClassCode() != null, "class_code", forArtemisCO.getClassCode())
                                .like(forArtemisCO.getDescription() != null, "description", forArtemisCO.getDescription())
                                .orderBy("class_code")
                );
            }
        }else if (forArtemisCO.getRange().equals("notChoose")){
            return this.selectPage(page,
                    new EntityWrapper<CashTransactionClass>()
                            .eq("deleted",false)
                            .eq("enabled",true)
                            .eq("set_of_book_id",forArtemisCO.getSetOfBookId())
                            .eq("type_code","PREPAYMENT")
                            .notIn("id",forArtemisCO.getTransactionClassIdList())
                            .like(forArtemisCO.getClassCode() != null,"class_code",forArtemisCO.getClassCode())
                            .like(forArtemisCO.getDescription() != null,"description",forArtemisCO.getDescription())
                            .orderBy("class_code")
            );
        }else if (forArtemisCO.getRange().equals("all")){
            List<CashTransactionClass> list = new ArrayList<>();
            if ( !CollectionUtils.isEmpty(forArtemisCO.getTransactionClassIdList()) ){
                List<CashTransactionClass> list1 = this.selectList(
                        new EntityWrapper<CashTransactionClass>()
                                .eq("deleted", false)
                                .eq("enabled", true)
                                .eq("set_of_book_id", forArtemisCO.getSetOfBookId())
                                .eq("type_code", "PREPAYMENT")
                                .in("id", forArtemisCO.getTransactionClassIdList())
                                .like(forArtemisCO.getClassCode() != null, "class_code", forArtemisCO.getClassCode())
                                .like(forArtemisCO.getDescription() != null, "description", forArtemisCO.getDescription())
                                .orderBy("class_code")
                );
                list1.stream().forEach(cashTransactionClass -> {
                    cashTransactionClass.setAssigned(true);
                    list.add(cashTransactionClass);
                });
            }
            List<CashTransactionClass> list2 = this.selectList(
                    new EntityWrapper<CashTransactionClass>()
                            .eq("deleted",false)
                            .eq("enabled",true)
                            .eq("set_of_book_id",forArtemisCO.getSetOfBookId())
                            .eq("type_code","PREPAYMENT")
                            .notIn("id",forArtemisCO.getTransactionClassIdList())
                            .like(forArtemisCO.getClassCode() != null,"class_code",forArtemisCO.getClassCode())
                            .like(forArtemisCO.getDescription() != null,"description",forArtemisCO.getDescription())
                            .orderBy("class_code")
            );
            list2.stream().forEach(cashTransactionClass -> {
                cashTransactionClass.setAssigned(false);
                list.add(cashTransactionClass);
            });
            page.setTotal(list.size());
            page.setRecords(list);
            return page;
        }
        return page;
    }

    /**
     * 根据id或者限定条件查询现金事务分类
     *
     * @param selectId
     * @param setOfBooksId
     * @param code
     * @param name
     * @param page
     * @return
     */
    public Page<BasicCO> listCashTransactionClassByIdOrCond(Long selectId, Long setOfBooksId, String code, String name, Page page){
        List<BasicCO> list = new ArrayList<>();

        if (selectId != null){
            CashTransactionClass cashTransactionClass = baseMapper.selectById(selectId);
            if (cashTransactionClass != null){
                BasicCO basicCO =BasicCO
                        .builder()
                        .id(cashTransactionClass.getId())
                        .name(cashTransactionClass.getDescription())
                        .code(cashTransactionClass.getClassCode())
                        .build();
                list.add(basicCO);
            }
        }else {
            List<CashTransactionClass> transactionClassList = baseMapper.selectPage(page,
                    new EntityWrapper<CashTransactionClass>()
                            .eq("deleted",false)
                            .eq("enabled",true)
                            .eq(setOfBooksId != null,"set_of_book_id",setOfBooksId)
                            .like(code != null,"class_code",code)
                            .like(name != null,"description",name)
                            .orderBy("class_code")
            );
            if (transactionClassList.size() > 0){
                transactionClassList.stream().forEach(cashTransactionClass -> {
                    BasicCO basicCO =BasicCO
                            .builder()
                            .id(cashTransactionClass.getId())
                            .name(cashTransactionClass.getDescription())
                            .code(cashTransactionClass.getClassCode())
                            .build();
                    list.add(basicCO);
                });
            }
        }
        page.setRecords(list);
        return page;
    }

    /**
     * 给对公报账单提供，根据账套id，查询该账套下启用的、付款类型的现金事务分类集合
     *
     * @param setOfBooksId
     * @return
     */
    public List<CashTransactionClass> listCashTransactionClassBySetOfBooksId(Long setOfBooksId){
        List<CashTransactionClass> list = new ArrayList<>();
        list = baseMapper.selectList(
                new EntityWrapper<CashTransactionClass>()
                        .eq("set_of_book_id",setOfBooksId)
                        .eq("enabled",true)
                        .eq("deleted",false)
                        .eq("type_code","PAYMENT")
                        .orderBy("class_code")
        );
        return list;
    }

    /*@LcnTransaction*/
    @Transactional(rollbackFor = Exception.class)
    public void test(CashTransactionClass cashTransactionClass) {
        this.insert(cashTransactionClass);
    }
}

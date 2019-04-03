package com.hand.hcf.app.mdata.accounts.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.mdata.accounts.domain.Accounts;
import com.hand.hcf.app.mdata.accounts.domain.AccountsHierarchy;
import com.hand.hcf.app.mdata.accounts.dto.AccountsHierarchyDTO;
import com.hand.hcf.app.mdata.accounts.persistence.AccountsHierarchyMapper;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.mdata.utils.StringUtil;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseI18nService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class AccountsHierarchyService extends ServiceImpl<AccountsHierarchyMapper, AccountsHierarchy> {

    private final Logger log = LoggerFactory.getLogger(AccountsHierarchyService.class);

    @Autowired
    private AccountsHierarchyMapper accountsHierarchyMapper;
    @Autowired
    private AccountsHierarchyService accountsHierarchyService;
    @Autowired
    private BaseI18nService baseI18nService;
    @Autowired
    private HcfOrganizationInterface organizationInterface;

    /**
     * 新建子科目
     *
     * @param accountsHierarchy
     * @return AccountsHierarchy
     */
    public AccountsHierarchy insertAccountsHierarchy(AccountsHierarchy accountsHierarchy) {
        //  参数是否为空校验
        if (accountsHierarchy.getId() != null){
            throw new BizException(RespCode.ACCOUNTSHIERARCHY_ID_NOT_NULL);
        }
        if (accountsHierarchy.getParentAccountId() == null){
            throw new BizException(RespCode.ACCOUNTSHIERARCHY_PARENT_ID_NULL);
        }
        if (accountsHierarchy.getSubAccountId() == null){
            throw new BizException(RespCode.ACCOUNTSHIERARCHY_SUB_ID_NULL);
        }
        if (accountsHierarchy.getTenantId() == null){
            throw new BizException(RespCode.ACCOUNTSHIERARCHY_TENANT_ID_NULL);
        }
        //  查询判断是否重复
        AccountsHierarchy selectResult = accountsHierarchyService.selectOne(new EntityWrapper<AccountsHierarchy>()
            .where("deleted = false")
            .eq("parent_account_id",accountsHierarchy.getParentAccountId())
            .eq("sub_account_id",accountsHierarchy.getSubAccountId())
        );
        //  查询结果不为空则有重复
        if (selectResult != null){
            throw new BizException(RespCode.ACCOUNTSHIERARCHY_HAS_SAME_SUB_ACCOUNT);
        }
        //  插入校验后的数据
        accountsHierarchyMapper.insert(accountsHierarchy);
        return accountsHierarchy;
    }

    /**
     * 批量-新建子科目
     *
     * @param list
     * @return AccountsHierarchy
     */
    public List<AccountsHierarchy> insertAccountsHierarchyBatch(List<AccountsHierarchy> list) {
        for (AccountsHierarchy accountsHierarchy : list){
            this.insertAccountsHierarchy(accountsHierarchy);
        }
        return list;
    }

    /**
     * 根据子科目表主键ID删除子科目
     *
     * @param id 子科目表主键ID
     * @return
     */
    public Boolean deleteAccountsHierarchy(Long id) {
        AccountsHierarchy result = accountsHierarchyMapper.selectById(id);
        if(null != result && result.getDeleted() != true){ //  删除成功
            result.setDeleted(true);
            accountsHierarchyMapper.updateById(result);
        }else { //  删除失败
            throw new BizException(RespCode.ACCOUNTSHIERARCHY_NOT_EXISTS);
        }
        // 返回成功标志
        return true;
    }

    /**
     * 批量-删除子科目
     *
     * @param list
     * @return AccountsHierarchy
     */
    public Boolean deleteAccountsHierarchyBatch(List<Long> list) {
        for (Long id : list){
            this.deleteAccountsHierarchy(id);
        }
        return true;
    }

    /**
     * 分页查询 某汇总科目的子科目条件查询
     * @param parentAccountId 父科目ID
     * @param info 科目代码或科目名称
     * @param page 分页对象
     * @return
     */
    public Page<AccountsHierarchyDTO> findParentAccountsHierarchyDTO(Long parentAccountId,
                                                                     String info,
                                                                     Page<AccountsHierarchyDTO> page) {
        List<AccountsHierarchyDTO> list = new ArrayList<>();
        if (StringUtil.isNullOrEmpty(info)){
            list = accountsHierarchyMapper.findParentAccountsHierarchyDTO(parentAccountId,null,null,page);
        }else {
            //  按code查询
            List<AccountsHierarchyDTO> codes = accountsHierarchyMapper.findParentAccountsHierarchyDTO(parentAccountId,info,null,page);
            //  按name查询
            List<AccountsHierarchyDTO> names = accountsHierarchyMapper.findParentAccountsHierarchyDTO(parentAccountId,null,info,page);
            //  根据两个查询结果处理集合
            if (CollectionUtils.isNotEmpty(codes) && !CollectionUtils.isNotEmpty(names)){
                list = codes;
            }else if (!CollectionUtils.isNotEmpty(codes) && CollectionUtils.isNotEmpty(names)) {
                list = names;
            }else if (CollectionUtils.isNotEmpty(codes) && CollectionUtils.isNotEmpty(names)) {
                //  中间变量
                List<AccountsHierarchyDTO> distinct = new ArrayList<>();
                //  codes值赋值给中间变量
                distinct.addAll(codes);
                //  此时distinct中是A集合中去除A与B交集
                distinct.removeAll(names);
                //  此时codes中是A和B交集
                codes.removeAll(distinct);
                //  此时names中是B集合中去除A与B交集
                names.removeAll(distinct);
                //  将三个结果拼接
                list.addAll(distinct);
                list.addAll(codes);
                list.addAll(names);
                //  排序
                Collections.sort(list, new Comparator<AccountsHierarchyDTO>() {
                    @Override
                    public int compare(AccountsHierarchyDTO o1, AccountsHierarchyDTO o2) {
                        return o1.getAccountCode().compareTo(o2.getAccountCode());
                    }
                });
            }else {
                list = null;
            }
        }
        //  判断是否为空
        if(CollectionUtils.isNotEmpty(list)){
            list.stream().forEach((AccountsHierarchyDTO dto) -> {
                 /* List<SysCodeValue> eList = customEnumerationService.findByCustomEnumerationIdAndIsEnabledTrueAndValueIn(OrgInformationUtil.getCurrentCompanyId(),2205, Arrays.asList(dto.getAccountType()));
                if(eList != null && eList.size() > 0){// 解决空指针问题
                    dto.setAccountTypeName(eList.get(0).getName());
                }*/
                SysCodeValueCO item = organizationInterface.getValueBySysCodeAndValue( "2205", dto.getAccountType());
                if(item != null){// 解决空指针问题
                    dto.setAccountTypeName(item.getName());
                }
                dto.setAccountName(baseI18nService.selectOneTranslatedTableInfoWithI18n(dto.getSubAccountId(), Accounts.class).getAccountName());
            });
            page.setRecords(list);
        }
        return page;
    }

    /**
     * 分页查询 子科目表条件查询
     * @param parentAccountId 父科目ID
     * @param accountCode 科目代码
     * @param accountName 科目名称
     * @param page 分页对象
     * @return
     */
    public Page<AccountsHierarchyDTO> findChildAccountsHierarchyDTO(Long accountSetId,
                                                                    Long parentAccountId,
                                                                    String accountCode,
                                                                    String accountName,
                                                                    String codeFrom,
                                                                    String codeTo,
                                                                    Page<AccountsHierarchyDTO> page) {
        List<AccountsHierarchyDTO> list = accountsHierarchyMapper.findChildAccountsHierarchyDTO(accountSetId,parentAccountId,accountCode,accountName,codeFrom,codeTo,page);
        //  判断是否为空
        if(CollectionUtils.isNotEmpty(list)){
            list.stream().forEach((AccountsHierarchyDTO dto) -> {
                /*  List<SysCodeValue> eList = customEnumerationService.findByCustomEnumerationIdAndIsEnabledTrueAndValueIn(OrgInformationUtil.getCurrentCompanyId(),2205, Arrays.asList(dto.getAccountType()));
                * 切换值列表访问方式
                * */
                SysCodeValueCO item = organizationInterface.getValueBySysCodeAndValue("2205", dto.getAccountType());
                if(item != null){// 解决空指针问题
                    dto.setAccountTypeName(item.getName());
                }
                dto.setAccountName(baseI18nService.selectOneTranslatedTableInfoWithI18n(dto.getSubAccountId(), Accounts.class).getAccountName());
            });
            page.setRecords(list);
        }
        return page;
    }
}

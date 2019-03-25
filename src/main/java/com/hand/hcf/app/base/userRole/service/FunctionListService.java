package com.hand.hcf.app.base.userRole.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.userRole.domain.FunctionList;
import com.hand.hcf.app.base.userRole.persistence.FunctionListMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseI18nService;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/29
 */
@Service
@AllArgsConstructor
@Transactional
public class FunctionListService extends BaseService<FunctionListMapper,FunctionList>{
    private final FunctionListMapper functionListMapper;

    private final BaseI18nService baseI18nService;

    /**
     * 新增 功能
     * @param functionList
     * @return
     */
    public FunctionList createFunctionList(FunctionList functionList){
        if (functionList.getId() != null){
            throw new BizException(RespCode.FUNCTION_LIST_EXIST);
        }
        if (functionList.getFunctionName() == null){
            throw new BizException(RespCode.FUNCTION_LIST_FUNCTION_NAME_IS_NULL);
        }
        if (functionList.getFunctionRouter() == null){
            throw new BizException(RespCode.FUNCTION_LIST_FUNCTION_ROUTER_IS_NULL);
        }
        if (functionList.getFunctionUrl() == null){
            throw new BizException(RespCode.FUNCTION_LIST_FUNCTION_URL_IS_NULL);
        }
        if (functionList.getSequenceNumber() == null){
            throw new BizException(RespCode.FUNCTION_LIST_SEQUENCE_NUMBER_IS_NULL);
        }
        if (functionListMapper.selectList(
                new EntityWrapper<FunctionList>()
                        .eq("function_name",functionList.getFunctionName())
                        .eq("function_router",functionList.getFunctionRouter())
        ).size() > 0 ){
            throw new BizException(RespCode.FUNCTION_LIST_FUNCTION_ROUTER_REPEAT);
        }
        functionListMapper.insert(functionList);
        return functionListMapper.selectById(functionList);
    }

    /**
     * 逻辑删除 功能
     * @param id
     */
    public void deleteFunctionListById(Long id){
        FunctionList functionList = functionListMapper.selectById(id);
        if (functionList == null){
            throw new BizException(RespCode.FUNCTION_LIST_NOT_EXIST);
        }
        functionList.setDeleted(true);
        functionListMapper.updateById(functionList);
    }

    /**
     * 修改 功能
     * @param functionList
     * @return
     */
    public FunctionList updateFunctionList(FunctionList functionList){
        if (functionList.getId() == null){
            throw new BizException(RespCode.FUNCTION_LIST_NOT_EXIST);
        }
        functionListMapper.updateById(functionList);
        return functionListMapper.selectById(functionList);
    }

    /**
     * 根据id查询 功能
     * @param id
     * @return
     */
    public FunctionList getFunctionListById(Long id){
        FunctionList functionList = functionListMapper.selectById(id);
        if (functionList == null){
            throw new BizException(RespCode.FUNCTION_LIST_NOT_EXIST);
        }
        return baseI18nService.selectOneTranslatedTableInfoWithI18nByEntity(functionList,FunctionList.class);
    }

    /**
     * 条件分页查询 功能
     * @param functionName
     * @param functionRouter
     * @param page
     * @return
     */
    public Page<FunctionList> getFunctionListByCond(String functionName, String functionRouter, Page page){
        Page<FunctionList> result = this.selectPage(page,
                new EntityWrapper<FunctionList>()
                        .eq("deleted",false)
                        .like(functionName != null,"function_name",functionName)
                        .like(functionRouter != null,"function_router",functionRouter)
                        .orderBy("sequence_number",true)
                        .orderBy("last_updated_date",false)
        );
        if (result.getRecords().size() > 0) {
            result.setRecords(baseI18nService.selectListTranslatedTableInfoWithI18nByEntity(result.getRecords(),FunctionList.class));
        }
        return result;
    }
}

package com.hand.hcf.app.mdata.implement.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.SetOfBooksInfoCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.core.util.PageUtil;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SobControllerImpl {

    @Autowired
    private SetOfBooksService setOfBooksService;

    @Autowired
    private MapperFacade mapperFacade;
    /**
     * 根据账套id查询
     * @param id 账套id
     * @return
     */
    public SetOfBooksInfoCO getSetOfBooksById(@PathVariable("id") Long id) {
        return mapperFacade.map(setOfBooksService.findSetOfBooksById(id),SetOfBooksInfoCO.class);
    }

    /**
     * (没用到)
     * 根据条件分页查询账套
     * @param setOfBooksCode 账套code
     * @param setOfBooksName 账套name
     * @param keyWord 关键字(账套code或name)
     * @param excludeIds 排除的账套的id集合
     * @param page 页码
     * @param size 每页数量
     * @return
     */
    public Page<SetOfBooksInfoCO> pageSetOfBooksListByCondAndNotExcludeIds(@RequestParam(value = "setOfBooksCode",required = false) String setOfBooksCode,
                                                                           @RequestParam(value = "setOfBooksName",required = false) String setOfBooksName,
                                                                           @RequestParam(value = "keyWord",required = false) String keyWord,
                                                                           @RequestBody(required = false) List<Long> excludeIds,
                                                                           @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                           @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page mybatisPage = PageUtil.getPage(page, size);
        return setOfBooksService.pageSetOfBooksListByTenantIdAndCond(OrgInformationUtil.getCurrentTenantId(), setOfBooksCode, setOfBooksName, keyWord, excludeIds, mybatisPage);
    }


    /**
     * (没用到)
     * 根据账套id，关键字查询账套信息
     * @param ids 账套id集合
     * @param keyWord 关键字（账套name或账套code）
     * @return
     */
    public List<SetOfBooksInfoCO> listSetOfBooksListByIds(@RequestBody List<Long> ids,
                                                          @RequestParam(value = "keyWord",required = false) String keyWord) {
        return mapperFacade.mapAsList(setOfBooksService.getSetOfBooksListByIds(ids,keyWord,null),SetOfBooksInfoCO.class);
    }

    /**
     * (没用到)
     * 根据账套id，关键字，分页查询账套信息
     * @param ids
     * @param keyWord
     * @param page
     * @param size
     * @return
     */
    public Page<SetOfBooksInfoCO> pageSetOfBooksListByIdsResultPage(@RequestBody List<Long> ids,
                                                                    @RequestParam(value = "keyWord",required = false) String keyWord,
                                                                    @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                    @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page mybatisPage = PageUtil.getPage(page, size);
        List<SetOfBooksInfoCO> setOfBooksList = mapperFacade.mapAsList(setOfBooksService.getSetOfBooksListByIds(ids, keyWord, mybatisPage),SetOfBooksInfoCO.class);
        mybatisPage.setRecords(setOfBooksList);
        return mybatisPage;
    }
}

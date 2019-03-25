package com.hand.hcf.app.base.lov.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.lov.domain.Lov;
import com.hand.hcf.app.base.lov.persistence.LovMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by weishan on 2019/3/5.
 * lov Service
 */
@Service
public class LovService extends BaseService<LovMapper, Lov> {


    /**
     * 创建LOV
     *
     * @param lov
     * @return
     */
    @Transactional
    public Lov createLov(Lov lov) {
        //校验
        if (lov == null || lov.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        validateEntity(lov);

        insert(lov);
        return lov;
    }

    /**
     * 更新LOV
     *
     * @param lov
     * @return
     */
    @Transactional
    public Lov updateLov(Lov lov) {
        //校验
        if (lov == null || lov.getId() == null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }

        validateEntity(lov);

        updateById(lov);
        return lov;
    }


    public void validateEntity(Lov lov) {

        if (StringUtils.isEmpty(lov.getLovCode())) {
            throw new BizException(RespCode.LOV_CODE_NOT_BE_NULL);
        }

        if (selectCount(new EntityWrapper<Lov>()
                .eq("lov_code", lov.getLovCode())
                .ne(lov.getId() != null, "id", lov.getId())) > 0) {
            throw new BizException(RespCode.LOV_CODE_EXISTS);
        }


        if (StringUtils.isEmpty(lov.getLovName())) {
            throw new BizException(RespCode.LOV_NAME_NOT_BE_NULL);
        }

        if (selectCount(new EntityWrapper<Lov>()
                .eq("lov_name", lov.getLovName())
                .ne(lov.getId() != null, "id", lov.getId())) > 0) {
            throw new BizException(RespCode.LOV_NAME_EXISTS);
        }
    }

    /**
     * @param id 删除（逻辑删除）
     * @return
     */
    @Transactional
    public void delete(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }


    public List<Lov> pageAll(Page page, String lovCode, String lovName,Long appId) {
        return baseMapper.pageAll(lovCode,lovName,appId,page)
                ;
    }

    public List<Lov> listAll(String lovCode, String lovName) {
        return selectList(new EntityWrapper<Lov>()
                .like(StringUtils.isEmpty(lovCode), "lov_code", lovCode)
                .like(StringUtils.isEmpty(lovName), "lov_name", lovName)
                .orderBy("lov_code"))
                ;
    }

}

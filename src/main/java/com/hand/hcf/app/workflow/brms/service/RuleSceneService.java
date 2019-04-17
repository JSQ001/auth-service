package com.hand.hcf.app.workflow.brms.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.workflow.brms.domain.RuleScene;
import com.hand.hcf.app.workflow.brms.dto.RuleSceneDTO;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.brms.persistence.RuleSceneMapper;
import com.hand.hcf.app.workflow.brms.util.cache.CacheNames;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@Transactional
@CacheConfig(cacheNames = CacheNames.BRMS_ENTITY_RULE_SCENE)
public class RuleSceneService extends BaseService<RuleSceneMapper,RuleScene> {

@Autowired
private MapperFacade mapper;

    @Transactional(readOnly = true)
    public Page<RuleScene> listAllWithInvalid(Page page) {
        return selectPage(page);
    }

    @Transactional(readOnly = true)
    public Page<RuleScene> listAll(Page page) {
        return selectPage(page,new EntityWrapper<RuleScene>()
                .eq("status",RuleApprovalEnum.VALID.getId()));

    }


    @Transactional(readOnly = true)
    public RuleScene getByOid(UUID ruleSceneOid){
        return selectOne(new EntityWrapper<RuleScene>()
                .eq("rule_scene_oid",ruleSceneOid)
        );
    }

    /**
     * get one RuleScene by id.
     *
     * @return the entity
     */
    @Transactional(readOnly = true)
    //@Cacheable(key = "#ruleSceneOid.toString()")
    public RuleSceneDTO getRuleScene(UUID ruleSceneOid) {
        RuleScene opt = getByOid(ruleSceneOid);
        if (opt!=null) {
            return mapper.map(opt, RuleSceneDTO.class);
        }
        return null;
    }

    @CacheEvict(key = "#ruleSceneOid.toString()")
    public int delete(UUID ruleSceneOid) {
        RuleScene opt =getByOid(ruleSceneOid);
        if (opt!=null && opt.getStatus().equals(RuleApprovalEnum.VALID.getId())) {
            RuleScene ruleScene = opt;
            ruleScene.setStatus(RuleApprovalEnum.INVALID.getId());
            ruleScene.setCreatedDate(ZonedDateTime.now());
            insertOrUpdate(ruleScene);
            return 1;
        }
        return 0;
    }

    public RuleScene save(RuleScene ruleScene) {
        ruleScene.setRuleSceneOid(UUID.randomUUID());
        ruleScene.setStatus(RuleApprovalEnum.VALID.getId());
        ruleScene.setCreatedDate(ZonedDateTime.now());
       insertOrUpdate(ruleScene);
       return ruleScene;
    }

    @CacheEvict(key = "#ruleSceneDTO.ruleSceneOid.toString()")
    public RuleScene update(RuleSceneDTO ruleSceneDTO) {
        RuleScene opt = getByOid(ruleSceneDTO.getRuleSceneOid());
        if (opt!=null && opt.getStatus().equals(RuleApprovalEnum.VALID.getId())) {
            RuleScene ruleScene = mapper.map(ruleSceneDTO, RuleScene.class);
            ruleScene.setId(opt.getId());
            ruleScene.setStatus(opt.getStatus());
            ruleScene.setCreatedDate(ZonedDateTime.now());
            insertOrUpdate(ruleScene);
            return ruleScene;
        }
        return null;
    }
}

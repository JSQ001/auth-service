package com.hand.hcf.app.base.system.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.constant.Constants;
import com.hand.hcf.app.base.system.domain.Application;
import com.hand.hcf.app.base.system.persistence.ApplicationMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by weishan on 2019/3/5.
 * 应用Service
 */
@Service
public class ApplicationService extends BaseService<ApplicationMapper, Application> {


    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 创建应用
     *
     * @param application
     * @return
     */
    @Transactional
    public Application createApplication(Application application) {
        //校验
        if (application == null || application.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        validateEntity(application);

        insert(application);
        return application;
    }

    /**
     * 更新应用
     *
     * @param application
     * @return
     */
    @Transactional
    public Application updateApplication(Application application) {
        //校验
        if (application == null || application.getId() == null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        validateEntity(application);

        updateById(application);
        return application;
    }


    public void validateEntity(Application application) {

        if (StringUtils.isEmpty(application.getAppCode())) {
            throw new BizException(RespCode.APPLICATION_CODE_NOT_BE_NULL);
        }

        if (selectCount(new EntityWrapper<Application>()
                .eq("app_code", application.getAppCode())
                .ne(application.getId() != null, "id", application.getId())) > 0) {
            throw new BizException(RespCode.APPLICATION_CODE_EXISTS);
        }

        if (StringUtils.isEmpty(application.getAppName())) {
            throw new BizException(RespCode.APPLICATION_NAME_NOT_BE_NULL);
        }

        if (selectCount(new EntityWrapper<Application>()
                .eq("app_name", application.getAppName())
                .ne(application.getId() != null, "id", application.getId())) > 0) {
            throw new BizException(RespCode.APPLICATION_NAME_EXISTS);
        }
    }

    public List<Application> pageApps(Page page, String appCode, String appName) {
        List<Application> apps = super.selectPage(page
                , new EntityWrapper<Application>()
                        .like(!StringUtils.isEmpty(appCode), "app_code", appCode)
                        .like(!StringUtils.isEmpty(appName), "app_name", appName)
                        .orderBy("app_code"))
                .getRecords();
        if (apps != null && apps.size() > 0) {
            List<String> services = discoveryClient.getServices();
            apps.stream().forEach(a -> {
                if (services.contains(a.getAppCode())) {
                    a.setStatus(Constants.SERVICE_UP);
                } else {
                    a.setStatus(Constants.SERVICE_DOWN);
                }
            });
        }
        return apps;
    }

    public List<Application> listAll(String appCode, String appName) {
        return selectList(new EntityWrapper<Application>()
                .like(StringUtils.isEmpty(appCode), "app_code", appCode)
                .like(StringUtils.isEmpty(appName), "app_name", appName)
                .orderBy("app_code"))
                ;
    }

    /**
     * @param id 删除应用（逻辑删除）
     * @return
     */
    @Transactional
    public void delete(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }


}

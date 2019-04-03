package com.hand.hcf.app.base.code.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.code.domain.SysCode;
import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.domain.SysCodeValueTemp;
import com.hand.hcf.app.base.code.dto.SysCodeValueDTO;
import com.hand.hcf.app.base.code.persistence.SysCodeMapper;
import com.hand.hcf.app.base.system.enums.SysCodeEnum;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.core.domain.ExportConfig;
import com.hand.hcf.core.domain.ExportConfigByList;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.handler.ExcelImportHandler;
import com.hand.hcf.core.service.BaseI18nService;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.service.ExcelExportService;
import com.hand.hcf.core.service.ExcelImportService;
import com.hand.hcf.core.util.LoginInformationUtil;
import com.hand.hcf.core.web.dto.ImportResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  值列表
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/25
 */
@Service
public class SysCodeService extends BaseService<SysCodeMapper, SysCode> {
    @Autowired
    private SysCodeValueService itemService;
    @Autowired
    private BaseI18nService baseI18nService;
    @Autowired
    private ExcelExportService excelExportService;
    @Autowired
    private SysCodeValueTempService sysCodeValueTempService;
    @Autowired
    private ExcelImportService excelImportService;

    private Wrapper<SysCode> initWrapper(){
        Wrapper<SysCode> wrapper = new EntityWrapper<SysCode>()
                .in("tenant_id", Arrays.asList(LoginInformationUtil.getCurrentTenantId(),-1L));
        return wrapper;
    }
    public List<SysCode> pageByCondition(Page page,
                                         String code,
                                         String name,
                                         Boolean enabled,
                                         SysCodeEnum typeFlag) {
        Wrapper<SysCode> wrapper = initWrapper()
                .like(StringUtils.hasText(code), "code", code)
                .like(StringUtils.hasText(name), "name", name)
                .eq(enabled != null, "enabled", enabled)
                .eq(typeFlag != null, "type_flag", typeFlag)
                .orderBy("enabled", false)
                .orderBy("type_flag", false)
                .orderBy("code", true);

        List<SysCode> sysCodes = baseMapper.selectPage(page, wrapper);

        return sysCodes;
    }

    public SysCode getByOid(String oid) {
        SysCode customEnumeration = this.selectOne(initWrapper().eq("code_oid", oid));
        Map<String, List<Map<String, String>>> i18nMap = baseI18nService.getI18nMap(SysCode.class, customEnumeration.getId());
        customEnumeration.setI18n(i18nMap);
        return customEnumeration;
    }

    public SysCode getById(Long id) {
        SysCode sysCode = baseI18nService.selectOneTranslatedTableInfoWithI18n(id, SysCode.class);
        return sysCode;
    }

    public List<SysCodeValue> pageSysCodeValueByCodeId(Page<SysCodeValue> page, Long id, String keyword) {
        SysCode sysCode = this.selectById(id);
        if (sysCode == null){
            return new ArrayList<>();
        }
        Wrapper<SysCodeValue> waWrapper = new EntityWrapper<SysCodeValue>()
                .eq("code_id", sysCode.getId());
        if (StringUtils.hasText(keyword)){
            waWrapper.andNew()
                    .like(StringUtils.hasText(keyword), "name", keyword)
                    .or()
                    .like(StringUtils.hasText(keyword), "value", keyword)
                    .or()
                    .like(StringUtils.hasText(keyword), "remark", keyword)
                    .orderBy("enabled", false)
                    .orderBy("value");
        }else{
            waWrapper.orderBy("enabled", false)
                    .orderBy("value");
        }
        Page<SysCodeValue> result = itemService.selectPage(page, waWrapper);

        return result.getRecords();
    }

    @Transactional(rollbackFor = Exception.class)
    public SysCode updateSysCode(SysCode sysCode, String systemFlag) {
        if (sysCode.getId() == null){
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        SysCode queryCode = this.selectById(sysCode.getId());
        if ("system".equalsIgnoreCase(systemFlag)){
            queryCode.setTypeFlag(sysCode.getTypeFlag());
            if (SysCodeEnum.SYSTEM.equals(sysCode.getTypeFlag())){
                queryCode.setTenantId(-1L);
            }else{
                queryCode.setTenantId(LoginInformationUtil.getCurrentTenantId());
            }
        }else{
            if (!SysCodeEnum.CUSTOM.equals(sysCode.getTypeFlag())){
                throw new BizException(RespCode.SYS_CODE_TYPE_NOT_ALLOW_UPDATE);
            }
        }
        queryCode.setName(queryCode.getName());
        if (!CollectionUtils.isEmpty(sysCode.getI18n())){
            queryCode.setI18n(sysCode.getI18n());
        }

        queryCode.setVersionNumber(sysCode.getVersionNumber());
        queryCode.setEnabled(sysCode.getEnabled());
        if (Boolean.FALSE.equals(sysCode.getEnabled())){
            // 如果为禁用状态，则将其下的值全部置为禁用状态
            SysCodeValue sysCodeValue = new SysCodeValue();
            sysCodeValue.setEnabled(Boolean.FALSE);
            itemService.update(sysCodeValue, new EntityWrapper<SysCodeValue>().eq("code_id", sysCode.getId()));
        }
        this.updateById(queryCode);
        return queryCode;
    }

    @Transactional(rollbackFor = Exception.class)
    public SysCode createSysCode(SysCode sysCode, String systemFlag) {
        if (!StringUtils.hasText(sysCode.getCode())){
            throw new BizException(RespCode.SYS_CODE_CODE_IS_NULL);
        }
        SysCode selectOne;
        if ("system".equalsIgnoreCase(systemFlag)){
            // 如果是系统管理员, 得看这个代码是不是存在所以的租户
            selectOne = this.selectOne(this.getWrapper().eq("code", sysCode.getCode()));
            if (SysCodeEnum.SYSTEM.equals(sysCode.getTypeFlag())){
                sysCode.setTenantId(-1L);
            }else{
                sysCode.setTenantId(LoginInformationUtil.getCurrentTenantId());
            }
        }else {
            // 判断当前代码是否在系统级里面有没有
            selectOne = this.selectOne(initWrapper().eq("code", sysCode.getCode()));
            sysCode.setTypeFlag(SysCodeEnum.CUSTOM);
            sysCode.setTenantId(LoginInformationUtil.getCurrentTenantId());
        }
        if (null != selectOne){
            throw new BizException(RespCode.SYS_CODE_CODE_IS_EXISTS);
        }
        sysCode.setId(null);
        sysCode.setCodeOid(UUID.randomUUID().toString());
        try {
            this.insert(sysCode);
        }catch (DuplicateKeyException e){
            throw new BizException(RespCode.SYS_CODE_CODE_IS_EXISTS);
        }
        return sysCode;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateSysCodeValue(SysCodeValue sysCodeValue) {
        if (sysCodeValue.getId() == null){
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        SysCode sysCode = this.selectById(sysCodeValue.getCodeId());
        if (sysCode == null){
            throw new BizException(RespCode.SYS_CODE_NOT_EXISTS);
        }
        if (!Boolean.TRUE.equals(sysCode.getEnabled())){
            sysCodeValue.setEnabled(Boolean.FALSE);
        }
        return itemService.updateById(sysCodeValue);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createSysCodeValue(SysCodeValue sysCodeValue) {
        SysCode sysCode = this.selectById(sysCodeValue.getCodeId());
        if (sysCode == null){
            throw new BizException(RespCode.SYS_CODE_NOT_EXISTS);
        }
        if (!Boolean.TRUE.equals(sysCode.getEnabled())){
            sysCodeValue.setEnabled(Boolean.FALSE);
        }
        sysCodeValue.setId(null);
        sysCodeValue.setCodeId(sysCode.getId());
        try {
            itemService.insert(sysCodeValue);
        }catch (DuplicateKeyException e){
            throw new BizException(RespCode.SYS_CODE_VALE_CODE_IS_EXISTS);
        }
        return true;
    }

    public List<SysCodeValue> listAllSysCodeValueBySysCode(String code) {
        SysCode sysCode = getByCode(code);
        if (sysCode == null){
            return new ArrayList<>();
        }
        return itemService.selectList(new EntityWrapper<SysCodeValue>()
                .eq("code_id", sysCode.getId())
                .orderBy("value"));
    }

    public List<SysCodeValue> listEnabledSysCodeValueBySysCodeAnd(String code) {
        SysCode sysCode = getByCode(code);
        if (sysCode == null){
            return new ArrayList<>();
        }
        return itemService.selectList(new EntityWrapper<SysCodeValue>()
                .eq("code_id", sysCode.getId())
                .eq("enabled", true)
                .orderBy("value"));
    }

    public SysCodeValue getValueBySysCodeAndValue(String code, String value) {
        SysCode sysCode = getByCode(code);
        if (sysCode == null){
            return null;
        }
        SysCodeValue sysCodeValue = itemService.selectOne(new EntityWrapper<SysCodeValue>()
                .eq("code_id", sysCode.getId())
                .eq("value", value));
        return sysCodeValue;
    }


    public void exportItem(Long id,
                           ExportConfig exportConfig,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        ExportConfigByList exportConfigByList = new ExportConfigByList(exportConfig);
        SysCode sysCode = this.selectById(id);

        exportConfigByList.setClazz(SysCodeValueDTO.class);
        if (sysCode == null){
            exportConfigByList.setListDTO(new ArrayList<>());
        }else{
            Wrapper<SysCodeValue> waWrapper = new EntityWrapper<SysCodeValue>()
                    .eq("code_id", sysCode.getId())
                    .orderBy("enabled", false)
                    .orderBy("value");
            List<SysCodeValue> result = itemService.selectList(waWrapper);
            List<SysCodeValueDTO> exportList = result.stream().map(e -> {
                SysCodeValueDTO dto = new SysCodeValueDTO();
                dto.setCode(e.getValue());
                dto.setRemark(e.getRemark());
                dto.setEnabledStr(e.getEnabled() ? "是" : "否");
                dto.setName(e.getName());
                return dto;
            }).collect(Collectors.toList());

            exportConfigByList.setListDTO(exportList);
        }
        excelExportService.exportAndDownloadExcel(exportConfigByList, request, response);
    }

    @Transactional(rollbackFor = Exception.class)
    public UUID importSysCodeValue(InputStream in, Long id) throws Exception {
        SysCode sysCode = this.selectById(id);
        if (sysCode == null){
            throw new BizException(RespCode.SYS_DATASOURCE_CANNOT_FIND_OBJECT);
        }
        UUID batchNumber = UUID.randomUUID();
        ExcelImportHandler<SysCodeValueTemp> excelImportHandler = new ExcelImportHandler<SysCodeValueTemp>() {
            @Override
            public void clearHistoryData() {
                sysCodeValueTempService.delete(new EntityWrapper<SysCodeValueTemp>()
                        .le("created_date", ZonedDateTime.now().plusDays(-7)));
            }
            @Override
            public String getRowNumberColumnName() {

                return "rowNumber";
            }
            @Override
            public Class<SysCodeValueTemp> getEntityClass() {
                return SysCodeValueTemp.class;
            }

            @Override
            public List<SysCodeValueTemp> persistence(List<SysCodeValueTemp> list) {
                list.stream().forEach(e -> {
                    e.setErrorDetail("");
                    e.setErrorFlag(false);
                    e.setBatchNumber(batchNumber.toString());
                    e.setCodeId(sysCode.getId());
                    if (StringUtils.hasText(e.getEnabledStr())) {
                        if (!"Y".equals(e.getEnabledStr()) && !"N".equals(e.getEnabledStr())) {
                            e.setErrorDetail(e.getErrorDetail() + "是否启用只能输入 Y 或者 N !");
                            e.setErrorFlag(true);
                        }
                    }else{
                        e.setErrorDetail(e.getErrorDetail() + "是否启用字段必须输入！");
                        e.setEnabledStr(null);
                        e.setErrorFlag(true);
                    }
                    if (!StringUtils.hasText(e.getName())){
                        e.setErrorDetail(e.getErrorDetail() + "值名称必须输入！");
                        e.setErrorFlag(true);
                    }
                    if (!StringUtils.hasText(e.getValue())){
                        e.setErrorDetail(e.getErrorDetail() + "值编码必须输入！");
                        e.setErrorFlag(true);
                    }
                });
                // 导入数据
                sysCodeValueTempService.insertBatch(list);
                return list;
            }
            @Override
            public void check(List<SysCodeValueTemp> list) {
            }
        };
        excelImportService.importExcel(in, false,2,excelImportHandler);
        sysCodeValueTempService.checkData(batchNumber, sysCode.getId());

        return batchNumber;
    }

    public ImportResultDTO queryImportResultInfo(String transactionOid) {
        return sysCodeValueTempService.queryImportResultInfo(transactionOid);
    }

    public List<SysCodeValue> pageSysCodeValueByCondition(Page<SysCodeValue> page,
                                                          String code,
                                                          String valueFrom,
                                                          String valueTo,
                                                          String value) {
        SysCode sysCode = getByCode(code);
        if (sysCode == null){
            return new ArrayList<>();
        }
        Page<SysCodeValue> customEnumerationItems = itemService.selectPage(page,new EntityWrapper<SysCodeValue>()
                .eq("code_id", sysCode.getId())
                .eq("enabled", true)
                .ge(StringUtils.hasText(valueFrom), "value", valueFrom)
                .le(StringUtils.hasText(valueTo), "value", valueTo)
                .like(StringUtils.hasText(value), "value", value)
                .orderBy("value"));
        return customEnumerationItems.getRecords();
    }

    public SysCode getByCode(String code){
        return this.selectOne(initWrapper()
                .eq("code", code));
    }

    public List<SysCodeValueCO> listSysValueByCodeConditionByEnabled(String code, Boolean enabled) {
        SysCode sysCode = getByCode(code);
        if (sysCode == null){
            return new ArrayList<>();
        }
        List<SysCodeValue> sysCodeValues = itemService.selectList(new EntityWrapper<SysCodeValue>()
                .eq("code_id", sysCode.getId())
                .eq(enabled != null, "enabled", enabled)
                .orderBy("value"));
        if (CollectionUtils.isEmpty(sysCodeValues)){
            return new ArrayList<>();
        }
        List<SysCodeValueCO> result = sysCodeValues.stream().map(sysCodeValue -> {
            SysCodeValueCO sysCodeValueCO = new SysCodeValueCO();
            sysCodeValueCO.setCodeId(sysCodeValue.getCodeId());
            sysCodeValueCO.setEnabled(sysCodeValue.getEnabled());
            sysCodeValueCO.setId(sysCodeValue.getId());
            sysCodeValueCO.setName(sysCodeValue.getName());
            sysCodeValueCO.setValue(sysCodeValue.getValue());
            return sysCodeValueCO;
        }).collect(Collectors.toList());
        return result;
    }

    public List<SysCodeValueCO> listSysValueByCodeOidConditionByEnabled(String codeOid, Boolean enabled) {
        SysCode sysCode = this.selectOne(initWrapper().eq("code_oid", codeOid));
        if (sysCode == null){
            return new ArrayList<>();
        }
        List<SysCodeValue> sysCodeValues = itemService.selectList(new EntityWrapper<SysCodeValue>()
                .eq("code_id", sysCode.getId())
                .eq(enabled != null, "enabled", enabled)
                .orderBy("value"));
        if (CollectionUtils.isEmpty(sysCodeValues)){
            return new ArrayList<>();
        }
        List<SysCodeValueCO> result = sysCodeValues.stream().map(sysCodeValue -> {
            SysCodeValueCO sysCodeValueCO = new SysCodeValueCO();
            sysCodeValueCO.setCodeId(sysCodeValue.getCodeId());
            sysCodeValueCO.setEnabled(sysCodeValue.getEnabled());
            sysCodeValueCO.setId(sysCodeValue.getId());
            sysCodeValueCO.setName(sysCodeValue.getName());
            sysCodeValueCO.setValue(sysCodeValue.getValue());
            return sysCodeValueCO;
        }).collect(Collectors.toList());
        return result;
    }

    public SysCodeValue getSysCodeValueByCodeOidAndValue(String codeOid, String value) {
        SysCode sysCode = this.selectOne(initWrapper().eq("code_oid", codeOid));
        SysCodeValue sysCodeValue = itemService.selectOne(new EntityWrapper<SysCodeValue>()
                .eq("code_id", sysCode.getId())
                .eq("value", value));
        return sysCodeValue;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean init() {
        List<Long> tenantIds = baseMapper.getNotExistsTenantId();
        List<Long> ids = this.selectList(new EntityWrapper<SysCode>().eq("tenant_id", 0L))
                .stream()
                .map(SysCode::getId)
                .collect(Collectors.toList());
        List<SysCode> sysCodes1 = baseI18nService.selectListTranslatedTableInfoWithI18n(ids, SysCode.class);
        tenantIds.forEach(tenantId ->{
            sysCodes1.forEach(e -> {
                Long codeId = e.getId();
                e.setTenantId(tenantId);
                e.setId(null);
                e.setCodeOid(UUID.randomUUID().toString());
                this.insert(e);
                itemService.initTenantBySysCode(e, codeId);
            });
        });
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateValueStatusByValueIds(List<Long> ids, Boolean enabled) {
        List<SysCodeValue> sysCodeValues = itemService.selectBatchIds(ids);
        if (!CollectionUtils.isEmpty(sysCodeValues)) {
            SysCodeValue sysCodeValue = sysCodeValues.get(0);
            SysCode sysCode = this.selectById(sysCodeValue.getCodeId());
            if (sysCode == null){
                throw new BizException(RespCode.SYS_CODE_NOT_EXISTS);
            }
            if (!Boolean.TRUE.equals(sysCode.getEnabled())){
                enabled = Boolean.FALSE;
            }
            itemService.updateItemStatusByIds(sysCodeValues, enabled);
        }
        return true;
    }
}

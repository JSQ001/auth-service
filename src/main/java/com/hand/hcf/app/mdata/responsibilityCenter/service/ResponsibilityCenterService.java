package com.hand.hcf.app.mdata.responsibilityCenter.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.common.co.ResponsibilityCenterCO;
import com.hand.hcf.app.common.enums.RangeEnum;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;

import com.hand.hcf.app.mdata.dimension.domain.enums.DimensionItemImportCode;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.GroupCenterRelationship;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.ResponsibilityAssignCompany;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.ResponsibilityCenter;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.enums.ResponsibilityCenterImportCode;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.temp.ResponsibilityCenterTemp;
import com.hand.hcf.app.mdata.responsibilityCenter.dto.ResponsibilityCenterExportDTO;
import com.hand.hcf.app.mdata.responsibilityCenter.dto.ResponsibilityLov;
import com.hand.hcf.app.mdata.responsibilityCenter.persistence.ResponsibilityCenterMapper;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.mdata.system.constant.Constants;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.mdata.utils.StringUtil;

import com.hand.hcf.core.domain.ExportConfig;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.handler.ExcelExportHandler;
import com.hand.hcf.core.handler.ExcelImportHandler;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.service.ExcelExportService;
import com.hand.hcf.core.service.ExcelImportService;
import com.hand.hcf.core.util.TypeConversionUtils;
import com.hand.hcf.core.web.dto.ImportResultDTO;
import com.itextpdf.text.io.StreamUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ResponsibilityCenterService extends BaseService<ResponsibilityCenterMapper, ResponsibilityCenter> {
    private final Logger log = LoggerFactory.getLogger(ResponsibilityCenterService.class);

    @Autowired
    private ResponsibilityCenterMapper responsibilityCenterMapper;

    @Autowired
    private GroupCenterRelationshipService groupCenterRelationshipService;

    @Autowired
    private ResponsibilityAssignCompanyService resAssginCompanyService;
    @Autowired
    private SetOfBooksService setOfBooksService;

    @Autowired
    private ExcelImportService excelImportService;

    @Autowired
    private ResponsibilityCenterTempService centerTempService;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private MapperFacade mapperFacade;


    /**
     * 新增责任中心
     * @param responsibilityCenter
     * @return
     */
    @Transactional
    public ResponsibilityCenter insertOrUpdateResponsibilityCenter(ResponsibilityCenter responsibilityCenter) {
        Long resCenterId = responsibilityCenter.getId();
        if(null == resCenterId ) {
            //责任中心代码账套下校验唯一性
            int count = this.selectCount(new EntityWrapper<ResponsibilityCenter>()
                    .eq("responsibility_center_code", responsibilityCenter.getResponsibilityCenterCode())
                    .eq("set_of_books_id", responsibilityCenter.getSetOfBooksId())
            );
            if (count != 0) {
                throw new BizException(RespCode.RESPONSIBILITY_CENTER_CODE_REPEAT);
            }
            responsibilityCenterMapper.insert(responsibilityCenter);
        }else{
            ResponsibilityCenter oldResCenter = responsibilityCenterMapper.selectById(resCenterId);
            if(oldResCenter == null) {
                throw new BizException(RespCode.RESPONSIBILITY_CENTER_NOT_EXIST);
            }
            responsibilityCenterMapper.updateById(responsibilityCenter);
        }
        return responsibilityCenter;
    }

    /**
     * 分页查询责任中心
     *
     * @param keyword 责任中心代码或者名称
     * @param codeFrom 责任中心代码从
     * @param codeTo 责任中心代码至
     * @param setOfBooksId 账套Id
     * @param responsibilityCenterCode 责任中心代码
     * @param responsibilityCenterName 责任中心名称
     * @param enabled 启用禁用
     * @param page 分页
     * @return
     */
    public Page<ResponsibilityCenter> pageResponsibilityCenterBySetOfBooksId(String keyword,
                                                                             String codeFrom,
                                                                             String codeTo,
                                                                             Long setOfBooksId,
                                                                             String responsibilityCenterCode,
                                                                             String responsibilityCenterName,
                                                                             Boolean enabled,
                                                                             Page page) {
        //根据套账查询
        List<ResponsibilityCenter> list = responsibilityCenterMapper
                .pageResponsibilityCenterBySetOfBooksId(keyword,
                        codeFrom,
                        codeTo,
                        setOfBooksId,
                        responsibilityCenterCode,
                        responsibilityCenterName,
                        enabled,
                        page);
        if (CollectionUtils.isNotEmpty(list)){
        page.setRecords(list);
        }
        return page;
    }


    /**
     * 根据责任中心组查询责任中心定义
     * @param groupId 责任中心组id
     * @param responsibilityCenterCode 责任中心代码
     * @param setOfBooksId 账套Id
     * @param responsibilityCenterName 责任中心名称
     * @param enabled 启用禁用
     * @param range  选择范围
     * @param page  分页
     * @return
     */
    public Page<ResponsibilityCenter> pageResponsibilityCenterBySetOfBooksIdAndGroupId(Long groupId,
                                                                                          String responsibilityCenterCode,
                                                                                          Long setOfBooksId,
                                                                                          String responsibilityCenterName,
                                                                                          Boolean enabled,
                                                                                          String range,
                                                                                          Page page) {
        Wrapper<ResponsibilityCenter> wapper = new EntityWrapper<ResponsibilityCenter>()
                .eq("set_of_books_id",setOfBooksId)
                .eq(enabled != null,"enabled",enabled)
                .orderBy("enabled",false)
                .orderBy("responsibility_center_code");
        if (StringUtils.isNotEmpty(responsibilityCenterCode)){
            wapper .like("responsibility_center_code",responsibilityCenterCode);
        }
        if(StringUtils.isNotEmpty(responsibilityCenterName)){
            wapper.like("responsibility_center_name",responsibilityCenterName);
        }
        List<Long> relIdList = groupCenterRelationshipService.selectList(
                new EntityWrapper<GroupCenterRelationship>()
                        .eq("group_id",groupId))
                .stream()
                .map(GroupCenterRelationship::getResponsibilityCenterId)
                .collect(Collectors.toList());
            //已选
            if(RangeEnum.SELECTED.name().equals(range)){
                if(CollectionUtils.isEmpty(relIdList)){
                    return page;
                }
                wapper.in("id",relIdList);
            }else if(RangeEnum.NOTCHOOSE.name().equals(range)){
                wapper.notIn("id",relIdList);
            }
        List<ResponsibilityCenter> list = responsibilityCenterMapper.selectPage(page,wapper);
        page.setRecords(list);
        return page;
    }

    /**
     * 获取当前责任中心已关联总数
     * @param groupId 责任中心组id
     * @return
     */
    public int getResponsibilityCenterCountByGroupId(Long groupId) {
        return groupCenterRelationshipService.selectCount(
                new EntityWrapper<GroupCenterRelationship>()
                        .eq("group_id",groupId));
    }

    /**
     * 导入责任中心
     * @param in
     * @param setOfBooksId 账套Id
     * @return
     * @throws Exception
     */
    public UUID importResponsibilityCenters(InputStream in, Long setOfBooksId) throws Exception {
        UUID batchNumber = UUID.randomUUID();
        SetOfBooks setOfBooks = setOfBooksService.getSetOfBooksById(setOfBooksId);
        if(setOfBooks == null){
            throw new BizException(RespCode.SETOFBOOKS_NOT_EXIST);
        }
        ExcelImportHandler<ResponsibilityCenterTemp> excelImportHandler = new ExcelImportHandler<ResponsibilityCenterTemp>() {

            @Override
            public void clearHistoryData() {
                centerTempService.deleteHistoryData();
            }

            @Override
            public Class<ResponsibilityCenterTemp> getEntityClass() {
                return ResponsibilityCenterTemp.class;
            }

            @Override
            public List<ResponsibilityCenterTemp> persistence(List<ResponsibilityCenterTemp> list) {
                //导入数据
                centerTempService.insertBatch(list);
                centerTempService.updateExists(batchNumber.toString());
                return list;
            }

            @Override
            public void check(List<ResponsibilityCenterTemp> importData) {
                checkImportData(importData,batchNumber,setOfBooksId);
            }
        };
        excelImportService.importExcel(in,false,2,excelImportHandler);
        return batchNumber;
    }

    /**
     * 导入数据校验
     * @param importData  导入数据
     * @param batchNumber 批次号
     * @param setOfBooksId 账套Id
     */
    public void checkImportData(List<ResponsibilityCenterTemp> importData, UUID batchNumber, Long setOfBooksId) {
        //初始化数据
        importData.stream().forEach(e->{
            e.setBatchNumber(batchNumber.toString());
            e.setErrorFlag(false);
            e.setErrorDetail("");
            e.setSetOfBooksId(setOfBooksId);
        });
        //非空校验
        importData.stream().filter(e-> StringUtil.isNullOrEmpty(e.getRowNumber())
                || StringUtil.isNullOrEmpty(e.getEnabledStr())
                ||StringUtil.isNullOrEmpty(e.getResponsibilityCenterCode())
                ||StringUtil.isNullOrEmpty(e.getResponsibilityCenterName()))
                .forEach(e ->{
                    e.setErrorFlag(true);
                    e.setErrorDetail("必输字段不能为空");
                });

        //判断该责任中心是否在该账套唯一
        importData.stream().forEach(e->{

            int count = this.selectCount(
                    new EntityWrapper<ResponsibilityCenter>()
                    .eq("responsibility_center_code", e.getResponsibilityCenterCode())
                    .eq("set_of_books_id", e.getSetOfBooksId())
            );
            if(count != 0 ){
                e.setErrorFlag(true);
                e.setErrorDetail("一个账套下责任中心代码不可重复");
            }
            // 验证是否启用
            if(StringUtils.isNotEmpty(e.getEnabledStr())) {
                if (!(e.getEnabledStr().equals(Constants.YES) || e.getEnabledStr().equals(Constants.NO)
                        || e.getEnabledStr().equals(Constants.SMALL_YES) || e.getEnabledStr().equals(Constants.SMALL_NO))) {
                    e.setErrorFlag(true);
                    e.setErrorDetail(e.getErrorDetail() + "是否启用输入错误！");
                } else {
                    e.setEnabled(e.getEnabledStr().toUpperCase().equals(Constants.YES));
                }
            }
        });
    }

    /**
     * 【责任中心-导入】查询导入结果
     * @param transactionOid
     * @return
     */
    public ImportResultDTO queryResultInfo(String transactionOid) {
        return centerTempService.queryResultInfo(transactionOid);
    }

    /**
     * 【责任中心-导入】确认导入
     * @param transactionId
     * @return
     */
    public Boolean confirmImport(String transactionId) {
        return centerTempService.confirmImport(transactionId);
    }

    /**
     *【责任中心-导入】取消导入
     * @param transactionId
     * @return
     */
    public Boolean deleteImportData(String transactionId) {
        return centerTempService.delete(new EntityWrapper<ResponsibilityCenterTemp>()
                .eq("batch_number", transactionId));
    }

    /**
     * 【责任中心-导出】
     * @param setOfBooksId
     * @param request
     * @param response
     * @param exportConfig
     */
    public void exportResponsibilityCenterData(Long setOfBooksId, HttpServletRequest request, HttpServletResponse response, ExportConfig exportConfig) throws IOException {
        SetOfBooks setOfBooks = setOfBooksService.getSetOfBooksById(setOfBooksId);
        if(setOfBooks == null){
            throw new BizException(RespCode.DIMENSION_SETOFBOOKS_NOT_EXIST);
        }
        log.info("Start Exporting Dimension Items");
        Page page = new Page<ResponsibilityCenter>(0, 0);
        responsibilityCenterMapper.selectPage(page,new EntityWrapper<ResponsibilityCenter>().eq("set_of_books_id",setOfBooksId));
        int total = TypeConversionUtils.parseInt(page.getTotal());
        int threadNumber = total > 100000 ? 8 : 2;
        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<ResponsibilityCenter, ResponsibilityCenterExportDTO>() {
            @Override
            public int getTotal() {
                return total;
            }
            @Override
            public List<ResponsibilityCenter> queryDataByPage(Page page) {
                return  responsibilityCenterMapper.selectPage(page,
                        new EntityWrapper<ResponsibilityCenter>()
                                .eq("set_of_books_id",setOfBooksId));
            }
            @Override
            public ResponsibilityCenterExportDTO toDTO(ResponsibilityCenter t) {
                ResponsibilityCenterExportDTO centerExportDTO = new ResponsibilityCenterExportDTO();
                centerExportDTO.setResponsibilityCenterCode(t.getResponsibilityCenterCode());
                centerExportDTO.setResponsibilityCenterName(t.getResponsibilityCenterName());
                if(t.getEnabled() != null){
                    if (t.getEnabled()){
                        centerExportDTO.setEnabled(Constants.YES);
                    }
                    else{
                        centerExportDTO.setEnabled(Constants.NO);
                    }
                }
                return centerExportDTO;
            }
            @Override
            public Class<ResponsibilityCenterExportDTO> getEntityClass() {
                return ResponsibilityCenterExportDTO.class;
            }
        },threadNumber, request, response);
    };

    /**
     * 删除责任中心
     * @param id 责任中心Id
     * @return
     */
    @Transactional
    public Boolean delecteResponsibilityCenterById(Long id) {
        ResponsibilityCenter responsibilityCenter =responsibilityCenterMapper.selectById(id);
        if(responsibilityCenter == null){
            throw new BizException(RespCode.RESPONSIBILITY_CENTER_NOT_EXIST);
        }
        responsibilityCenter.setDeleted(true);
        responsibilityCenterMapper.updateById(responsibilityCenter);
        return true;
    }

    /**
     * 导出错误信息
     * @param transactionId 批次号
     * @return
     */
    public byte[] exportFailedData(String transactionId) {
        List<ResponsibilityCenterTemp> customEnumerationItemTemps = centerTempService.selectList(
                new EntityWrapper<ResponsibilityCenterTemp>()
                        .eq("batch_number", transactionId)
                        .eq("error_flag", 1));
        InputStream in = null;
        ByteArrayOutputStream bos = null;
        XSSFWorkbook workbook = null;
        try {
            in = StreamUtil.getResourceStream(ResponsibilityCenterImportCode.ERROR_TEMPLATE_PATH);
            workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int startRow = DimensionItemImportCode.EXCEL_BASEROW_ERROR;
            Row row = null;
            Cell cell = null;
            for (ResponsibilityCenterTemp importDTO : customEnumerationItemTemps) {
                row = sheet.createRow(startRow++);
                cell = row.createCell(DimensionItemImportCode.ROW_NUMBER);
                cell.setCellValue(importDTO.getRowNumber());
                cell = row.createCell(ResponsibilityCenterImportCode.Responsibility_Center_Code);
                cell.setCellValue(importDTO.getResponsibilityCenterCode());
                cell = row.createCell(ResponsibilityCenterImportCode.Responsibility_Center_Name);
                cell.setCellValue(importDTO.getResponsibilityCenterName());
                cell = row.createCell(ResponsibilityCenterImportCode.ENABLED);
                cell.setCellValue(importDTO.getEnabledStr());
                cell = row.createCell(DimensionItemImportCode.ERROR_DETAIL);
                cell.setCellValue(importDTO.getErrorDetail());

            }
            bos = new ByteArrayOutputStream();
            workbook.write(bos);
            bos.flush();
            workbook.close();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new BizException(RespCode.READ_FILE_FAILED);
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (workbook != null) {
                    workbook.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                throw new BizException(RespCode.READ_FILE_FAILED);
            }
        }
    }

    /**
     * 获取当前所选账套下所有启用的责任中心，如果选择了公司，则只能选到分配给此公司的责任中心
     * @param companyId 公司id
     * @param info
     * @param codeFrom
     * @param codeTo
     * @param page
     * @return
     */
    public Page<ResponsibilityCenter> pageDefaultResponsibilityCenter(Long setOfBooksId,
                                                                      Long companyId,
                                                                      String info,
                                                                      String codeFrom,
                                                                      String codeTo,
                                                                      List<Long> ids,
                                                                      Boolean enabled,
                                                                      Page page) {
        List<ResponsibilityCenter> resCenterList ;
        if(companyId != null){
            ids = resAssginCompanyService.selectList(
                    new EntityWrapper<ResponsibilityAssignCompany>()
                            .eq("company_id", companyId)
            ).stream().map(ResponsibilityAssignCompany::getResponsibilityCenterId).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(ids)){
                return page;
            }
        }
        resCenterList = this.pageByResponsibilityCenterByIds(setOfBooksId, info, codeFrom, codeTo, ids,enabled, page);
        page.setRecords(resCenterList);
        return page;
    }

    /**
     * 根据责任中心ids范围获取关联的责任中心
     * @param setOfBooksId 账套Id
     * @param ids  责任中心ids
     * @return
     */
    public List<ResponsibilityCenter> listByResponsibilityCenterConditionByIds(Long setOfBooksId,
                                                                               List<Long> ids,
                                                                               Boolean enabled) {
        return responsibilityCenterMapper.selectList(
                new EntityWrapper<ResponsibilityCenter>()
                        .eq("set_of_books_id", setOfBooksId)
                        .in(CollectionUtils.isNotEmpty(ids), "id", ids)
                        .eq(enabled != null,"enabled",enabled));
    }

    /**
     * 根据责任中心id获取责任中心详情
     * @param responsibilityCenterId  责任中心id
     * @return
     */
    public ResponsibilityCenter getResponsibilityCenterById(Long responsibilityCenterId) {
        ResponsibilityCenter responsibilityCenter = baseMapper.selectById(responsibilityCenterId);
        if(responsibilityCenter != null){
            StringBuilder strBuilder = new StringBuilder()
                    .append(responsibilityCenter.getResponsibilityCenterCode())
                    .append("-")
                    .append(responsibilityCenter.getResponsibilityCenterName());
            responsibilityCenter.setResponsibilityCenterCodeName(strBuilder.toString());
            SetOfBooks setOfBooks =  setOfBooksService.getSetOfBooksById(responsibilityCenter.getSetOfBooksId());
            if(StringUtils.isNotEmpty(setOfBooks.toString())){
                responsibilityCenter.setSetOfBooksName(setOfBooks.getSetOfBooksName());
            }
        }
        return responsibilityCenter;
    }

    /**
     * 根据责任中心id列表查询责任中心信息
     * @param setOfBooksId 账套id
     * @param info  责任中心代码或者名称
     * @param codeFrom 责任中心代码从
     * @param codeTo 责任中心代码至
     * @param enabled 启用禁用
     * @param ids 责任中心Id列表
     * @return
     */
    public List<ResponsibilityCenter> pageByResponsibilityCenterByIds(Long setOfBooksId,
                                                                      String info,
                                                                      String codeFrom,
                                                                      String codeTo,
                                                                      List<Long> ids,
                                                                      Boolean enabled,
                                                                      Page mybatisPage) {
        List<ResponsibilityCenter> resCenterList = baseMapper.pageByResponsibilityCenterConditionByIds(setOfBooksId, info, codeFrom, codeTo, ids, enabled, mybatisPage);
        resCenterList.stream().forEach(responsibilityCenter -> {
            StringBuilder strBuilder = new StringBuilder()
                    .append(responsibilityCenter.getResponsibilityCenterCode())
                    .append("-")
                    .append(responsibilityCenter.getResponsibilityCenterName());
            responsibilityCenter.setResponsibilityCenterCodeName(strBuilder.toString());
            if (responsibilityCenter.getSetOfBooksId() != null) {
                SetOfBooks setOfBooks = setOfBooksService.getSetOfBooksById(responsibilityCenter.getSetOfBooksId());
                if (setOfBooks != null) {
                    responsibilityCenter.setSetOfBooksName(setOfBooks.getSetOfBooksName());
                }
            }
        });
        return resCenterList;
    }

    /**
     * 查询责任中心信息 - 分页
     * @param setOfBooksId
     * @param code
     * @param codeFrom
     * @param codeTo
     * @param name
     * @param keyWord
     * @param ids
     * @param enabled
     * @param mybatisPage
     * @return
     */
    public Page<ResponsibilityCenterCO> pageByResponsibilityCenterByCond(Long setOfBooksId,
                                                                         String code,
                                                                         String codeFrom,
                                                                         String codeTo,
                                                                         String name,
                                                                         String keyWord,
                                                                         List<Long> ids,
                                                                         Boolean enabled,
                                                                         Page<ResponsibilityCenterCO> mybatisPage) {
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Wrapper<ResponsibilityCenter> wrapper = new EntityWrapper<ResponsibilityCenter>()
                .eq("tenant_id", tenantId)
                .eq(setOfBooksId != null,"set_of_books_id", setOfBooksId)
                .eq(enabled != null,"enabled",enabled)
                .in(ids != null && ids.size() > 0,"id",ids)
                .like(StringUtils.isNotEmpty(code),"responsibility_center_code", code)
                .ge(StringUtils.isNotEmpty(codeFrom),"responsibility_center_code",codeFrom)
                .le(StringUtils.isNotEmpty(codeTo),"responsibility_center_code",codeTo)
                .like(StringUtils.isNotEmpty(name),"responsibility_center_name",name);
        if(org.springframework.util.StringUtils.hasText(keyWord)){
            wrapper.andNew()
                    .like("department_code", keyWord)
                    .or()
                    .like("name", keyWord);
        }
        List<ResponsibilityCenter> responsibilityCenters = baseMapper.selectPage(mybatisPage, wrapper);
        mybatisPage.setRecords(mapperFacade.mapAsList(responsibilityCenters, ResponsibilityCenterCO.class));
        return mybatisPage;
    }

    /**
     * 查询责任中心信息
     * @param setOfBooksId
     * @param code
     * @param codeFrom
     * @param codeTo
     * @param name
     * @param keyWord
     * @param ids
     * @param enabled
     * @return
     */
    public List<ResponsibilityCenterCO> listByResponsibilityCenterByCond(Long setOfBooksId,
                                                                         String code,
                                                                         String codeFrom,
                                                                         String codeTo,
                                                                         String name,
                                                                         String keyWord,
                                                                         List<Long> ids,
                                                                         Boolean enabled) {
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Wrapper<ResponsibilityCenter> wrapper = new EntityWrapper<ResponsibilityCenter>()
                .eq("tenant_id", tenantId)
                .eq(setOfBooksId != null,"set_of_books_id", setOfBooksId)
                .eq(enabled != null,"enabled",enabled)
                .in(ids != null && ids.size() > 0,"id",ids)
                .like(StringUtils.isNotEmpty(code),"responsibility_center_code", code)
                .ge(StringUtils.isNotEmpty(codeFrom),"responsibility_center_code",codeFrom)
                .le(StringUtils.isNotEmpty(codeTo),"responsibility_center_code",codeTo)
                .like(StringUtils.isNotEmpty(name),"responsibility_center_name",name);
        if(org.springframework.util.StringUtils.hasText(keyWord)){
            wrapper.andNew()
                    .like("department_code", keyWord)
                    .or()
                    .like("name", keyWord);
        }
        List<ResponsibilityCenter> responsibilityCenters = baseMapper.selectList(wrapper);
        return mapperFacade.mapAsList(responsibilityCenters,ResponsibilityCenterCO.class);
    }

    /**
     * 根据责任中心id集合获取责任中集合
     * @param idList
     * @return
     */
    public List<ResponsibilityCenterCO> getResponsibilityCenterByIdList(List<Long> idList){
        List<ResponsibilityCenterCO> result = new ArrayList<>();
        if (!org.apache.commons.collections4.CollectionUtils.isEmpty(idList)) {
            List<ResponsibilityCenter> responsibilityCenters = this.selectBatchIds(idList);
            result = mapperFacade.mapAsList(responsibilityCenters,ResponsibilityCenterCO.class);
        }
        return result;
    }

    /**
     * 条件查询租户下责任中心
     * @param selectId
     * @param code
     * @param name
     * @param securityType
     * @param filterId
     * @param queryPage
     * @return
     */
    public Page<BasicCO> pageResponsibilityCenterByInfoResultBasic(Long selectId, String code, String name, String securityType, Long filterId, Page queryPage) {
        List<BasicCO> basicCOS = new ArrayList<>();
        if (selectId != null) {
            ResponsibilityCenter responsibilityCenter = this.selectById(selectId);
            if(responsibilityCenter == null){
                return queryPage;
            }else {
                BasicCO basicCO = BasicCO
                        .builder()
                        .id(responsibilityCenter.getId())
                        .name(responsibilityCenter.getResponsibilityCenterName())
                        .code(responsibilityCenter.getResponsibilityCenterCode())
                        .build();
                basicCOS.add(basicCO);
            }
        }else{
            List<ResponsibilityCenter> responsibilityCenters = baseMapper.selectPage(queryPage, new EntityWrapper<ResponsibilityCenter>()
                    .eq("tenant_id", filterId)
                    .like(StringUtils.isNotEmpty(code), "responsibility_center_code", code)
                    .like(StringUtils.isNotEmpty(name), "responsibility_center_name", name));
            responsibilityCenters.forEach(responsibilityCenter -> {
                BasicCO basicCO = BasicCO.builder()
                        .id(responsibilityCenter.getId())
                        .name(responsibilityCenter.getResponsibilityCenterName())
                        .code(responsibilityCenter.getResponsibilityCenterCode())
                        .build();
                basicCOS.add(basicCO);
            });
        }
        queryPage.setRecords(basicCOS);
        return queryPage;
    }

    public List<ResponsibilityLov> pageByCompanyAndDepartment(Page page,
                                                              Long companyId,
                                                              Long departmentId,
                                                              String code,
                                                              String name,
                                                              Long id) {
        if (id != null){
            page.setSearchCount(Boolean.FALSE);
        }
        return baseMapper.pageByCompanyAndDepartment(page, companyId, departmentId, code, name, id);
    }
}

package com.hand.hcf.app.mdata.dimension.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.DimensionDetailCO;
import com.hand.hcf.app.common.co.DimensionItemCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;

import com.hand.hcf.app.mdata.contact.domain.Contact;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.department.domain.Department;
import com.hand.hcf.app.mdata.department.service.DepartmentService;
import com.hand.hcf.app.mdata.dimension.domain.*;
import com.hand.hcf.app.mdata.dimension.domain.enums.DimensionItemImportCode;
import com.hand.hcf.app.mdata.dimension.domain.enums.VisibleUserScopeEnum;
import com.hand.hcf.app.mdata.dimension.domain.temp.DimensionItemTemp;
import com.hand.hcf.app.mdata.dimension.dto.DepartmentOrUserGroupReturnDTO;
import com.hand.hcf.app.mdata.dimension.dto.DimensionItemExportDTO;
import com.hand.hcf.app.mdata.dimension.dto.DimensionItemRequestDTO;
import com.hand.hcf.app.mdata.dimension.persistence.DimensionItemMapper;
import com.hand.hcf.app.mdata.dimension.persistence.DimensionMapper;
import com.hand.hcf.app.mdata.parameter.service.ParameterService;
import com.hand.hcf.app.mdata.parameter.service.ParameterValuesService;
import com.hand.hcf.app.mdata.system.constant.Constants;
import com.hand.hcf.app.mdata.contact.domain.UserGroup;
import com.hand.hcf.app.mdata.contact.service.UserGroupService;
import com.hand.hcf.app.mdata.utils.PatternMatcherUtil;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.mdata.utils.StringUtil;
import com.hand.hcf.core.domain.ExportConfig;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.handler.ExcelExportHandler;
import com.hand.hcf.core.handler.ExcelImportHandler;
import com.hand.hcf.core.service.BaseI18nService;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.service.ExcelExportService;
import com.hand.hcf.core.service.ExcelImportService;
import com.hand.hcf.core.util.TypeConversionUtils;
import com.hand.hcf.core.web.dto.ImportResultDTO;
import com.itextpdf.text.io.StreamUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class DimensionItemService extends BaseService<DimensionItemMapper, DimensionItem> {

    private final Logger log = LoggerFactory.getLogger(DimensionItemService.class);

    @Autowired
    private DimensionItemMapper dimensionItemMapper;

    @Autowired
    private DimensionItemAssignDepartmentService dimensionDepartmentService;

    @Autowired
    private DimensionItemAssignUserGroupService dimensionUserGroupService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private DimensionItemTempService dimensionItemTempService;

    @Autowired
    private ExcelImportService excelImportService;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private DimensionService dimensionService;

    @Autowired
    private DimensionItemAssignCompanyService dimensionItemAssignCompanyService;

    @Autowired
    private BaseI18nService baseI18nService;

    @Autowired
    private DimensionItemAssignEmployeeService dimensionItemAssignEmployeeService;

    @Autowired
    private DimensionMapper dimensionMapper;

    /**
     *  新建维值
     * @param dimensionItemRequestDTO
     * @return
     */
    @Transactional
    public DimensionItemRequestDTO insertDimensionItem(DimensionItemRequestDTO dimensionItemRequestDTO) {
        log.debug("REST request to save dimensionItem : {}", dimensionItemRequestDTO);
        DimensionItem dimensionItem = dimensionItemRequestDTO.getDimensionItem();

        if (dimensionService.selectById(dimensionItem.getDimensionId()) == null) {
            throw new BizException(RespCode.DIMENSION_NOT_EXIST);
        }
        if (TypeConversionUtils.isNotEmpty(dimensionItem.getId())){
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        if (dimensionItemMapper.selectList(
                new EntityWrapper<DimensionItem>()
                        .eq("dimension_id", dimensionItem.getDimensionId())
                        .eq("dimension_item_code",dimensionItem.getDimensionItemCode())
        ).size() > 0){
            throw new BizException(RespCode.DIMENSION_ITEM_CODE_REPEAT);
        }
        dimensionItemMapper.insert(dimensionItem);

        //插入部门或人员组
        if ( VisibleUserScopeEnum.DEPARTMENT.getId().equals(dimensionItem.getVisibleUserScope()) ){
            List<Long> departmentIdList = dimensionItemRequestDTO.getDepartmentOrUserGroupIdList();
            if ( null != departmentIdList ){
                List<DimensionItemAssignDepartment> departmentList = new ArrayList<>();
                departmentIdList.stream().forEach(departmentId ->{
                    DimensionItemAssignDepartment department = new DimensionItemAssignDepartment();
                    department.setDepartmentId(departmentId);
                    department.setDimensionItemId(dimensionItem.getId());
                    departmentList.add(department);
                });
                dimensionDepartmentService.insertBatch(departmentList);
            }
        }else if ( VisibleUserScopeEnum.USER_GROUP.getId().equals(dimensionItem.getVisibleUserScope()) ){
            List<Long> userGroupIdList = dimensionItemRequestDTO.getDepartmentOrUserGroupIdList();
            if ( null != userGroupIdList ){
                List<DimensionItemAssignUserGroup> userGroupList = new ArrayList<>();
                userGroupIdList.stream().forEach(userGroupId -> {
                    DimensionItemAssignUserGroup userGroup = new DimensionItemAssignUserGroup();
                    userGroup.setUserGroupId(userGroupId);
                    userGroup.setDimensionItemId(dimensionItem.getId());
                    userGroupList.add(userGroup);
                });
                dimensionUserGroupService.insertBatch(userGroupList);
            }
        }else if ( VisibleUserScopeEnum.USER.getId().equals(dimensionItem.getVisibleUserScope()) ){
            List<Long> contactIdList = dimensionItemRequestDTO.getDepartmentOrUserGroupIdList();
            if ( null != contactIdList ){
                List<DimensionItemAssignEmployee> assignEmployeeList = new ArrayList<>();
                contactIdList.stream().forEach(contactId -> {
                    DimensionItemAssignEmployee assignEmployee = new DimensionItemAssignEmployee();
                    assignEmployee.setContactId(contactId);
                    assignEmployee.setDimensionItemId(dimensionItem.getId());
                    assignEmployeeList.add(assignEmployee);
                });
                dimensionItemAssignEmployeeService.insertBatch(assignEmployeeList);
            }
        }
        return dimensionItemRequestDTO;
    }

    /**
     * 更新维值
     * @param dimensionItemRequestDTO
     * @return
     */
    @Transactional
    public DimensionItemRequestDTO updateDimensionItem(DimensionItemRequestDTO dimensionItemRequestDTO) {
        log.debug("REST request to update dimensionItem : {}", dimensionItemRequestDTO);
        DimensionItem newDimensionItem = dimensionItemRequestDTO.getDimensionItem();
        if (TypeConversionUtils.isEmpty(newDimensionItem.getId())){
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        DimensionItem oldDimensionItem = dimensionItemMapper.selectById(newDimensionItem.getId());
        if (dimensionItemMapper.selectList(
                new EntityWrapper<DimensionItem>()
                        .ne("id", newDimensionItem.getId())
                        .eq("dimension_id", newDimensionItem.getDimensionId())
                        .eq("dimension_item_code",newDimensionItem.getDimensionItemCode())
        ).size() > 0){
            throw new BizException(RespCode.DIMENSION_ITEM_CODE_REPEAT);
        }
        dimensionItemMapper.updateById(newDimensionItem);

        //修改维值关联部门或人员组
        //删除原有数据
        if ( VisibleUserScopeEnum.DEPARTMENT.getId().equals(oldDimensionItem.getVisibleUserScope()) ){
            dimensionDepartmentService.delete(
                    new EntityWrapper<DimensionItemAssignDepartment>()
                            .eq("dimension_item_id", oldDimensionItem.getId())
            );
        }else if ( VisibleUserScopeEnum.USER_GROUP.getId().equals(oldDimensionItem.getVisibleUserScope()) ){
            dimensionUserGroupService.delete(
                    new EntityWrapper<DimensionItemAssignUserGroup>()
                            .eq("dimension_item_id", oldDimensionItem.getId())
            );
        } else if(VisibleUserScopeEnum.USER.getId().equals(oldDimensionItem.getVisibleUserScope()) ) {
            dimensionItemAssignEmployeeService.delete(
                    new EntityWrapper<DimensionItemAssignEmployee>()
                            .eq("dimension_item_id", oldDimensionItem.getId())
            );
        }
        //插入部门或人员组
        if ( VisibleUserScopeEnum.DEPARTMENT.getId().equals(newDimensionItem.getVisibleUserScope()) ){
            List<Long> departmentIdList = dimensionItemRequestDTO.getDepartmentOrUserGroupIdList();
            if ( null != departmentIdList ){
                List<DimensionItemAssignDepartment> departmentList = new ArrayList<>();
                departmentIdList.stream().forEach(departmentId ->{
                    DimensionItemAssignDepartment department = new DimensionItemAssignDepartment();
                    department.setDepartmentId(departmentId);
                    department.setDimensionItemId(newDimensionItem.getId());
                    departmentList.add(department);
                });
                dimensionDepartmentService.insertBatch(departmentList);
            }
        }else if ( VisibleUserScopeEnum.USER_GROUP.getId().equals(newDimensionItem.getVisibleUserScope()) ){
            List<Long> userGroupIdList = dimensionItemRequestDTO.getDepartmentOrUserGroupIdList();
            if ( null != userGroupIdList ){
                List<DimensionItemAssignUserGroup> userGroupList = new ArrayList<>();
                userGroupIdList.stream().forEach(userGroupId -> {
                    DimensionItemAssignUserGroup userGroup = new DimensionItemAssignUserGroup();
                    userGroup.setUserGroupId(userGroupId);
                    userGroup.setDimensionItemId(newDimensionItem.getId());
                    userGroupList.add(userGroup);
                });
                dimensionUserGroupService.insertBatch(userGroupList);
            }
        }else if ( VisibleUserScopeEnum.USER.getId().equals(newDimensionItem.getVisibleUserScope()) ){
            List<Long> contactIdList = dimensionItemRequestDTO.getDepartmentOrUserGroupIdList();
            if ( null != contactIdList ){
                List<DimensionItemAssignEmployee> assignEmployeeList = new ArrayList<>();
                contactIdList.stream().forEach(contactId -> {
                    DimensionItemAssignEmployee assignEmployee = new DimensionItemAssignEmployee();
                    assignEmployee.setContactId(contactId);
                    assignEmployee.setDimensionItemId(newDimensionItem.getId());
                    assignEmployeeList.add(assignEmployee);
                });
                dimensionItemAssignEmployeeService.insertBatch(assignEmployeeList);
            }
        }
        return dimensionItemRequestDTO;
    }

    /**
     * 根据维度ID和其他条件查询维值
     * @param dimensionId
     * @param dimensionItemCode
     * @param page
     * @return
     */
    public List<DimensionItemRequestDTO> pageDimensionItemsByDimensionIdAndCond(Long dimensionId, String dimensionItemCode, String dimensionItemName, Boolean enabled, Page page) {
        List<DimensionItem> dimensionItemList = dimensionItemMapper.selectPage(
                page,
                new EntityWrapper<DimensionItem>()
                        .eq("dimension_id", dimensionId)
                        .like(TypeConversionUtils.isNotEmpty(dimensionItemCode), "dimension_item_code",dimensionItemCode)
                        .like(TypeConversionUtils.isNotEmpty(dimensionItemName), "dimension_item_name",dimensionItemName)
                        .eq(TypeConversionUtils.isNotEmpty(enabled), "enabled",enabled)
                        .orderBy("enabled", false)
                        .orderBy("dimension_item_code")
        );
        List<DimensionItemRequestDTO> dimensionItemRequestDTOList = new ArrayList<>();
        dimensionItemList.stream().forEach(d -> dimensionItemRequestDTOList.add(this.getDimensionItemById(d.getId())));
        return dimensionItemRequestDTOList;
    }

    /**
     * 根据维值ID查询维值详情
     * @param dimensionItemId
     * @return
     */
    public DimensionItemRequestDTO getDimensionItemById(Long dimensionItemId) {
        DimensionItemRequestDTO requestDTO = new DimensionItemRequestDTO();
        DimensionItem dimensionItem = dimensionItemMapper.selectById(dimensionItemId);
        if (dimensionItem == null) {
            return requestDTO;
        }
        dimensionItem.setI18n(baseI18nService.getI18nMap(DimensionItem.class, dimensionItem.getId()));
        requestDTO.setDimensionItem(dimensionItem);
        List<Long> idList = null;
        List<DepartmentOrUserGroupReturnDTO> dOrUgList = new ArrayList<>();
        if (VisibleUserScopeEnum.DEPARTMENT.getId().equals(dimensionItem.getVisibleUserScope())) {
            idList = dimensionDepartmentService.selectList(
                    new EntityWrapper<DimensionItemAssignDepartment>()
                            .eq("dimension_item_id", dimensionItemId)
            ).stream().map(DimensionItemAssignDepartment::getDepartmentId).collect(toList());
            if (idList != null) {
                List<Department> departments = departmentService.selectBatchIds(idList);
                departments.stream().forEach(department -> {
                    DepartmentOrUserGroupReturnDTO dep = new DepartmentOrUserGroupReturnDTO();
                    dep.setId(department.getId());
                    dep.setPathOrName(department.getPath());
                    dOrUgList.add(dep);
                });
            }
        } else if (VisibleUserScopeEnum.USER_GROUP.getId().equals(dimensionItem.getVisibleUserScope())) {
            idList = dimensionUserGroupService.selectList(
                    new EntityWrapper<DimensionItemAssignUserGroup>()
                            .eq("dimension_item_id", dimensionItemId)
            ).stream().map(DimensionItemAssignUserGroup::getUserGroupId).collect(toList());
            if (idList != null) {
                List<UserGroup> userGroups = userGroupService.selectBatchIds(idList);
                userGroups.stream().forEach(department -> {
                    DepartmentOrUserGroupReturnDTO dep = new DepartmentOrUserGroupReturnDTO();
                    dep.setId(department.getId());
                    dep.setPathOrName(department.getName());
                    dOrUgList.add(dep);
                });
            }
        }else if (VisibleUserScopeEnum.USER.getId().equals(dimensionItem.getVisibleUserScope())) {
            idList = dimensionItemAssignEmployeeService.selectList(
                    new EntityWrapper<DimensionItemAssignEmployee>()
                            .eq("dimension_item_id", dimensionItemId)
            ).stream().map(DimensionItemAssignEmployee::getContactId).collect(toList());
            if (idList != null) {
                List<Contact> contactList = contactService.selectBatchIds(idList);
                contactList.stream().forEach(contact -> {
                    DepartmentOrUserGroupReturnDTO dep = new DepartmentOrUserGroupReturnDTO();
                    dep.setId(contact.getId());
                    dep.setPathOrName(contact.getFullName());
                    dOrUgList.add(dep);
                });
            }
        }
        requestDTO.setDepartmentOrUserGroupIdList(idList);
        requestDTO.setDepartmentOrUserGroupList(dOrUgList);
        return requestDTO;
    }

    /**
     * 根据维值ID和代码筛选维值
     * @param dimensionItemIds
     * @param dimensionId
     * @param dimensionItemCode
     * @param page
     * @return
     */
    public List<DimensionItem> pageDimensionItemsByIdsAndCond(List<Long> dimensionItemIds, Long dimensionId, String dimensionItemCode, Page page) {
        if (dimensionItemIds.size() == 0) {
            return new ArrayList<>();
        }
        List<DimensionItem> dimensionItemList = dimensionItemMapper.selectPage(
                page,
                new EntityWrapper<DimensionItem>()
                        .in("id", dimensionItemIds)
                        .eq("dimension_id", dimensionId)
                        .like(TypeConversionUtils.isNotEmpty(dimensionItemCode), "dimension_item_code",dimensionItemCode)
                        .orderBy("enabled", false)
                        .orderBy("dimension_item_code")
        );
        return dimensionItemList;
    }

    /**
     * 根据维值ID和其他条件筛选维值
     * @param dimensionItemIds
     * @param dimensionId
     * @param dimensionItemCode
     * @param dimensionItemName
     * @param enabled
     * @param page
     * @return
     */
    public List<DimensionItem> pageDimensionItemsByIdsAndCond(List<Long> dimensionItemIds, Long dimensionId, String dimensionItemCode, String dimensionItemName, Boolean enabled, Page page) {
        List<DimensionItem> dimensionItemList = dimensionItemMapper.selectPage(
                page,
                new EntityWrapper<DimensionItem>()
                        .notIn("id", dimensionItemIds)
                        .eq("dimension_id", dimensionId)
                        .like(TypeConversionUtils.isNotEmpty(dimensionItemCode), "dimension_item_code",dimensionItemCode)
                        .like(TypeConversionUtils.isNotEmpty(dimensionItemName), "dimension_item_name",dimensionItemName)
                        .eq(TypeConversionUtils.isNotEmpty(enabled), "enabled",enabled)
                        .orderBy("enabled", false)
                        .orderBy("dimension_item_code")
        );
        return dimensionItemList;
    }

    /**
     * 导入维值
     * @param inputStream
     * @param dimensionId
     * @return
     * @throws Exception
     */
    @Transactional
    public UUID importDimensionItems(InputStream inputStream, Long dimensionId) throws Exception{

        Dimension dimension = dimensionService.selectById(dimensionId);
        if (dimension == null) {
            throw new BizException(RespCode.DIMENSION_NOT_EXIST);
        }
        log.info("Start Importing Dimension Items");

        UUID batchNumber = UUID.randomUUID();
        ExcelImportHandler<DimensionItemTemp> excelImportHandler = new ExcelImportHandler<DimensionItemTemp>() {
            @Override
            public void clearHistoryData() {
                dimensionItemTempService.deleteHistoryData();
            }

            @Override
            public Class<DimensionItemTemp> getEntityClass() {
                return DimensionItemTemp.class;
            }

            @Override
            public List<DimensionItemTemp> persistence(List<DimensionItemTemp> list) {
                // 导入数据
                dimensionItemTempService.insertBatch(list);
                // 重复数据校验
                dimensionItemTempService.updateExists(batchNumber.toString());
                return list;
            }

            @Override
            public void check(List<DimensionItemTemp> list) {
                checkImportData(list, dimension, batchNumber);
            }
        };
        excelImportService.importExcel(inputStream,false,3,excelImportHandler);
        return batchNumber;
    }

    /**
     * 数据校验
     * @param importData
     * @param dimension
     * @param batchNumber
     */
    public void checkImportData(List<DimensionItemTemp> importData,
                                Dimension dimension,
                                UUID batchNumber){
        //初始化
        importData.forEach(e -> {
            e.setBatchNumber(batchNumber.toString());
            e.setErrorFlag(false);
            e.setErrorDetail("");
            //初始值
            e.setDimensionId(dimension.getId());
        });

        // 非空校验
        importData
                .stream()
                .filter(e -> StringUtil.isNullOrEmpty(e.getRowNumber())
                        || StringUtil.isNullOrEmpty(e.getDimensionItemCode())
                        || StringUtil.isNullOrEmpty(e.getDimensionItemName())
                        || StringUtil.isNullOrEmpty(e.getEnabledStr()))
                .forEach(e -> {
                    e.setErrorFlag(true);
                    e.setErrorDetail("必输字段不能为空！");
                });

        //校验数据
        for(DimensionItemTemp importDomain: importData) {

            // 验证值编码
            if(StringUtils.isNotEmpty(importDomain.getDimensionItemCode())) {
                if (!PatternMatcherUtil.validationPatterMatcherRegex(importDomain.getDimensionItemCode(), PatternMatcherUtil.CODE_VALIDATION_REGEX)) {
                    importDomain.setErrorFlag(true);
                    importDomain.setErrorDetail(importDomain.getErrorDetail() + "编码最多输入36个字符或数字！");
                }
                if (!importDomain.getErrorFlag()) {
                    DimensionItem dimensionItem = this.selectOne(new EntityWrapper<DimensionItem>()
                            .eq("dimension_id",dimension.getId())
                            .eq("dimension_item_code", importDomain.getDimensionItemCode()));
                    if (dimensionItem != null) {
                        importDomain.setErrorFlag(true);
                        importDomain.setErrorDetail(importDomain.getErrorDetail() + "维值编码已存在！");
                    }
                }
            }

            // 验证值名称长度
            if(StringUtils.isNotEmpty(importDomain.getDimensionItemName())) {
                if (!PatternMatcherUtil.validationPatterMatcherRegex(importDomain.getDimensionItemName(), DimensionItemImportCode.NAME_LENGTH_REGEX)) {
                    importDomain.setErrorFlag(true);
                    importDomain.setErrorDetail(importDomain.getErrorDetail() + "维值名称最多输入200个文字！");
                }
            }

            // 验证是否启用
            if(StringUtils.isNotEmpty(importDomain.getEnabledStr())) {
                if (!(importDomain.getEnabledStr().equals(Constants.YES) || importDomain.getEnabledStr().equals(Constants.NO)
                        || importDomain.getEnabledStr().equals(Constants.SMALL_YES) || importDomain.getEnabledStr().equals(Constants.SMALL_NO))) {
                    importDomain.setErrorFlag(true);
                    importDomain.setErrorDetail(importDomain.getErrorDetail() + "是否启用输入错误！");
                } else {
                    importDomain.setEnabled(importDomain.getEnabledStr().toUpperCase().equals(Constants.YES));
                }
            }

        }

    }

    /**
     * 查询导入结果
     * @param transactionID
     * @return
     */
    public ImportResultDTO queryResultInfo(String transactionID) {

        return dimensionItemTempService.queryResultInfo(transactionID);
    }

    /**
     * 取消导入
     * @param transactionID
     * @return
     */
    @Transactional
    public Boolean deleteImportData(String transactionID) {
        boolean delete = dimensionItemTempService.delete(new EntityWrapper<DimensionItemTemp>()
                .eq("batch_number", transactionID));
        return delete;
    }

    /**
     * 确定导入
     * @param transactionID
     * @return
     */
    @Transactional
    public Boolean confirmImport(String transactionID) {

        return dimensionItemTempService.confirmImport(transactionID);
    }

    /**
     * 导出错误信息
     * @param transactionID
     * @return
     */
    public byte[] exportFailedData(String transactionID) {

        List<DimensionItemTemp> customEnumerationItemTemps = dimensionItemTempService.selectList(
                new EntityWrapper<DimensionItemTemp>()
                        .eq("batch_number", transactionID)
                        .eq("error_flag", 1));
        InputStream in = null;
        ByteArrayOutputStream bos = null;
        XSSFWorkbook workbook = null;
        try {
            in = StreamUtil.getResourceStream(DimensionItemImportCode.ERROR_TEMPLATE_PATH);
            workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int startRow = DimensionItemImportCode.EXCEL_BASEROW_ERROR;
            Row row = null;
            Cell cell = null;
            for (DimensionItemTemp importDTO : customEnumerationItemTemps) {
                row = sheet.createRow(startRow++);
                cell = row.createCell(DimensionItemImportCode.ROW_NUMBER);
                cell.setCellValue(importDTO.getRowNumber());
                cell = row.createCell(DimensionItemImportCode.Dimension_Item_CODE);
                cell.setCellValue(importDTO.getDimensionItemCode());
                cell = row.createCell(DimensionItemImportCode.Dimension_Item_name);
                cell.setCellValue(importDTO.getDimensionItemName());
                cell = row.createCell(DimensionItemImportCode.ENABLED);
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
     * 导出维值
     * @param dimensionId
     * @param request
     * @param response
     * @param exportConfig
     * @throws IOException
     */
    public void exportDimensionItemData(Long dimensionId,
                                        HttpServletRequest request,
                                        HttpServletResponse response,
                                        ExportConfig exportConfig) throws IOException {
        Dimension dimension = dimensionService.selectById(dimensionId);
        if (dimension == null) {
            throw new BizException(RespCode.DIMENSION_NOT_EXIST);
        }
        log.info("Start Exporting Dimension Items");

        Page page = new Page<DimensionItem>(0, 0);
        dimensionItemMapper.selectPage(page,
                new EntityWrapper<DimensionItem>()
                        .eq(dimension.getId() != null, "dimension_id", dimension.getId()));
        int total = TypeConversionUtils.parseInt(page.getTotal());
        int threadNumber = total > 100000 ? 8 : 2;
        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<DimensionItem, DimensionItemExportDTO>() {
            @Override
            public int getTotal() {
                return total;
            }
            @Override
            public List<DimensionItem> queryDataByPage(Page page) {
                List<DimensionItem> items = dimensionItemMapper.selectPage(page,
                        new EntityWrapper<DimensionItem>()
                                .eq(dimension.getId() != null, "dimension_id", dimension.getId()));
                return items;
            }

            @Override
            public DimensionItemExportDTO toDTO(DimensionItem head) {
                // 由于原查询状态是由前端赋值，所以在此赋值
                DimensionItemExportDTO dto = new DimensionItemExportDTO();
                dto.setDimensionItemCode(head.getDimensionItemCode());
                dto.setDimensionItemName(head.getDimensionItemName());
                if (head.getEnabled() != null){
                    if (head.getEnabled()) {
                        dto.setEnabled(Constants.YES);
                    } else {
                        dto.setEnabled(Constants.NO);
                    }
                }
                if (head.getVisibleUserScope() != null) {
                    if (VisibleUserScopeEnum.DEPARTMENT.getId().equals(head.getVisibleUserScope())) {
                        dto.setVisibleUserScope("部门");
                    } else if (VisibleUserScopeEnum.USER_GROUP.getId().equals(head.getVisibleUserScope())) {
                        dto.setVisibleUserScope("人员组");
                    } else {
                        dto.setVisibleUserScope("全部");
                    }
                }
                return dto;
            }

            @Override
            public Class<DimensionItemExportDTO> getEntityClass() {
                return DimensionItemExportDTO.class;
            }
        },threadNumber, request, response);
    }

    /**
     * 根据维值id删除维值及关联部门、人员组、公司
     * @param dimensionItemId
     */
    @Transactional
    public void deleteDimensionItemById(Long dimensionItemId) {
        DimensionItem dimensionItem = dimensionItemMapper.selectById(dimensionItemId);
        if (dimensionItem == null) {
            return;
        }

        dimensionDepartmentService.delete(new EntityWrapper<DimensionItemAssignDepartment>().eq("dimension_item_id", dimensionItemId));
        dimensionUserGroupService.delete(new EntityWrapper<DimensionItemAssignUserGroup>().eq("dimension_item_id", dimensionItemId));
        dimensionItemAssignCompanyService.delete(new EntityWrapper<DimensionItemAssignCompany>().eq("dimension_item_id", dimensionItemId));

        dimensionItem.setDeleted(true);
        String randomNumeric = RandomStringUtils.randomNumeric(6);
        dimensionItem.setDimensionItemCode(dimensionItem.getDimensionItemCode() + "_DELETED_" + randomNumeric);
        dimensionItemMapper.updateById(dimensionItem);
    }

    /**
     * 根据维度id删除
     * @param dimensionId
     */
    public void deleteByDimensionId(Long dimensionId) {
        List<DimensionItem> dimensionItems = dimensionItemMapper.selectList(
                new EntityWrapper<DimensionItem>().eq("dimension_id",dimensionId)
        );
        if (dimensionItems.size() > 0) {
            List<Long> dimensionItemIds = dimensionItems.stream().map(DimensionItem::getId).collect(Collectors.toList());
            dimensionDepartmentService.delete(new EntityWrapper<DimensionItemAssignDepartment>().in("dimension_item_id", dimensionItemIds));
            dimensionUserGroupService.delete(new EntityWrapper<DimensionItemAssignUserGroup>().in("dimension_item_id", dimensionItemIds));
            dimensionItemAssignCompanyService.delete(new EntityWrapper<DimensionItemAssignCompany>().in("dimension_item_id", dimensionItemIds));
        }

        String randomNumeric = RandomStringUtils.randomNumeric(6);
        dimensionItems.stream().forEach(e -> {
            e.setDeleted(true);
            e.setDimensionItemCode(e.getDimensionItemCode() + "_DELETED_" + randomNumeric);
            dimensionItemMapper.updateById(e);
        });
    }

    public List<DimensionItem> listDimensionItemsByIds(List<Long> dimensionItemIds) {
        if (dimensionItemIds == null || dimensionItemIds.size() == 0) {
            return new ArrayList<>();
        } else {
            return dimensionItemMapper.selectList(new EntityWrapper<DimensionItem>().in("id", dimensionItemIds));
        }
    }

    public List<DimensionItem> listDimensionItemsByDimensionIdAndEnabled(Long dimensionId, Boolean enabled, Long companyId) {

        List<DimensionItem> dimensionItemList = dimensionItemMapper.selectList(
                new EntityWrapper<DimensionItem>()
                        .eq("dimension_id", dimensionId)
                        .eq(enabled != null, "enabled",enabled)
                        .le("start_date", ZonedDateTime.now())
                        .andNew()
                        .isNull("end_date")
                        .or()
                        .ge("end_date",ZonedDateTime.now())
                        .orderBy("enabled", false)
                        .orderBy("dimension_item_code")
        );

        //根据已分配公司筛选维值
        if (companyId != null) {
            List<Long> dimensionItemIdList = dimensionItemAssignCompanyService.selectList(
                    new EntityWrapper<DimensionItemAssignCompany>().eq("company_id", companyId).eq("enabled", true)
            ).stream().map(DimensionItemAssignCompany::getDimensionItemId).collect(toList());

            if (dimensionItemIdList.size() != 0) {
                dimensionItemList = dimensionItemList.stream().filter(
                        dimensionItem -> dimensionItemIdList.contains(dimensionItem.getId())
                ).collect(toList());
            }
        }
        //根据员工筛选维值
        Long currentContactId = contactService.getContactByUserOid(OrgInformationUtil.getCurrentUserOid()).getId();
        List<Long> ids = dimensionItemMapper.listDimensionsByContactId(currentContactId)
                .stream().map(DimensionItem::getId).collect(toList());
        dimensionItemList = dimensionItemList.stream().filter(
                dimensionItem -> (ids.contains(dimensionItem.getId()) && VisibleUserScopeEnum.USER.getId().equals(dimensionItem.getVisibleUserScope())) || !VisibleUserScopeEnum.USER.getId().equals(dimensionItem.getVisibleUserScope())
        ).collect(toList());
        return dimensionItemList;
    }

    public List<DimensionItem> pageDimensionItemsByDimensionId(Long dimensionId,
                                                               Page page,
                                                               Boolean enabled,
                                                               String dimensionItemName,
                                                               String dimensionItemCode) {
        List<DimensionItem> dimensionItemList = dimensionItemMapper.selectPage(
                page,
                new EntityWrapper<DimensionItem>()
                        .eq("dimension_id", dimensionId)
                        .eq(enabled != null , "enabled", enabled)
                        .like(org.springframework.util.StringUtils.hasText(dimensionItemCode), "dimension_item_code", dimensionItemCode)
                        .like(org.springframework.util.StringUtils.hasText(dimensionItemName), "dimension_item_name", dimensionItemName)
                        .orderBy("enabled", false)
                        .orderBy("dimension_item_code")
        );
        return dimensionItemList;
    }


    public List<DimensionItem> pageDimensionItemsByDimensionIdEnabledCompanyId(Long dimensionId,Boolean enabled,Long companyId, Page page) {
        return baseMapper.pageDimensionItemsByDimensionIdEnabledCompanyId(dimensionId,enabled,companyId,page);
    }

    public List<DimensionDetailCO> listItemsByDimensionIdsAndEnabled(List<Long> dimensionIds,
                                                                     Boolean enabled,
                                                                     Long companyId) {
        if (CollectionUtils.isEmpty(dimensionIds)){
            return new ArrayList<>();
        }
        List<Dimension> dimensions = dimensionService.selectBatchIds(dimensionIds);
        Map<Long, Dimension> collect = dimensions.stream().collect(Collectors.toMap(Dimension::getId, e -> e));
        List<DimensionDetailCO> result = new ArrayList<>();
        dimensionIds.forEach(e -> {
            Dimension dimension = collect.get(e);
            DimensionDetailCO dto = new DimensionDetailCO();
            List<DimensionItem> dimensionItems = listDimensionItemsByDimensionIdAndEnabled(e, enabled, companyId);
            dto.setId(e);
            dto.setDimensionCode(dimension == null ? null : dimension.getDimensionCode());
            dto.setDimensionName(dimension == null ? null : dimension.getDimensionName());
            dto.setDimensionSequence(dimension == null ? null : dimension.getDimensionSequence());
            List<DimensionItemCO> dimensionItemCOS = dimensionItems.stream().map(item -> {
                DimensionItemCO itemCO = new DimensionItemCO();
                itemCO.setDimensionId(item.getDimensionId());
                itemCO.setDimensionItemCode(item.getDimensionItemCode());
                itemCO.setDimensionItemName(item.getDimensionItemName());
                itemCO.setEnabled(item.getEnabled());
                itemCO.setVisibleUserScope(item.getVisibleUserScope());
                itemCO.setId(item.getId());
                itemCO.setEnabled(item.getEnabled());
                return itemCO;
            }).collect(toList());
            dto.setSubDimensionItemCOS(dimensionItemCOS);
            result.add(dto);
        });
        return result;
    }

    /**
     * 项目申请单插入维度值
     * 由项目申请单审批完成时定义，根据参数定义中定义的维度代码，确认插入到哪个维度中。
     */
    public void proInsertDimensionItem(Long setOfBooksId, String parameterCode, String projectNumber,String projectName){
        List<Dimension> dimensionList = dimensionMapper.listDimensionsByParameterCode(setOfBooksId,parameterCode);
        dimensionList.stream().forEach(dimension ->{
            DimensionItemRequestDTO dto = new DimensionItemRequestDTO();
            DimensionItem dimensionItem = new DimensionItem();
            dimensionItem.setDimensionItemCode(projectNumber);
            dimensionItem.setDimensionItemName(projectName);
            dimensionItem.setDimensionId(dimension.getId());
            dimensionItem.setVisibleUserScope(1001);
            dimensionItem.setStartDate(ZonedDateTime.now());
            dto.setDimensionItem(dimensionItem);
            insertDimensionItem(dto);
            }
        );
    }
}


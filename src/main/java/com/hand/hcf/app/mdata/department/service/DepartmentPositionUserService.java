package com.hand.hcf.app.mdata.department.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.department.domain.Department;
import com.hand.hcf.app.mdata.department.domain.DepartmentPosition;
import com.hand.hcf.app.mdata.department.domain.DepartmentPositionUser;
import com.hand.hcf.app.mdata.department.domain.DepartmentRole;
import com.hand.hcf.app.mdata.department.domain.enums.DepartmentPositionCode;
import com.hand.hcf.app.mdata.department.domain.enums.DepartmentPositionUserImportCode;
import com.hand.hcf.app.mdata.department.dto.DepartmentPositionDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentPositionUserDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentPositionUserImportDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentRoleDTO;
import com.hand.hcf.app.mdata.department.persistence.DepartmentPositionUserMapper;
import com.hand.hcf.app.mdata.system.domain.BatchTransactionLog;
import com.hand.hcf.app.mdata.utils.FileUtil;
import com.itextpdf.text.io.StreamUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

@Service
@Transactional
public class DepartmentPositionUserService extends ServiceImpl<DepartmentPositionUserMapper, DepartmentPositionUser> {
    public static final Map<UUID, BatchTransactionLog> transactions = new HashMap<>();
    public static final Map<UUID, List<DepartmentPositionUserImportDTO>> departmentPositionUserImportMap = new HashMap<>();

    private final Logger log = LoggerFactory.getLogger(DepartmentPositionUserService.class);


    @Autowired
    private DepartmentPositionUserMapper departmentPositionUserMapper;
    @Autowired
    private DepartmentRoleService departmentRoleService;
    @Autowired
    private DepartmentPositionService departmentPositionService;

    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ContactService contactService;




    @Autowired
    @Qualifier("taskExecutor")
    private Executor executor;

    public UserDTO getUser(long departmentId, long positionId){
        Map<String, Object> map = new HashMap<>();
        map.put("department_id", departmentId);
        map.put("position_id", positionId);
        map.put("deleted",false);
        UserDTO user = null;
        List<DepartmentPositionUser> departmentPositionUserList = departmentPositionUserMapper.selectByMap(map);
        if(CollectionUtils.isNotEmpty(departmentPositionUserList)){
            user =contactService.getUserDTOByUserId(departmentPositionUserList.get(0).getUserId());
//            Optional<User> optional = userRepository.getById((departmentPositionUserList.get(0).getUserId()));
//            if(optional.isPresent()){
//                user = optional.get();
//            }
        }
        return user;
    }

    public void init() {
        FutureTask<Void> futureTask = new FutureTask<>(() -> {
            this.initDepartmentPositionUser(null);
            return null;
        });
        //ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(futureTask);
    }

    public void init(List<Long> departmentIds) {
        FutureTask<Void> futureTask = new FutureTask<>(() -> {
            this.initDepartmentPositionUser(departmentIds);
            return null;
        });
        //ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(futureTask);
    }

    private void initDepartmentPositionUser(List<Long> departmentIds) {
        long start = System.currentTimeMillis();
        List<DepartmentRole> departmentRoles = null;
        if(CollectionUtils.isNotEmpty(departmentIds)){
            departmentRoles = departmentRoleService.selectList(
                    new EntityWrapper<DepartmentRole>()
                            .in("department_id", departmentIds)
            );
        }else{
            departmentRoles = departmentRoleService.selectList(new EntityWrapper<>());
        }
        departmentRoles.stream().forEach(departmentRole -> {
            try {
                log.info("处理数据,departmentRoleId:[{}]",departmentRole.getId());
                Long departmentId = departmentRole.getDepartmentId();

                saveOrUpdateDepartmentPositionUser(departmentRole,departmentId);
            }catch (Exception e){
                log.info("初始化数据出错,departmentRoleID:[{}]",departmentRole.getId());
                log.error("初始化数据出错,错误信息:{}",e);
            }
        });
        log.info("修复部门角色结束，共耗时:{}秒",(System.currentTimeMillis()-start)/1000);
    }

    public void saveOrUpdateDepartmentPositionUser(DepartmentRole departmentRole, Long departmentId){
        Department department = departmentService.findOneByDepartmentId(departmentId);
        if(department == null){
            throw new RuntimeException("部门不存在,部门id:"+departmentId);
        }
        long tenantId = department.getTenantId();
        UserDTO user = null;
        for (int i = 0; i< DepartmentPositionCode.codes.length; i++){
            long positionId = departmentPositionService.getPostionId(tenantId, DepartmentPositionCode.codes[i]);

            long userId = getUserId(departmentRole, DepartmentPositionCode.codes[i], tenantId);
            if(userId <= 0){
                continue;
            }

            user = contactService.getUserDTOByUserId(userId);
            Map<String,Object> map = new HashedMap();
            map.put("department_id",departmentId);
            map.put("position_id",positionId);
            List<DepartmentPositionUser> queryList = departmentPositionUserMapper.selectByMap(map);
            DepartmentPositionUser departmentPositionUser = new DepartmentPositionUser();
            if(CollectionUtils.isNotEmpty(queryList)){
                departmentPositionUser = queryList.get(0);
                departmentPositionUser.setUserId(userId);
                departmentPositionUser.setLastUpdatedDate(ZonedDateTime.now());
                departmentPositionUserMapper.updateById(departmentPositionUser);
//                if(departmentPositionUser.getUserId() != userId){
//                    UUID currentUserOid = OrgInformationUtil.getCurrentUserOid();
//                    String fullName = contactService.findOne(user.getContactId()).getFullName();
//                    if(currentUserOid != null){
//                        dataOperationService.save(currentUserOid.toString(),
//                            messageTranslationService.getMessageDetailByCode(OrgInformationUtil.getCurrentLanguage(), DataOperationMessageKey.UPDATE_DEPARTMENT_POSITION_USER,DepartmentPositionCode.codes[i], fullName, fullName),
//                            OperationEntityTypeEnum.DEPARTMENT_POSITION_USER.getKey(), OperationTypeEnum.UPDATE.getKey(), tenantId);
//                    }else{
//                        String currentClientId = OrgInformationUtil.getCurrentClientId();
//                        if(currentClientId != null){
//                            dataOperationService.save(currentClientId,
//                                messageTranslationService.getMessageDetailByCode(OrgInformationUtil.getCurrentLanguage(), DataOperationMessageKey.UPDATE_DEPARTMENT_POSITION_USER,DepartmentPositionCode.codes[i],fullName,fullName),
//                                OperationEntityTypeEnum.DEPARTMENT_POSITION_USER.getKey(), OperationTypeEnum.UPDATE.getKey(), tenantId);
//                        }
//                    }
//                }
            }else{
                departmentPositionUser.setTenantId(tenantId);
                departmentPositionUser.setDepartmentId(departmentId);
                departmentPositionUser.setPositionId(positionId);
                departmentPositionUser.setUserId(userId);
                departmentPositionUser.setCreatedDate(ZonedDateTime.now());
                departmentPositionUserMapper.insert(departmentPositionUser);
//                UUID currentUserOid = OrgInformationUtil.getCurrentUserOid();
//                String fullName = contactService.findOne(user.getContactId()).getFullName();
//                if(currentUserOid != null){
//                    dataOperationService.save(currentUserOid.toString(),
//                        departmentPositionUser,
//                        messageTranslationService.getMessageDetailByCode(OrgInformationUtil.getCurrentLanguage(),DataOperationMessageKey.ADD_DEPARTMENT_POSITION_USER,DepartmentPositionCode.codes[i],fullName,fullName),
//                        OperationEntityTypeEnum.DEPARTMENT_POSITION_USER.getKey(), OperationTypeEnum.ADD.getKey(), tenantId);
//                }else{
//                    String currentClientId = OrgInformationUtil.getCurrentClientId();
//                    if(currentClientId != null){
//                        dataOperationService.save(currentClientId,
//                            departmentPositionUser,
//                            messageTranslationService.getMessageDetailByCode(OrgInformationUtil.getCurrentLanguage(),DataOperationMessageKey.ADD_DEPARTMENT_POSITION_USER,DepartmentPositionCode.codes[i],fullName,fullName),
//                            OperationEntityTypeEnum.DEPARTMENT_POSITION_USER.getKey(), OperationTypeEnum.ADD.getKey(), tenantId);
//                    }
//                }
            }
        }
    }

    public long getUserId(DepartmentRole departmentRole, String code, long tenantId) {
        UUID userOid = null;
        long userId = -1;
        switch (code){
            case "6101":
                //需要使用部门上的部门经理
                Department department=departmentService.findOneByDepartmentId(departmentRole.getDepartmentId());
                if(department!=null){
                    UserDTO manager=department.getManager();
                    if(manager!=null){
                        userOid = manager.getUserOid();
                    }
                }
                break;
            case "6102":
                userOid = departmentRole.getViceManagerOid();
                break;
            case "6103":
                userOid = departmentRole.getChargeManagerOid();
                break;
            case "6104":
                userOid = departmentRole.getDepartmentManagerOid();
                break;
            case "6105":
                userOid = departmentRole.getFinancialBPOid();
                break;
            case "6106":
                userOid = departmentRole.getFinancialDirectorOid();
                break;
            case "6107":
                userOid = departmentRole.getFinancialManagerOid();
                break;
            case "6108":
                userOid = departmentRole.getHrbpOid();
                break;
            case "6109":
                userOid = departmentRole.getVicePresidentOid();
                break;
            case "6110":
                userOid = departmentRole.getPresidentOid();
                break;
            case "6111":
                userOid = departmentRole.getLegalReviewOid();
                break;
            case "6112":
                userOid = departmentRole.getAdministrativeReviewOid();
                break;
            case "6113":
                userOid = departmentRole.getFinancialAPOid();
                break;
            default:
                break;
        }
        if(userOid != null){
            UserDTO user = contactService.getUserDTOByUserOid(userOid);
            if (user != null && user.getTenantId().equals(tenantId)) {
                userId = user.getId();
            }else{
                log.error("用户不存在，用户Oid:[{}]",userOid);
            }
        }
        return userId;
    }

    private DepartmentRole convert(DepartmentPositionUser positionUser, DepartmentRole departmentRole) {
        if(positionUser != null){
            departmentRole.setDepartmentId(positionUser.getDepartmentId());
            long positionId = positionUser.getPositionId();
            DepartmentPosition departmentPosition = departmentPositionService.selectById(positionId);
            if(departmentPosition != null){
                String code = departmentPosition.getPositionCode();
                Long userId = positionUser.getUserId();
                UserDTO user = contactService.getUserDTOByUserId(userId);
                if(user != null){
                    UUID userOid = user.getUserOid();
                    switch (code){
                        case "6101":
                            departmentRole.setManagerOid(userOid);
                            break;
                        case "6102":
                            departmentRole.setViceManagerOid(userOid);
                            break;
                        case "6103":
                            departmentRole.setChargeManagerOid(userOid);
                            break;
                        case "6104":
                            departmentRole.setDepartmentManagerOid(userOid);
                            break;
                        case "6105":
                            departmentRole.setFinancialBPOid(userOid);
                            break;
                        case "6106":
                            departmentRole.setFinancialDirectorOid(userOid);
                            break;
                        case "6107":
                            departmentRole.setFinancialManagerOid(userOid);
                            break;
                        case "6108":
                            departmentRole.setHrbpOid(userOid);
                            break;
                        case "6109":
                            departmentRole.setVicePresidentOid(userOid);
                            break;
                        case "6110":
                            departmentRole.setPresidentOid(userOid);
                            break;
                        case "6111":
                            departmentRole.setLegalReviewOid(userOid);
                            break;
                        case "6112":
                            departmentRole.setAdministrativeReviewOid(userOid);
                            break;
                        case "6113":
                            departmentRole.setFinancialAPOid(userOid);
                            break;
                        default:
                            break;
                    }
                }
            }

        }
        return departmentRole;
    }

    public DepartmentRole convertPositionUserToRole(DepartmentPositionUser positionUser, DepartmentRole departmentRole){
        departmentRole = convert(positionUser,departmentRole);
        return departmentRole;
    }

    public DepartmentRole convertPositionUserToRole(List<DepartmentPositionUser> positionUserList){
        DepartmentRole departmentRole = new DepartmentRole();
        if(CollectionUtils.isNotEmpty(positionUserList)){
            for (DepartmentPositionUser departmentPositionUser:positionUserList) {
                departmentRole = convertPositionUserToRole(departmentPositionUser,departmentRole);
            }
        }
        return departmentRole;
    }

    public DepartmentPositionUser getDepartmentPositionUser(long departmentId, long positionId){
        Map<String, Object> map = new HashMap<>();
        map.put("department_id", departmentId);
        map.put("position_id", positionId);
        map.put("deleted", false);
        List<DepartmentPositionUser> departmentPositionUserList = departmentPositionUserMapper.selectByMap(map);
        if(CollectionUtils.isNotEmpty(departmentPositionUserList)){
            return departmentPositionUserList.get(0);
        }else{
            return null;
        }
    }

    public void saveOrUpdate(List<DepartmentPositionDTO> departmentPositionDTOList, long departmentId, long tenantId, boolean isControl) {
        if (CollectionUtils.isEmpty(departmentPositionDTOList)) {
            return;
        }
        departmentPositionDTOList.stream().forEach(departmentPositionDTO -> {
            UUID userOid = departmentPositionDTO.getUserOid();
            if (userOid != null) {
                DepartmentPositionUser departmentPositionUser = this.getDepartmentPositionUser(departmentId, departmentPositionDTO.getId());
                if (departmentPositionUser != null) {
                    UserDTO user = contactService.getUserDTOByUserOid(userOid);
                    UserDTO originUser = contactService.getUserDTOByUserId(departmentPositionUser.getUserId());
                    if (departmentPositionUser.getUserId() != user.getId()) {
                        departmentPositionUser.setUserId(user.getId());
                        departmentPositionUserMapper.updateById(departmentPositionUser);
//                        if (isControl) {
//                            dataOperationService.save(OrgInformationUtil.getCurrentUserId().toString(),
//                                messageTranslationService.getMessageDetailByCode(OrgInformationUtil.getCurrentLanguage(),DataOperationMessageKey.UPDATE_DEPARTMENT_POSITION_USER,departmentPositionDTO.getPositionName(),contactService.findOne(originUser.getContactId()).getFullName(),contactService.findOne(user.getContactId()).getFullName()),
//                                OperationEntityTypeEnum.DEPARTMENT_POSITION_USER.getKey(), OperationTypeEnum.UPDATE.getKey(), tenantId);
//                        } else {
//                            dataOperationService.save(OrgInformationUtil.getCurrentClientId(),
//                                messageTranslationService.getMessageDetailByCode(OrgInformationUtil.getCurrentLanguage(),DataOperationMessageKey.UPDATE_DEPARTMENT_POSITION_USER,departmentPositionDTO.getPositionName(),contactService.findOne(originUser.getContactId()).getFullName(),contactService.findOne(user.getContactId()).getFullName()),
//                                OperationEntityTypeEnum.DEPARTMENT_POSITION_USER.getKey(), OperationTypeEnum.UPDATE.getKey(), tenantId);
//                        }
                    }

                } else {
                    departmentPositionUser = new DepartmentPositionUser();
                    UserDTO user = contactService.getUserDTOByUserOid(userOid);
                    if (user != null) {
                        departmentPositionUser.setUserId(user.getId());
                        departmentPositionUser.setTenantId(tenantId);
                        departmentPositionUser.setPositionId(departmentPositionDTO.getId());
                        departmentPositionUser.setDepartmentId(departmentId);
                        departmentPositionUserMapper.insert(departmentPositionUser);
//                        String fullName = contactService.findOne(user.getContactId()).getFullName();
//                        if (isControl) {
//                            dataOperationService.save(OrgInformationUtil.getCurrentUserId(),
//                                messageTranslationService.getMessageDetailByCode(OrgInformationUtil.getCurrentLanguage(),DataOperationMessageKey.ADD_DEPARTMENT_POSITION_USER,departmentPositionDTO.getPositionName(),fullName,fullName),
//                                OperationEntityTypeEnum.DEPARTMENT_POSITION_USER.getKey(), OperationTypeEnum.ADD.getKey(), tenantId);
//                        } else {
//                            dataOperationService.save( OrgInformationUtil.getCurrentClientId(),
//                                messageTranslationService.getMessageDetailByCode(OrgInformationUtil.getCurrentLanguage(),DataOperationMessageKey.ADD_DEPARTMENT_POSITION_USER,departmentPositionDTO.getPositionName(),fullName,fullName),
//                                OperationEntityTypeEnum.DEPARTMENT_POSITION_USER.getKey(), OperationTypeEnum.ADD.getKey(), tenantId);
//                        }
                    }
                }
            } else {
                DepartmentPositionUser departmentPositionUser = this.getDepartmentPositionUser(departmentId, departmentPositionDTO.getId());
                if (null != departmentPositionUser) {
                    departmentPositionUser.setDeleted(true);
                    departmentPositionUserMapper.updateById(departmentPositionUser);
                }
            }
        });
    }

    public UserDTO getUser(long tenantId, long departmentId, String positionCode){
        Long userId = departmentPositionUserMapper.selectUser(tenantId,departmentId,positionCode);
        if(userId != null && userId > 0){
            return contactService.getUserDTOByUserId(userId);
        }else{
            return null;
        }
    }

    public UserDTO getUser(String departmentOid, String positionCode) {
//        Department department=departmentService.getByDepartmentOid(UUID.fromString(departmentOid));

        Long userId = departmentPositionUserMapper.selectUserByDepartmentOidAndPositionCode(departmentOid,positionCode);
        if(userId != null && userId > 0){
            return contactService.getUserDTOByUserId(userId);
        }else{
            return null;
        }
    }

    public DepartmentRoleDTO convert(List<DepartmentPositionDTO> positionUsers, UUID departmentOid) {
        DepartmentRoleDTO departmentRoleDTO = new DepartmentRoleDTO();
        if(positionUsers != null){
            departmentRoleDTO.setDepartmentOid(departmentOid);
            positionUsers.stream().forEach(positionUser->{
                String code = positionUser.getPositionCode();
                UUID userOid = positionUser.getUserOid();
                switch (code){
                    case "6101":
                        departmentRoleDTO.setManagerOid(userOid);
                        break;
                    case "6102":
                        departmentRoleDTO.setViceManagerOid(userOid);
                        break;
                    case "6103":
                        departmentRoleDTO.setChargeManagerOid(userOid);
                        break;
                    case "6104":
                        departmentRoleDTO.setDepartmentManagerOid(userOid);
                        break;
                    case "6105":
                        departmentRoleDTO.setFinancialBPOid(userOid);
                        break;
                    case "6106":
                        departmentRoleDTO.setFinancialDirectorOid(userOid);
                        break;
                    case "6107":
                        departmentRoleDTO.setFinancialManagerOid(userOid);
                        break;
                    case "6108":
                        departmentRoleDTO.setHrbpOid(userOid);
                        break;
                    case "6109":
                        departmentRoleDTO.setVicePresidentOid(userOid);
                        break;
                    case "6110":
                        departmentRoleDTO.setPresidentOid(userOid);
                        break;
                    case "6111":
                        departmentRoleDTO.setLegalReviewOid(userOid);
                        break;
                    case "6112":
                        departmentRoleDTO.setAdministrativeReviewOid(userOid);
                        break;
                    case "6113":
                        departmentRoleDTO.setFinancialAPOid(userOid);
                        break;
                    default:
                        break;
                }
            });
        }
        return departmentRoleDTO;
    }

    /**
     * 根据租户id和部门状态查询部门角色用户信息
     * @param tenantId：租户id
     * @param departmentStatus：部门状态
     * @return
     */
    public List<DepartmentPositionUserDTO> findByTenantIdAndDepartmentStatus(Long tenantId, Integer departmentStatus){
        return departmentPositionUserMapper.selectByTenantIdAndDepartmentStatus(tenantId,departmentStatus);
    }

    /**
     * 下载部门角色模板
     * @param tenantId：租户id
     * @param language：语言
     * @return
     */
    public byte[] downloadDepartmentPositionUserTemplate(Long tenantId,String language){
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            InputStream inputStream = StreamUtil.getResourceStream(FileUtil.getTemplatePath(DepartmentPositionUserImportCode.DEPARTMENT_POSITION_USER_TEMPLATE,language));
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
            Cell cell = null;
            Row row = sheet.getRow(DepartmentPositionUserImportCode.EXCEL_BASEROW - 1);
            List<DepartmentPosition> departmentPositions = departmentPositionService.listByTenantIdAndEnabled(tenantId,true);
            for(int i = 0;i < departmentPositions.size();i++){
                cell = row.createCell(DepartmentPositionUserImportCode.DEPARTMENT_NAME + 1 + i);
                cell.setCellValue(departmentPositions.get(i).getPositionName());
            }
            xssfWorkbook.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {

                }
            }
        }
        return null;
    }

    /**
     * 导入部门角色用户信息
     * @param inputStream：文件流
     * @param tenantId：租户id
     * @param currentUserOid：当前登录用户Oid
     * @return
     */
    @Transactional
    public UUID importDepartmentPositionUser(InputStream inputStream,Long tenantId,UUID currentUserOid){
        log.info("Start Importing Department Position User");
        //to do
        return null;
    }

}

package com.hand.hcf.app.mdata.implement.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.contact.web.adapter.UserAdapter;
import com.hand.hcf.app.mdata.department.domain.DepartmentPosition;
import com.hand.hcf.app.mdata.department.domain.DepartmentGroupDepartmentCO;
import com.hand.hcf.app.mdata.department.service.*;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class DepartmentControllerImpl /*implements DepartmentInterface */{

    @Autowired
    private MapperFacade mapper;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private DepartmentGroupService departmentGroupService;

    @Autowired
    private DepartmentUserService departmentUserService;

    @Autowired
    private DepartmentPositionUserService departmentPositionUserService;

    @Autowired
    private DepartmentPositionService departmentPositionService;

    @Autowired
    private DepartmentRoleService departmentRoleService;

    /**
     * 通过部门id获取部门信息
     *
     * @param id 部门id
     * @return
     */
    /*@Override*/
    public DepartmentCO getDepartmentById(@PathVariable("id") Long id) {
        return departmentService.getDepartmentById(id);
    }

    /**
     * 根据部门id查询部门所属的部门组
     *
     * @param id 部门id
     * @return
     */
    /*@Override*/
    public List<DepartmentGroupCO> listByDepartmentIdAndGroupStatus(@PathVariable("id") Long id) {
        return departmentGroupService.listByDepartmentIdAndGroupStatus(id);
    }

    /**
     * 根据部门组id查询部门组下的部门
     *
     * @param groupId 部门组id
     * @return
     */
    /*@Override*/
    public List<DepartmentGroupDepartmentCO> listDepartmentBydepartmentGroupId(@PathVariable("groupId") Long groupId) {
        return departmentGroupService.listDepartmentBydepartmentGroupId(groupId);
    }

    /**
     * 根据当前租户下部门code查询部门信息
     *
     * @param code 部门code
     * @return
     */
   /* @Override*/
    public DepartmentCO getDepartmentByCodeAndTenantId(@RequestParam("code") String code) {
        return departmentService.getDepartmentByCodeAndTenantId(code);
    }

    /**
     * 获取当前租户下，所有部门组及部门组对应部门ID的信息
     *
     * @return
     */
   /* @Override*/
    public List<DepartmentGroupCO> listDepartmentGroupInfoByTenantId() {
        return departmentGroupService.listDepartmentsByTenantId();
    }

    /**
     * 获取指定部门组id集合下，所有部门组及部门组对应部门ID的信息
     *
     * @param groupIds
     * @return
     */
  /*  @Override*/
    public List<DepartmentGroupCO> listDepartmentsByGroupIds(@RequestBody List<Long> groupIds) {
        return departmentGroupService.listDepartmentsByGroupIds(groupIds);
    }

    /**
     * 根据部门id集合和关键字，获取部门id,部门name
     *
     * @param ids     部门id集合
     * @param keyWord 关键字
     * @return
     */
   /* @Override*/
    public List<DepartmentCO> listDepartmentsByIds(@RequestBody List<Long> ids,
                                                   @RequestParam(value = "keyWord", required = false) String keyWord) {
        return departmentService.listDepartmentsByIds(ids, keyWord);
    }

    /**
     * 根据部门id集合，获取部门信息
     *
     * @param ids 部门id集合
     * @return
     */
  /*  @Override*/
    public List<DepartmentCO> listPathByIds(@RequestBody List<Long> ids) {
        return departmentService.listPathByIds(ids);
    }

    /**
     * 根据员工oid查员工所属部门
     *
     * @param empOid 员工oid
     * @return
     */
    /*@Override*/
    public DepartmentCO getDepartmentByEmpOid(@PathVariable("empOid") String empOid) {
        return departmentService.getDepartmentByEmpOid(empOid);
    }

    /**
     * 查询租户下，公司id为传值的部门集合
     *
     * @param companyId 公司id 条件查询
     * @param deptCode  部门code 条件查询
     * @param deptName  部门名称 条件查询
     * @param page      每页多少条
     * @param size      每页大小
     * @return
     */
  /*  @Override*/
    public Page<DepartmentCO> pageDepartmentByCompanyIdAndTenantId(@RequestParam(required = false, value = "companyId") Long companyId,
                                                                   @RequestParam(required = false, value = "deptCode") String deptCode,
                                                                   @RequestParam(required = false, value = "deptName") String deptName,
                                                                   @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                   @RequestParam(value = "size", required = false, defaultValue = "10") int size) {


        Page<DepartmentCO> mybatisPage = PageUtil.getPage(page, size);
        return departmentService.pageDepartmentByCompanyIdAndTenantId(companyId,
                deptCode,
                deptName,
                mybatisPage);
    }

    /**
     * 获取某个部门的子部门ID
     *
     * @param id 部门id
     * @return
     */
   /* @Override*/
    public Set<Long> listDepartmentChildrenIdById(@RequestParam("id") Long id) {
        return departmentService.listDepartmentChildrenIdById(id);
    }

    /**
     * 获取下属部门信息(不包含自己)
     *
     * @param id      部门id
     * @param keyWord 关键字 条件查询
     * @return
     */
    /*@Override*/
    public List<DepartmentCO> listDepartmentChildrenById(@RequestParam("id") Long id,
                                                         @RequestParam(value = "keyWord", required = false) String keyWord) {
        return departmentService.listDepartmentChildrenAndOwnById(id, keyWord, false);
    }


    /**
     * 获取下属部门信息(不包含自己) - 分页
     *
     * @param id      部门id
     * @param keyWord 关键字 条件查询
     * @param page    每页多少条
     * @param size    每页大小
     * @return
     */
    /*@Override*/
    public Page<DepartmentCO> pageDepartmentChildrenById(@RequestParam("id") Long id,
                                                         @RequestParam(value = "keyWord", required = false) String keyWord,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                         @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page<DepartmentCO> mybatisPage = new Page<>(page + 1, size);
        return departmentService.pageDepartmentChildrenById(id, keyWord, false, mybatisPage);
    }

    /**
     * 获取下属部门及本部门信息
     *
     * @param id      部门id
     * @param keyWord 关键字 条件查询
     * @return
     */
   /* @Override*/
    public List<DepartmentCO> listDepartmentChildrenAndOwnById(@RequestParam("id") Long id,
                                                               @RequestParam(value = "keyWord", required = false) String keyWord) {
        return departmentService.listDepartmentChildrenAndOwnById(id, keyWord, true);
    }

    /**
     * 获取下属部门及本部门信息 - 分页
     *
     * @param id      部门id
     * @param keyWord 关键字 条件查询
     * @param page    每页多少条
     * @param size    每页大小
     * @return
     */
   /* @Override*/
    public Page<DepartmentCO> pageDepartmentChildrenAndOwnById(@RequestParam("id") Long id,
                                                               @RequestParam(value = "keyWord", required = false) String keyWord,
                                                               @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                               @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page<DepartmentCO> mybatisPage = new Page<>(page + 1, size);
        return departmentService.pageDepartmentChildrenById(id, keyWord, true, mybatisPage);
    }

    /**
     * 根据当前租户下的部门信息
     *
     * @param keyWord 关键字 条件查询
     * @param page
     * @return
     */
    /*@Override*/
    public Page<DepartmentCO> pageDepartmentInfoByTenantId(@RequestParam(value = "keyWord", required = false) String keyWord,
                                                           @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                           @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page<DepartmentCO> mybatisPage = new Page<>(page + 1, size);
        return departmentService.pageDepartmentInfoByTenantId(keyWord, mybatisPage);
    }

    /**
     * 根据部门Oid查询部门信息
     *
     * @param oid 部门Oid
     * @return
     */
    /*@Override*/
    public DepartmentCO getDepartmentByOid(@PathVariable("oid") String oid) {
        return departmentService.getDepartmentByOid(oid);
    }

    /**
     * 根据部门id集合获取list部门信息
     *
     * @param ids     部门id集合
     * @param keyWord 关键字 条件查询
     * @param page    每页多少条
     * @param size    每页大小
     * @return
     */
    /*@Override*/
    public Page<DepartmentCO> pageDepartmentsByIdsResultPage(@RequestBody List<Long> ids,
                                                             @RequestParam(value = "keyWord", required = false) String keyWord,
                                                             @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                             @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page<DepartmentCO> mybatisPage = new Page<>(page + 1, size);
        return departmentService.pageDepartmentsByIdsResultPage(ids, keyWord, mybatisPage);
    }

    /**
     * 查询当前租户下的部门
     *
     * @param enabled true则status=101，false则status=102，空则status!=103 条件查询
     * @return
     */
   /* @Override*/
    public List<DepartmentCO> listDepartmentByStatus(@RequestParam(value = "enabled", required = false) Boolean enabled) {
        return departmentService.listDepartmentByStatus(enabled);
    }

    /**
     * 根据员工ID查询部门信息
     *
     * @param empId
     * @return
     */
 /*   @Override*/
    public DepartmentCO getDepartmentByEmployeeId(@PathVariable("empId") Long empId) {
        return departmentService.getDepartmentByEmployeeId(empId);
    }

    /**
     * 根据用户oid获取用户最大的部门主管
     *
     * @param userOid
     * @param isContainPeerLevel
     * @return
     */
   /* @Override*/
    public UUID getLastDepartmentManagerByUserOid(@RequestParam("userOid") UUID userOid,
                                                  @RequestParam("isContainPeerLevel") Boolean isContainPeerLevel) {

        return departmentService.getLastDepartmentManagerByUserOid(userOid, isContainPeerLevel);
    }

    /**
     * 根据部门oid获取所有层级部门
     *
     * @param departmentOid
     * @param userOid
     * @return
     */
    /*@Override*/
    public HashMap<Integer, DepartmentCO> listAllDepartmentByOid(@RequestParam("departmentOid") UUID departmentOid,
                                                                 @RequestParam("userOid") UUID userOid) {
        return departmentService.listAllDepartmentByOidAndUserOid(departmentOid, userOid);
    }

    /**
     * 根据部门oid列表及层级获取部门列表
     *
     * @param departmentOids
     * @param departmentLevel
     * @return
     */
    /*@Override*/
    public List<DepartmentCO> listByOidsAndLevel(@RequestBody List<UUID> departmentOids,
                                                 @RequestParam("departmentLevel") Integer departmentLevel) {
        return departmentService.listByOidsAndLevel(departmentOids, departmentLevel);
    }

    /**
     * 根据部门oid列表及层级获取部门列表
     *
     * @param userOid
     * @param departmentOid
     * @param departmentLevel
     * @return
     */
    /*@Override*/
    public List<String> listDepartmentPath(@RequestParam("userOid") UUID userOid,
                                           @RequestParam("departmentOid") UUID departmentOid,
                                           @RequestParam("departmentLevel") int departmentLevel) {
        return departmentService.listtDepartmentPath(userOid, departmentOid, departmentLevel);
    }



    /**
     * 根据部门id获取部门主管
     *
     * @param departmentId
     * @return
     */
    /*@Override*/
    public ContactCO getDepartmentManager(@RequestParam("departmentId") Long departmentId) {
        return UserAdapter.getUserCOByUserDTO(departmentUserService.getDepartmentManager(departmentId));
    }

    /**
     * 根据部门oid和职位代码获取用户
     *
     * @param departmentOid
     * @param positionCode
     * @return
     */
    /*@Override*/
    public ContactCO getUserByDeparmentOidAndPosition(@RequestParam("departmentOid") UUID departmentOid,
                                                   @RequestParam("positionCode") String positionCode) {
        return UserAdapter.getUserCOByUserDTO(departmentPositionUserService.getUser(departmentOid.toString(), positionCode));
    }

    /**
     * 根据公司oid等条件获取部门职位
     *
     * @param companyOid
     * @param departmentId
     * @param userOid
     * @param tenantId
     * @return
     */
   /* @Override*/
    public List<DepartmentPositionCO> listDepartmentPosition(
            @RequestParam(value = "companyOid",required = false) UUID companyOid,
            @RequestParam(value = "departmentId",required = false) Long departmentId,
            @RequestParam(value = "userOid",required = false) UUID userOid,
            @RequestParam(value = "tenantId",required = false) Long tenantId) {
        List<DepartmentPosition> lists = null;
        if (companyOid != null) {
            lists = departmentPositionService.listByCompanyOid(companyOid);
        }
        if (departmentId != null) {
            lists = departmentPositionService.listByUserAndDepartment(departmentId, userOid);
        }
        if (tenantId != null) {
            lists = departmentPositionService.listByTenantId(tenantId);
        }

        if (lists != null && lists.size() > 0) {
            return lists.stream().map(p -> departmentPositionService.toCO(p))
                    .collect(Collectors.toList());
        }
        return null;
    }


    /**
     * 根据部门id获取部门角色
     *
     * @param departmentId
     * @return
     */
    /*@Override*/
    public DepartmentRoleCO getDepartmentRoleById(@RequestParam("departmentId") Long departmentId) {
        return mapper.map(departmentRoleService.getDepartmentRole(departmentId), DepartmentRoleCO.class);
    }

    /**
     * 查询当前租户下，启用的部门（条件：部门代码、部门名称、部门代码从、部门代码至）分页
     *
     * @param deptCode  部门code 条件查询
     * @param deptName  部门名称 条件查询
     * @param deptCodeFrom  部门code 条件查询
     * @param deptCodeTo  部门code 条件查询
     * @param page      每页多少条
     * @param size      每页大小
     * @return
     */
    /*@Override*/
    public Page<DepartmentCO> pageDepartmentByTenantId(@RequestParam(required = false, value = "deptCode") String deptCode,
                                                       @RequestParam(required = false, value = "deptName") String deptName,
                                                       @RequestParam(required = false, value = "deptCodeFrom") String deptCodeFrom,
                                                       @RequestParam(required = false, value = "deptCodeTo") String deptCodeTo,
                                                       @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                       @RequestParam(value = "size", required = false, defaultValue = "10") int size) {


        Page<DepartmentCO> mybatisPage = PageUtil.getPage(page, size);
        return departmentService.pageDepartmentByTenantId(deptCode,
                deptName,
                deptCodeFrom,
                deptCodeTo,
                mybatisPage);
    }

    /**
     * 查询当前租户下，启用的部门（条件：部门代码、部门名称、部门代码从、部门代码至）不分页
     *
     * @param deptCode  部门code 条件查询
     * @param deptName  部门名称 条件查询
     * @return
     */
   /* @Override*/
    public List<DepartmentCO> listDepartmentByTenantId(@RequestParam(required = false, value = "deptCode") String deptCode,
                                                       @RequestParam(required = false, value = "deptName") String deptName,
                                                       @RequestParam(required = false, value = "deptCodeFrom") String deptCodeFrom,
                                                       @RequestParam(required = false, value = "deptCodeTo") String deptCodeTo) {
        return departmentService.listDepartmentByTenantId(deptCode,
                deptName,
                deptCodeFrom,
                deptCodeTo);
    }

    /**
     * 条件查询租户下的部门信息 - 分页
     * @param departmentCode
     * @param codeFrom
     * @param codeTo
     * @param name
     * @param ids
     * @param keyWord
     * @param page
     * @param size
     * @return
     */
    /*@Override*/
    public Page<DepartmentCO> pageDepartmentsByCond(@RequestParam(value = "departmentCode", required = false) String departmentCode,
                                                    @RequestParam(value = "codeFrom", required = false) String codeFrom,
                                                    @RequestParam(value = "codeTo", required = false) String codeTo,
                                                    @RequestParam(value = "name", required = false) String name,
                                                    @RequestBody(required = false) List<Long> ids,
                                                    @RequestParam(value = "keyWord", required = false) String keyWord,
                                                    @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                    @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page<DepartmentCO> mybatisPage = PageUtil.getPage(page, size);
        return departmentService.pageDepartmentsByCond(departmentCode,codeFrom,codeTo,name,ids,keyWord,mybatisPage);
    }

    /**
     * 条件查询租户下的部门信息
     * @param departmentCode
     * @param codeFrom
     * @param codeTo
     * @param name
     * @param ids
     * @param keyWord
     * @return
     */
    /*@Override*/
    public List<DepartmentCO> listDepartmentsByCond(@RequestParam(value = "departmentCode", required = false) String departmentCode,
                                                    @RequestParam(value = "codeFrom", required = false) String codeFrom,
                                                    @RequestParam(value = "codeTo", required = false) String codeTo,
                                                    @RequestParam(value = "name", required = false) String name,
                                                    @RequestBody(required = false) List<Long> ids,
                                                    @RequestParam(value = "keyWord", required = false) String keyWord) {
        return departmentService.listDepartmentsByCond(departmentCode,codeFrom,codeTo,name,ids,keyWord);
    }
}

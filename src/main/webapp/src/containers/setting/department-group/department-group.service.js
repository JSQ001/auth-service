import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  //条件查询部门组（分页）
  getDeptGroupByOptions(params) {
    return httpFetch.get(`${config.mdataUrl}/api/DepartmentGroup/selectByInput`, params);
  },

  //根据id删除部门组
  deleteDeptGroupById(params) {
    return httpFetch.delete(
      `${config.mdataUrl}/api/DepartmentGroupDetail/BatchDeleteByIds`,
      params
    );
  },

  //根据id查询部门组信息
  getDeptGroupById(params) {
    return httpFetch.get(`${config.mdataUrl}/api/DepartmentGroup/selectById?id=${params}`);
  },

  //新增/更新部门组
  addOrUpdateDeptGroup(params) {
    return httpFetch.post(`${config.mdataUrl}/api/DepartmentGroup/insertOrUpdate`, params);
  },

  //查询部门组详情
  getDeptGroupInfo(params) {
    return httpFetch.get(
      `${config.mdataUrl}/api/DepartmentGroup/selectDepartmentByGroupId`,
      params
    );
  },

  //添加部门
  addDept(params) {
    return httpFetch.post(
      `${config.mdataUrl}/api/DepartmentGroupDetail/BatchAddDepartmentGroupDetail`,
      params
    );
  },
};

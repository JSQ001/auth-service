import httpFetch from '../../share/httpFetch';
import config from 'config';

export default {
  /**
   * 确认导入
   * @param {*} transactionId
   */
  confirmImporter(transactionId) {
    return httpFetch.post(`${config.mdataUrl}/api/user/import/new/confirm/${transactionId}`);
  },
  /**
   * 确认导出
   * @param {*} params
   */
  confirmExport(url, params) {
    return httpFetch.post(`${config.baseUrl}${url}`, params, {}, { responseType: 'arraybuffer' });
  },
  /**
   * 获取数据
   * @param {*} params
   */
  getRolesDistribute(params) {
    return httpFetch.get(`${config.baseUrl}api/userRole/query/roles/dataAuthority`, params);
  },
  /**
   * 新增角色权限分配
   * @param {*} params
   */
  saveRolesAuthority(params) {
    return httpFetch.post(`${config.baseUrl}/api/userRole/create`, params);
  },
  /**
   * 批量新增角色权限分配
   * @param {*} params
   */
  batchSaveRolesAuthority(params) {
    return httpFetch.post(`${config.baseUrl}/api/userRole/batch/create`, params);
  },
  /**
   * 修改角色权限分配
   * @param {*} params
   */
  updateRolesAuthority(params) {
    return httpFetch.put(`${config.baseUrl}/api/userRole/update`, params);
  },
  /**
   * 批量修改角色权限分配
   * @param {*} params
   */
  batchUpdateRolesAuthority(params) {
    return httpFetch.put(`${config.baseUrl}/api/userRole/batch/update`, params);
  },
  /**
   * 删除角色权限
   * @param {*} id
   */
  deleteRolesAuthority(id) {
    return httpFetch.delete(`${config.baseUrl}/api/userRole/delete/${id}`);
  },
  /**
   * 获取所有角色
   * @param {*}
   */
  getAllRoles() {
    return httpFetch.get(`${config.baseUrl}/api/userRole/query/all/roles`);
  },
  /**
   * 获取所有数据权限
   * @param {*}
   */
  getDataAuthority() {
    return httpFetch.get(`${config.mdataUrl}/api/system/data/authority/query/all/data/authority`);
  },
};

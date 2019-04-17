import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取列表
   * @param {*} params
   */
  getResponsibilityList(params) {
    return httpFetch.get(
      `${config.mdataUrl}/api/department/sob/resiponsibility/query/by/departmentId`,
      params
    );
  },
  /**
   * 获取责任中心
   */
  getResponsibleCenter(params) {
    return httpFetch.get(`${config.mdataUrl}/api/responsibilityCenter/query/default`, params);
  },

  /**
   * 获取账套
   * @param {*} params
   */
  getSetOfBooksList(params) {
    return httpFetch.get(`${config.mdataUrl}/api/department/sob/responsibility/query`, params);
  },

  /**
   * 新增修改配置责任配置
   * @param {*} params
   */
  addResponsibility(params) {
    return httpFetch.post(
      `${config.mdataUrl}/api/department/sob/responsibility/insertOrUpdate`,
      params
    );
  },

  /**
   * 获取组织架构详情
   * @param {*} id
   */
  getDimensionDetail(id) {
    return httpFetch.get(`${config.mdataUrl}/api/departments/${id}`);
  },

  /**
   * 根据id获得信息
   * @param {*} id
   */
  getById(id) {
    return httpFetch.get(`${config.mdataUrl}/api/department/sob/responsibility/${id}`);
  },
};

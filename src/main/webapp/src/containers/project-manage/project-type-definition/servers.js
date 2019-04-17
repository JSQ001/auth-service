import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 新增类型定义
   * @param {*} params
   */
  addProjectType(params) {
    return httpFetch.post(`${config.contractUrl}/api/project/requisition/type`, params);
  },
  /**
   * 更新类型定义
   * @param {*} params
   */
  updateProjectType(params) {
    return httpFetch.put(`${config.contractUrl}/api/project/requisition/type`, params);
  },
  /**
   * 获取关联审批流
   */
  getFormId(params) {
    return httpFetch.get(
      `${config.workflowUrl}/api/custom/forms/setOfBooks/my/available/all`,
      params
    );
  },
  /**
   * 分配公司页面 获取基本信息
   * @param {*} params
   */
  getInfoData(params) {
    return httpFetch.get(`${config.contractUrl}/api/project/requisition/type/${params}`);
  },
  /**
   * 分配公司
   * @param {*} params
   */
  distributeCompany(params, id) {
    return httpFetch.post(
      `${config.contractUrl}/api/project/requisition/type/company?requisitionTypeId=${id}`,
      params
    );
  },
  /**
   * 修改启用状态
   * @param {*} params
   */
  editStatus(params) {
    return httpFetch.put(
      `${config.contractUrl}/api/project/requisition/type/company/update/status?id=${
        params.id
      }&enabled=${!params.enabled}`
    );
  },
};

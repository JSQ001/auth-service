import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 根据id获得详情
   * @param {*} id
   */
  getInfoById(id) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/application/type/query/${id}`);
  },
  /**
   * 获取分配的公司
   * @param {*} id
   */
  getDistributiveCompany(id) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/application/type/${id}/company/query`);
  },

  /**
   * 更改公司分配状态
   * @param {*} parmas
   */
  updateAssignCompany(parmas) {
    return httpFetch.put(
      `${config.expenseUrl}/api/expense/application/type/assign/company`,
      parmas
    );
  },

  /**
   * 批量分配公司
   * @param {*} parmas
   */
  batchDistributeCompany(id, parmas) {
    return httpFetch.post(
      `${config.expenseUrl}/api/expense/application/type/${id}/assign/company`,
      parmas
    );
  },

  /**
   * 获取维度列表
   * @param {*} id
   */
  getDimensionById(id, params = {}) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/application/type/${id}/dimension/query`,
      params
    );
  },

  /**
   * 获取可关联表单类型
   * @param {*} setOfBooksId
   */
  getFormList(setOfBooksId) {
    return httpFetch.get(
      `${
        config.workflowUrl
      }/api/custom/forms/setOfBooks/my/available/all?formTypeId=801009&setOfBooksId=${setOfBooksId}`
    );
  },

  /**
   * 新建申请单类型
   * @param {*} params
   */
  addApplicationType(params) {
    return httpFetch.post(`${config.expenseUrl}/api/expense/application/type`, params);
  },

  /**
   * 更新申请单类型
   * @param {*} params
   */
  updateApplicationType(params) {
    return httpFetch.put(`${config.expenseUrl}/api/expense/application/type`, params);
  },
  /**
   * 获取未分配的维度
   * @param {*} params
   */
  getUndistributedDimensionList(id, params) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/application/type/${id}/dimensions/query/filter`,
      params
    );
  },
  /**
   * 获取维度
   * @param {*} params
   */
  getDimensionList(params) {
    return httpFetch.get(`${config.mdataUrl}/api/dimension/page/by/cond`, params);
  },
  /**
   * 获取维度下的维值
   * @param {*} params
   */
  getDimensionValueList(dimensionId) {
    // return httpFetch.get(`${config.mdataUrl}/api/dimension/item/page/by/cond`,params);
    return httpFetch.get(
      `${
        config.mdataUrl
      }/api/dimension/item/list/By/dimensionId/enabled?dimensionId=${dimensionId}&enabled=true`
    );
  },
  /**
   * 保存维度
   */
  saveDimensionValue(id, params) {
    return httpFetch.post(
      `${config.expenseUrl}/api/expense/application/type/${id}/assign/dimension`,
      params
    );
  },
  /**
   * 删除维度
   */
  deleteDimensionValue(id) {
    return httpFetch.delete(`${config.expenseUrl}/api/expense/application/type/dimension/${id}`);
  },
  /**
   * 修改维度
   */
  editDimensionValue(id, params) {
    return httpFetch.post(
      `${config.expenseUrl}/api/expense/application/type/${id}/assign/dimension`,
      params
    );
  },
};

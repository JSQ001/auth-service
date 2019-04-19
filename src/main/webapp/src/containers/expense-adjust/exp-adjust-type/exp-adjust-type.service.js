import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取关联表单类型
   */
  getRequisitionList(setOfBooksId) {
    return httpFetch.get(
      `${
        config.workflowUrl
      }/api/custom/forms/setOfBooks/my/available/all?formTypeId=801006&setOfBooksId=${setOfBooksId}`
    );
  },
  /**
   * 获取费用类型
   */
  getExpenseType(params) {
    let url = `${config.expenseUrl}/api/expense/types/query/by/document/assign`;
    return httpFetch.get(url, params);
  },
  /**
   * 根据id查询费用调整单类型
   */
  getExpenseAdjustTypeById(typeId) {
    let url = `${config.expenseUrl}/api/expense/adjust/types/${typeId}`;
    return httpFetch.get(url);
  },
  /**
   * 分页查询费用调整单类型定义下已经分配的公司
   */
  getDistributiveCompany(params) {
    let url = `${config.expenseUrl}/api/expense/adjust/type/assign/companies/query`;
    return httpFetch.get(url, params);
  },

  /**
   * 批量新增费用调整单类型定义之公司分配
   */
  batchAssignCompany(params) {
    let url = `${config.expenseUrl}/api/expense/adjust/type/assign/companies/batch`;
    return httpFetch.post(url, params);
  },
  /**
   * 批量修改费用调整单类型之分配公司
   */
  batchUpdateAssignCompany(params) {
    let url = `${config.expenseUrl}/api/expense/adjust/type/assign/companies/batch`;
    return httpFetch.put(url, params);
  },
  /**
   * 根据id删除费用调整单类型
   */
  deleteExpAdjustTypeById(typeId) {
    let url = `${config.expenseUrl}/api/expense/adjust/types/${typeId}`;
    return httpFetch.delete(url);
  },
  /**
   * 保存或者更新费用调整单类型定义行数据
   */
  saveExpAdjustTypeLineData(putOrPost, params) {
    return httpFetch[putOrPost](`${config.expenseUrl}/api/expense/adjust/types`, params);
  },
  /**
   * 修改预算管控和核算
   */
  updateBudgetorAccountFlag(id, accountFlag, budgetFlag) {
    return httpFetch.put(
      `${
        config.expenseUrl
      }/api/expense/adjust/types/update/budget/or/account?id=${id}&budgetFlag=${budgetFlag}&accountFlag=${accountFlag}`
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
   * 获取维度列表
   * @param {*} id
   */
  getDimensionById(id, params = {}) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/adjust/types/${id}/dimension/query`,
      params
    );
  },
  /**
   * 获取未分配的维度
   * @param {*} params
   */
  getUndistributedDimensionList(id, params) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/adjust/types/${id}/dimensions/query/filter`,
      params
    );
  },
  /**
   * 获取维度下的维值
   * @param {*} params
   */
  getDimensionValueList(dimensionId) {
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
      `${config.expenseUrl}/api/expense/adjust/types/${id}/assign/dimension`,
      params
    );
  },
  /**
   * 删除维度
   */
  deleteDimensionValue(id) {
    return httpFetch.delete(`${config.expenseUrl}/api/expense/adjust/types/dimension/${id}`);
  },
  /**
   * 修改维度
   */
  editDimensionValue(id, params) {
    return httpFetch.post(
      `${config.expenseUrl}/api/expense/adjust/types/${id}/assign/dimension`,
      params
    );
  },
};

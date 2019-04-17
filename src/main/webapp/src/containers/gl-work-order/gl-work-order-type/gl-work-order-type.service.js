import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 分页查询核算工单类型
   */
  typeQuery(params) {
    return httpFetch.get(
      `${config.accountingUrl}/api/general/ledger/work/order/types/query`,
      params
    );
  },
  /**
   * 获取关联表单类型
   */
  getRelatedFormList(setOfBooksId) {
    return httpFetch.get(
      `${
        config.workflowUrl
      }/api/custom/forms/setOfBooks/my/available/all?formTypeId=801008&setOfBooksId=${setOfBooksId}&roleType=TENANT`
    );
  },
  /**
   * 新增核算工单类型
   */
  typeInsert(params) {
    return httpFetch.post(`${config.accountingUrl}/api/general/ledger/work/order/types`, params);
  },
  /**
   * 更新核算工单类型
   */
  typeUpdate(params) {
    return httpFetch.put(`${config.accountingUrl}/api/general/ledger/work/order/types`, params);
  },
  /**
   * 根据id获取核算工单类型详细
   */
  getTypeById(id) {
    return httpFetch.get(`${config.accountingUrl}/api/general/ledger/work/order/types/${id}`);
  },
  /**
   * 获取核算工单类型分配公司
   */
  getTypeDistributionCompany(id, page, size) {
    return httpFetch.get(
      `${
        config.accountingUrl
      }/api/general/ledger/work/order/type/companies/query?workOrderTypeId=${id}&page=${page}&size=${size}`
    );
  },
  /**
   * 批量修改核算工单类型分配公司
   */
  typeDistributionCompanyUpdate(params) {
    return httpFetch.put(
      `${config.accountingUrl}/api/general/ledger/work/order/type/companies/batch`,
      params
    );
  },
  /**
   * 批量新增核算工单类型分配公司
   */
  typeDistributionCompanyInsert(params) {
    return httpFetch.post(
      `${config.accountingUrl}/api/general/ledger/work/order/type/companies/batch`,
      params
    );
  },
  /**
   * 获取维度列表
   * @param {*} id
   */
  getDimensionById(id, params = {}) {
    return httpFetch.get(
      `${config.accountingUrl}/api/general/ledger/work/order/types/${id}/dimension/query`,
      params
    );
  },
  /**
   * 获取未分配的维度
   * @param {*} params
   */
  getUndistributedDimensionList(id, params) {
    return httpFetch.get(
      `${config.accountingUrl}/api/general/ledger/work/order/types/${id}/dimensions/query/filter`,
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
      `${config.accountingUrl}/api/general/ledger/work/order/types/${id}/assign/dimension`,
      params
    );
  },
  /**
   * 删除维度
   */
  deleteDimensionValue(id) {
    return httpFetch.delete(
      `${config.accountingUrl}/api/general/ledger/work/order/types/dimension/${id}`
    );
  },
  /**
   * 修改维度
   */
  editDimensionValue(id, params) {
    return httpFetch.post(
      `${config.accountingUrl}/api/general/ledger/work/order/types/${id}/assign/dimension`,
      params
    );
  },
  /**
   * 获取责任中心
   */
  getResponsibleCenter(params) {
    return httpFetch.get(`${config.mdataUrl}/api/responsibilityCenter/query/default`, params);
  },
};

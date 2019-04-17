import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取自动付款规则数据列表
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getAutoPayRuleHeadList(page, size) {
    const url = `${config.fundUrl}/api/payment/rule/head/pageByCondition?page=${page}&size=${size}`;
    return httpFetch.get(url);
  },

  /**
   * 获取付款账户模态框的列表页面
   */
  getAccountList(page, size, companyId, searchParams) {
    let url = `${
      config.fundUrl
    }/api/account/maintain/normal/pageByCondition?page=${page}&size=${size}&companyId=${companyId}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 新建开户申请头
   * @param {*} record
   */
  createHeader(record) {
    return httpFetch.post(`${config.fundUrl}/api/payment/rule/head`, record);
  },

  /**
   * 更新开户申请头
   * @param {*} record
   */
  updateHeader(record) {
    return httpFetch.put(`${config.fundUrl}/api/payment/rule/head`, record);
  },

  /**
   * 根据id获得详情
   * @param {*} id
   */
  getInfoById(id) {
    return httpFetch.get(`${config.fundUrl}/api/payment/rule/head/${id}`);
  },

  /**
   * 获取分配的公司
   * @param {*} id
   */
  getDistributiveCompany(page, size, id, params = {}) {
    return httpFetch.get(
      `${config.fundUrl}/api/payment/rule/company/pageByCondition/${id}?page=${page}&size=${size}`,
      params
    );
  },

  /**
   * 更改公司分配状态
   * @param {*} parmas
   */
  updateAssignCompany(params) {
    return httpFetch.put(
      // `${config.fundUrl}/api/payment/rule/company?id=${parmas.id}&enabled=${parmas.enabled}`
      `${config.fundUrl}/api/payment/rule/company`,
      params
    );
  },

  /**
   * 批量分配公司
   * @param {*} parmas
   */
  batchDistributeCompany(parmas) {
    return httpFetch.put(
      // `${config.expenseUrl}/api/travel/application/type/company?travelTypeId=${id}`,
      `${config.fundUrl}/api/payment/rule/company/batchInsert`,
      parmas
    );
  },

  /**
   * 获取分配的员工级别
   * @param {*} id
   */
  getDistributiveEmploy(id, params = {}) {
    return httpFetch.get(
      `${config.fundUrl}/api/payment/rule/employee/pageByCondition/${id}`,
      params
    );
  },

  /**
   * 更改员工分配状态
   * @param {*} parmas
   */
  updateAssignEmploy(params) {
    return httpFetch.put(`${config.fundUrl}/api/payment/rule/employee`, params);
  },

  /**
   * 保存员工代码
   */
  saveEmployValue(params) {
    return httpFetch.put(`${config.fundUrl}/api/payment/rule/employee`, params);
  },

  /**
   *
   * @param {*} Id
   * 根据Id获取头
   */
  getAutoPayRuleHead() {
    // const url = `${config.fundUrl}/api/account/right/base/pageByCondition?employeeId=${employeeId}`;
    // const url = `http://192.168.1.188:9099/api/account/right/base/pageByCondition?&page=${page}&size=${size}`;
    // return httpFetch.get(url);
  },
};

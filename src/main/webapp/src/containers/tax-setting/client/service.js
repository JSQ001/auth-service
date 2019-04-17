import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 创建一个客户
   * @param {*} params
   */
  saveFunction(params) {
    return httpFetch.post(`${config.taxUrl}/api/tax/client/application`, params);
  },

  /**
   * 根据单据头ID查询单据头详情
   * @param {*} id
   */
  getApplicationDetail(id) {
    return httpFetch.get(`${config.taxUrl}/api/tax/client/application/` + id);
  },

  /**
   * 根据客户正式表id查询客户正式表详情
   * @param {*} id
   */
  getTaxClientDetail(id) {
    return httpFetch.get(`${config.taxUrl}/api/tax/client/` + id);
  },

  /**
   * 删除客户
   * @param {*} id
   */
  deleteClientApplication(id) {
    return httpFetch.delete(`${config.taxUrl}/api/tax/client/application/` + id);
  },

  /**
   * 获取审批历史
   * @param {*} oid
   */
  getHistory(oid) {
    return httpFetch.get(
      `${config.baseUrl}/api/budget/journa/reports/history?entityType=801009&entityOID=` + oid
    );
  },

  /**
   * 提交
   * @param {*} params
   */
  submit(params) {
    let url = `${config.taxUrl}/api/tax/client/application/submit`;
    return httpFetch.post(url, params);
  },

  /**
   * 根据ID查询申请单头信息(编辑申请单头时调用)
   * @param {*} id
   */
  getEditInfo(id) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/application/header/query?id=` + id);
  },

  /**
   * 更新客户信息
   * @param {*} params
   */
  updateClientData(params) {
    return httpFetch.put(`${config.taxUrl}/api/tax/client/application`, params);
  },

  /**
   * 撤回
   * @param {*} params
   */
  withdraw(params) {
    return httpFetch.post(`${config.workflowUrl}/api/workflow/withdraw`, params);
  },

  /**
   * 根据单据id获取申请人信息（新建单据时调用）
   * @param {*} id
   */
  listUserByTypeId(id) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/application/type/users/` + id);
  },

  /**
   * 根据用户oid获取公司及部门信息（新建单据时调用）
   * @param {*} userOid
   */
  getUserInfoByTypeId(userOid) {
    return httpFetch.get(`${config.mdataUrl}/api/users/oid/` + userOid);
  },

  // 导出客户信息
  exportTaxClient(result, ps, exportParams) {
    let url = `${config.taxUrl}/api/tax/client/export/data?page=${ps.page}&size=${ps.size}`;
    // eslint-disable-next-line guard-for-in
    for (const searchName in exportParams) {
      url += exportParams[searchName] ? `&${searchName}=${exportParams[searchName]}` : '';
    }
    return new Promise((resolve, reject) => {
      httpFetch
        .post(url, result, {}, { responseType: 'arraybuffer' })
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          // eslint-disable-next-line no-undef
          errorMessage(err.response);
          reject(err);
        });
    });
  },
};

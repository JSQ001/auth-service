import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 根据账户变更申请头ID集合，批量删除账户变更申请
   * @param {*} ids
   */
  batchDeleteAccount(list) {
    return httpFetch.delete(`${config.fundUrl}/api/account/change/batchDelete`, list);
  },

  /**
   * 获取银行账户列表
   */
  getAccountOpenMaintenanceList(page, size) {
    const url = `${config.fundUrl}/api/account/maintain/pageByCondition?&page=${page}&size=${size}`;
    return httpFetch.get(url);
  },
  /**
   * 获取银行账号所有信息
   */
  getPageByCondition(page, size, accountNumber = '') {
    const url = `${
      config.fundUrl
    }/api/account/change/normal/pageByCondition?&page=${page}&size=${size}&accountNumber=${accountNumber}`;

    return httpFetch.get(url);
  },

  /**
   * hi hi
   * 根据头id 删除调拨申请单
   * @param {*} list
   */
  deleteList(list) {
    return httpFetch.delete(`${config.fundUrl}/api/cp/transfer/appl/header/deleteList`, list);
  },

  /**
   * 获取调拨申请头列表
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getTransferApplHeader(page, size, searchParams) {
    let url = `http://10.211.97.86:9099/api/cp/transfer/appl/header/getTransferApplHeader?&page=${page}&size=${size}`;
    // let url = `${config.fundUrl}/api/cp/transfer/appl/header/getTransferApplHeader?&page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 根据账户变更申请头ID，获取变更申请详细信息
   * @param {*} headerId
   */
  getAccountHead(headerId) {
    return httpFetch.get(`${config.fundUrl}/api/account/change/${headerId}`);
  },

  /**
   * 根据账套ID，获取账套下公司列表
   * @param {*} setOfBooksId
   */
  getCompanyListByBooksId(setOfBooksId) {
    return httpFetch.get(`${config.baseUrl}/api/company/by/condition?setOfBooksId=${setOfBooksId}`);
  },

  /**
   * 提交账户信息更新申请
   * @param {*} list
   */
  submitAccountEdit(list) {
    return httpFetch.post(`${config.fundUrl}/api/account/change/batchSubmit`, list);
  },

  /**
   * 更新开户申请头
   * @param {*} record
   */
  updateHeader(record) {
    return httpFetch.put(`${config.fundUrl}/api/account/open/`, record);
  },

  /**
   * 插入更新调拨申请
   * @param {*} record
   */
  createOrUpdate(record) {
    // return httpFetch.post(`${config.fundUrl}/api/cp/transfer/appl/header/createOrUpdate`, record);
    return httpFetch.post(
      `http://10.211.97.86:9099/api/cp/transfer/appl/header/createOrUpdate`,
      record
    );
  },

  /**
   * 更新账户信息申请
   */
  updateAccountModifyDetail(record) {
    return httpFetch.put(`${config.fundUrl}/api/account/change`, record);
  },

  /**
   * 明细页删除行
   */
  deleteManualList(deleteIdList) {
    // const url = `${config.fundUrl}/api/cp/transfer/appl/line/deleteList`;
    const url = `http://10.211.97.86:9099/api/cp/transfer/appl/line/deleteList`;
    return httpFetch.delete(url, deleteIdList);
  },

  /**
   * 插入账户信息申请
   */
  insertAccountModifyDetail(record) {
    return httpFetch.post(`${config.fundUrl}/api/account/change`, record);
  },

  /**
   * 根据ID获取账户变更详情
   */
  getModifyAccountDetail(id) {
    //
    // return httpFetch.get(`${config.fundUrl}/api/account/change/${id}`);
    return httpFetch.get(
      `http://10.211.97.86:9099/api/cp/transfer/appl/line/getTransferApplLine/${id}`
    );
  },
};

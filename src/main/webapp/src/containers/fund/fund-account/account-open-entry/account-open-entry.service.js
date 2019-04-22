import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取开户银行列表
   */
  getAccountBank() {
    return httpFetch.get(`${config.fundUrl}/api/account/open/codeList/ZJ_OPEN_BANK`);
  },
  /**
   * 获取币种列表
   * @param {*} id
   */
  getCurrencyList(id) {
    // return httpFetch.get(`${config.baseUrl}/api/company/standard/currency/getAll`);
    return httpFetch.get(
      `${
        config.mdataUrl
      }/api/currency/rate/company/standard/currency/getAll/companyOid?companyOid=${id}`
    );
  },
  /**
   * 获取账户开户维护列表页面
   */
  getAccountOpenMaintenanceList(page, size, searchParams) {
    // http://10.211.110.57:8080
    let url = `${
      config.fundUrl
    }/api/account/maintain/pageDirectAddAccount?&page=${page}&size=${size}`;
    // let url = `http://10.211.110.57:8080/api/account/maintain/pageDirectAddAccount?&page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },
  /**
   * 创建或修改
   * @param {*} record
   */
  createOrUpdateList(record) {
    console.log('service in');
    return httpFetch.post(`${config.fundUrl}/api/account/maintain/directAdd`, record);
    // return httpFetch.post(`http://10.211.110.57:8080/api/account/maintain/directAdd`, record);
  },
  /**
   * 根据ID查询账户开户维护详情
   */
  getAccountOpenMaintenanceDetail(id) {
    return httpFetch.get(`${config.fundUrl}/api/account/maintain/${id}`);
  },

  /**
   * 更新账户开户详情页
   */
  updateAccountOpenMaintenanceDetail(record) {
    return httpFetch.put(`${config.fundUrl}/api/account/maintain`, record);
  },

  /**
   * 作废
   */
  deleteList(list) {
    return httpFetch.delete(`${config.fundUrl}/api/account/maintain/batchDelete`, list);
  },

  /**
   * 提交
   */
  submit(id) {
    return httpFetch.put(`${config.fundUrl}/api/account/maintain/submit/${id}`);
  },
};

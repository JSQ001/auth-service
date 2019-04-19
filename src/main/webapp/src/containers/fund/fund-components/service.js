import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取银行账户列表
   */
  getAccountOpenMaintenanceList(page, size, accountNumber = '') {
    const url = `${
      config.fundUrl
    }/api/account/maintain/normal/pageByCondition?page=${page}&size=${size}&accountNumber=${accountNumber}`;
    return httpFetch.get(url);
  },

  /**
   * 获取公司列表
   */
  getFundCompanys(page = 0, size = 10, fundSetOfBooksId, searchParams) {
    let url = `${
      config.mdataUrl
    }/api/company/by/condition?&page=${page}&size=${size}&setOfBooksId=${fundSetOfBooksId}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 获取账户
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getAccountList(page, size, companyId, searchParams) {
    let url;
    if (companyId) {
      url = `${
        config.fundUrl
      }/api/account/query/right/pageByCondition?&flag=PAY&page=${page}&size=${size}&companyId=${companyId}`;
    } else {
      url = `${
        config.fundUrl
      }/api/account/query/right/pageByCondition?&flag=PAY&page=${page}&size=${size}`;
    }
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },
};

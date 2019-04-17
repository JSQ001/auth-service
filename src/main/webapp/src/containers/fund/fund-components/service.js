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
  getCompanys(page, size, setOfBooksId, searchParams) {
    let url = `${
      config.mdataUrl
    }/api/company/by/condition?&page=${page}&size=${size}&setOfBooksId=${setOfBooksId}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },
};

import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取余额列表
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getAccountBalanceList(page, size, searchParams) {
    let url = `${config.fundUrl}/api/account/balance/queryByCond?page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 根据ID获取历史余额查询
   */
  getAccountBalanceHistory(accountId, startDate, endDate) {
    const url = `${
      config.fundUrl
    }/api/account/balance/queryHistoryByCond?accountId=${accountId}&startDate=${startDate}&endDate=${endDate}`;
    return httpFetch.get(url);
  },
};

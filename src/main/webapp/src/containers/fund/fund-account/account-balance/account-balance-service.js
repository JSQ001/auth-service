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
    // let url = `http://10.211.110.100:9099/api/account/balance/queryByCond?page=${page}&size=${size}`
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

  /**
   * 获取银行存款类型分布的报表数据
   */
  getCapitalType() {
    const url = `${config.fundUrl}/api/account/query/account/depositType`;
    // const url = `http://10.211.97.86:9099/api/account/query/account/depositType`;
    return httpFetch.get(url);
  },

  /**
   * 获取资金存款分布的报表数据
   */
  getCapitalDistribution() {
    const url = `${config.fundUrl}/api/account/query/account/amount`;
    // const url = `http://10.211.97.86:9099/api/account/query/account/amount`;
    return httpFetch.get(url);
  },
};

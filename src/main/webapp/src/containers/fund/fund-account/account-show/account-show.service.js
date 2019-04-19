import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  accountInformationList(page, size, searchParams) {
    let url = `${config.fundUrl}/api/account/query/pageByCondition?&page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 根据账套ID，获取账套下公司列表
   * @param {*} setOfBooksId
   */
  getCompanyListByBooksId(setOfBooksId) {
    return httpFetch.get(
      `${config.mdataUrl}/api/company/by/condition?setOfBooksId=${setOfBooksId}`
    );
  },

  /**
   * 获取银行预览状态一览的报表数据
   */
  getBankStatusRenderAccountStatus() {
    const url = `${config.fundUrl}/api/account/query/account/stateNum`;
    // const url = `http://10.211.97.86:9099/api/account/query/account/stateNum`;
    return httpFetch.get(url);
  },

  /**
   * 获取银行预览分布一览的报表数据
   */
  getBankRenderAccountStatus() {
    const url = `${config.fundUrl}/api/account/query/account/openBankNum`;
    // const url = `http://10.211.97.86:9099/api/account/query/account/openBankNum`;
    return httpFetch.get(url);
  },
};

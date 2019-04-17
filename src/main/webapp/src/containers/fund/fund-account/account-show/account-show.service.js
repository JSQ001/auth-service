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
};

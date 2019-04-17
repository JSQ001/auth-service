import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 分页获取oracle银行日记账信息
   */
  getListOracle(page, size, searchParams) {
    let url = `${config.fundUrl}/api/account/check/pageOracleByCondition?page=${page}&size=${size}`;
    // let url = `http://10.211.110.57:8080/api/account/check/pageOracleByCondition?page=${page}&size=${size}&accountId=${id}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 分页获取银行流水信息
   */
  getListBank(page, size, searchParams) {
    let url = `${config.fundUrl}/api/account/check/pageBankByCondition?page=${page}&size=${size}`;
    // let url = `http://10.211.110.57:8080/api/account/check/pageBankByCondition?page=${page}&size=${size}&accountId=${id}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 获取期间数据
   */
  getPeriod() {
    // const url = `http://localhost:8000/mdata/api/query/budget/periods?setOfBooksId=1083762150064451585`;
    const url = `http://localhost:8000/mdata/api/query/budget/periods?setOfBooksId=1083762150064451585`;
    return httpFetch.get(url);
  },
};

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
    const url = `${config.mdataUrl}/api/query/budget/periods?setOfBooksId=1083762150064451585`;
    return httpFetch.get(url);
  },

  /**
   * 手工调节
   */
  manualReconciliation(obj) {
    // const url = `http://10.211.110.57:8080/api/account/check/manualCheckBalance`;
    const url = `${config.fundUrl}/api/account/check/manualCheckBalance`;
    return httpFetch.post(url, obj);
  },

  /**
   * 自动对账
   */
  autoReconciliation(accountId, periodName) {
    // const url = `http://10.211.110.57:8080/api/account/check/autoCheckBalance?accountId=${accountId}&periodName=${periodName}`;
    const url = `${
      config.fundUrl
    }/api/account/check/autoCheckBalance?accountId=${accountId}&periodName=${periodName}`;
    return httpFetch.post(url);
  },

  /**
   * 撤销对账
   */
  revertReconciliation(arr) {
    // const url = `http://10.211.110.57:8080/api/account/check/revertCheckBalance`
    const url = `${config.fundUrl}/api/account/check/revertCheckBalance`;
    return httpFetch.post(url, arr);
  },

  /**
   * 生成余额调节表
   */
  generateBankReconciliation(accountId, periodName) {
    // const url = `http://10.211.110.57:8080/api/account/check/generateBankReconciliation?accountId=${accountId}&periodName=${periodName}`;
    const url = `${
      config.fundUrl
    }/api/account/check/generateBankReconciliation?accountId=${accountId}&periodName=${periodName}`;
    return httpFetch.post(url);
  },
};

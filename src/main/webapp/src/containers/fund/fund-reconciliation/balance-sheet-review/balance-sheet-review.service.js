import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 批量创建或修改
   * @param {*} record
   */
  createOrUpdateList(record) {
    return httpFetch.post(`${config.fundUrl}/api/cp/balance/maintain/createOrUpdateManual`, record);
    // return httpFetch.post(`${config.fundUrl}/api/cp/balance/maintain/createOrUpdateList`, list);
    // return httpFetch.post(`http://10.211.97.86:9099/api/cp/balance/maintain/createOrUpdateManual`, record);
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
   * 获取开户银行列表
   */
  getAccountBank() {
    return httpFetch.get(`${config.fundUrl}/api/account/open/codeList/ZJ_OPEN_BANK`);
  },

  /**
   * 余额调节表复核
   * @param {*} list
   */
  maintainReview(list) {
    return httpFetch.put(`${config.fundUrl}/api/account/check/adjust/confirm`, list);
    // return httpFetch.put(`http://10.211.97.86:9099/api/cp/balance/maintain/review`, list);
  },

  /**
   * 余额调节表取消复核
   * @param {*} list
   */
  accountCheckPassreview(list) {
    return httpFetch.put(`${config.fundUrl}/api/account/check/adjust/cancel`, list);
    // return httpFetch.put(`http://10.211.97.86:9099/api/cp/balance/maintain/passReview`, list);
  },

  /**
   * 根据ID获取历史余额查询
   */
  getAccountCheckList(searchParams) {
    // const url ={`${config.baseUrl}/api/account/change/normal/pageByCondition?&page=${page}&size=${size}`};
    let url = `${config.fundUrl}/api/account/check/adjust/pageByCondition?`;
    // let url = `http://10.211.97.86:9099/api/cp/balance/maintain/pageByCondition?&page=${page}&size=${size}`;
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
};

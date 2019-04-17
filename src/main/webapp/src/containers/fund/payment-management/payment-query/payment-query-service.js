import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取付款单列表页面
   */
  getPaymentQueryList(page, size, searchParams) {
    let url = `${config.fundUrl}/api/payment/baseInfo/pageByCondition?page=${page}&size=${size}`;
    // let url = `http://10.211.97.86:9099/api/payment/baseInfo/pageByCondition?page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },
  /**
   * 获取账户列表页面
   */
  getAccountList(page, size, searchParams) {
    let url = `${
      config.fundUrl
    }/api/account/maintain/normal/pageByCondition?page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 明细页查询
   */
  getManualList(page, size, id) {
    // getManualList() {
    const url = `${
      config.fundUrl
    }/api/payment/lineInfo/query?page=${page}&size=${size}&paymentBaseId=${id}`;
    // const url = `http://10.211.97.86:9099/api/payment/lineInfo/query?page=${page}&size=${size}&paymentBaseId=${id}`;
    return httpFetch.get(url);
  },
};

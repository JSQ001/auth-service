import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取付款单详细列表
   */
  getPaymentDetailList(page, size, searchParams, queryType) {
    // console.log(searchParams);
    // let url = `http://10.211.97.86:9099/api/payment/lineInfo/queryAllLine?page=${page}&size=${size}&queryType=${queryType}`;
    let url = `${
      config.fundUrl
    }/api/payment/lineInfo/queryAllLine?page=${page}&size=${size}&queryType=${queryType}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },
};

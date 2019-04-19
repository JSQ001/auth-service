import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取资金调拨单创建页面的数据列表
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getFundTransferList(page, size, searchParams) {
    // let url = `http://10.211.110.100:9099/api/cp/adjust/formal/baseInfo/getCpAdjustNew?page=${page}&size=${size}`;
    let url = `${
      config.fundUrl
    }/api/cp/adjust/formal/baseInfo/getCpAdjustNew?page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 调拨单模块-资金调拨-头 - 批量删除
   */
  batchDelete(params) {
    // return httpFetch.delete(`http://10.211.110.100:9099/api/cp/adjust/formal/baseInfo/batchDelete`,params)
    return httpFetch.delete(`${config.fundUrl}/api/cp/adjust/formal/baseInfo/batchDelete`, params);
  },
};

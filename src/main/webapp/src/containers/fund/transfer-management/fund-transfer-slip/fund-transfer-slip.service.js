import httpFetch from 'share/httpFetch';
import config from 'config';

export default {
  /**
   * 获取调拨申请头列表
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getCpAdjustShow(page, size, searchParams) {
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

  getManualList(page, size, id) {
    // ${config.fundUrl}
    const url = `${
      config.fundUrl
    }/api/cp/adjust/formal/baseInfo/queryByBaseId?page=${page}&size=${size}&baseId=${id}`;
    return httpFetch.get(url);
  },
};

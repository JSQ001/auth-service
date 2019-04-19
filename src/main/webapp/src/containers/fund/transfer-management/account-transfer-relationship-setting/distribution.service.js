import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取资金池配置行表
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getDistributionList(page, size, id, searchParams) {
    let url = `${config.fundUrl}/api/autoGather/line/get?&page=${page}&size=${size}&headId=${id}`;
    // let url = `http://10.211.98.2:9099/api/autoGather/line/get?&page=${page}&size=${size}&headId=${id}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },
  /**
   * 获取资金池配置头表
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getDistributionHeader(page, size, id) {
    const url = `${
      config.fundUrl
    }/api/autoGather/head/page?&page=${page}&size=${size}&headId=${id}`;
    // const url = `http://10.211.98.2:9099/api/autoGather/head/page?&page=${page}&size=${size}&headId=${id}`;
    return httpFetch.get(url);
  },
  /**
   * 资金池设置（行）删除
   */
  deleteDistributionList(deleteList) {
    const url = `${config.fundUrl}/api/autoGather/line/delete`;
    // const url = `http://10.211.98.2:9099/api/autoGather/line/delete`;
    return httpFetch.delete(url, deleteList);
  },

  /**
   * 资金池设置（行）新增与修改
   */
  addDistributionList(addDate) {
    const arr = [];
    arr.push(addDate);
    const url = `${config.fundUrl}/api/autoGather/line/post`;
    // const url = `http://10.211.98.2:9099/api/autoGather/line/post`;
    return httpFetch.post(url, arr);
  },
};

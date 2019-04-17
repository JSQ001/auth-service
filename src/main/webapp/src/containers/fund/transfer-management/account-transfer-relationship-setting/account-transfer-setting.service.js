import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取账户划拨关系设置的数据列表
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getAccountTransferList(page, size, searchParams) {
    // let url = `http://10.211.98.2:9099/api/autoGather/head/page?page=${page}&size=${size}`;
    let url = `${config.fundUrl}/api/autoGather/head/page?page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 企业资金池设置行表新增或修改
   */
  createOrUpdateSave(saveParams) {
    // const url = `http://10.211.98.2:9099/api/autoGather/head/post`;
    const url = `${config.fundUrl}/api/autoGather/head/post`;
    return httpFetch.post(url, saveParams);
    // return httpFetch.post(`${config.fundUrl}/api/autoGather/line/post`, saveParams);
  },

  /**
   * 序号新建时自增
   */
  numberIncrease() {
    // const url = `http://10.211.98.2:9099/api/autoGather/head/getPriority`;
    const url = `${config.fundUrl}/api/autoGather/head/getPriority`;
    return httpFetch.get(url);
  },
};

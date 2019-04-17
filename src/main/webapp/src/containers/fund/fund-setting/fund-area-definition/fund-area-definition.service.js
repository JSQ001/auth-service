// import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 资金地区定义查询
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getHead(page, size, searchParams) {
    // let url = `http://10.211.98.2:9099/api/autoGather/head/page?page=${page}&size=${size}`;

    let url = `http://10.211.98.2:9099/api/regionDefines/page?page=${page}&size=${size}`;
    // if (id) {
    //   url = `${config.fundUrl}/api/bankParams/head/page?page=${page}&size=${size}&headId=${id}`;
    // } else {
    //   url = `${config.fundUrl}/api/bankParams/head/page?page=${page}&size=${size}`;
    // }
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 资金地区定义新增与修改
   */
  updateSave(saveParams) {
    const url = `http://10.211.98.2:9099/api/regionDefines/post`;
    return httpFetch.post(url, saveParams);
  },
};

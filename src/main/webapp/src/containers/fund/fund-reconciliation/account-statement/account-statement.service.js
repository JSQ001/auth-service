import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 查询
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getMaintainList(page, size, searchParams) {
    let url = `${config.fundUrl}/api/account/flow/pageByCondition?page=${page}&size=${size}`;
    // let url = `http://10.211.110.57:8080/api/account/flow/pageByCondition?&page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   *保存新建流水
   * @param {*} saveData
   */
  addFlow(saveData) {
    const url = `${config.fundUrl}/api/account/flow`;
    // const url = `http://10.211.110.57:8080/api/account/flow?`;
    return httpFetch.post(url, saveData);
  },

  /**
   * 删除手工增加流水
   * @param {*} deleteList
   */
  deleteAccount(deleteList) {
    const url = `${config.fundUrl}/api/account/flow`;
    // const url = http://10.211.110.57:8080/api/account/flow`;
    return httpFetch.delete(url, deleteList);
  },
};

// import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 根据id获取流水详细信息
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getMaintainList(page, size, searchParams) {
    // const url ={`${config.baseUrl}/api/account/flow/pageByCondition?&page=${page}&size=${size}`};
    // let url = `${
    //   config.fundUrl
    // }/api/account/flow/pageByCondition?&page=${page}&size=${size}`;
    let url = `http://10.211.110.57:8080/api/account/flow/pageByCondition?&page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },
};

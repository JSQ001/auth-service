import config from 'config';
import httpFetch from 'share/httpFetch';

// const testUrl = `http://10.211.98.2:9099`;
export default {
  /**
   * 基础信息头查询
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getHead(page, size, searchParams, id) {
    let url = '';
    if (id) {
      url = `${config.fundUrl}/api/bankParams/head/page?page=${page}&size=${size}&headId=${id}`;
    } else {
      url = `${config.fundUrl}/api/bankParams/head/page?page=${page}&size=${size}`;
    }
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 基础头信息新增与更新
   */
  insertHead(record) {
    const url = `${config.fundUrl}/api/bankParams/head/post`;
    return httpFetch.post(url, record);
  },

  /**
   * 银企直联参数定义 - 【参数配置（行）查询】
   */
  getLine(headId) {
    const url = `${config.fundUrl}/api/bankParams/line/get?headId=${headId}`;
    return httpFetch.get(url);
  },

  /**
   * 银企直联参数定义 - 【公司-分配查询】
   */
  async getCompanys(page, size, headId) {
    const url = `${
      config.fundUrl
    }/api/bankParams/company/pageByCondition/${headId}?page=${page}&size=${size}`;
    return httpFetch.get(url);
  },

  /**
   * 银企直联参数定义 - 【公司-新增与修改】
   */
  insertCompanies(record) {
    const url = `${config.fundUrl}/api/bankParams/company/batch`;
    return httpFetch.put(url, record);
  },

  /**
   * 银企直联参数定义 - 【参数配置（行）新增与更新】
   */
  bankParamsLineSet(record) {
    const url = `${config.fundUrl}/api/bankParams/line/insertOrUpdate`;
    return httpFetch.post(url, record);
  },
};

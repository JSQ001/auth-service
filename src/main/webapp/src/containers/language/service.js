import httpFetch from 'share/httpFetch';
import config from 'config';

export default {
  /**
   * 新增前端多语言信息
   * @param {*} values
   */
  addWebLocale(values) {
    return httpFetch.post(`${config.baseUrl}/api/front/locale`, values);
  },

  /**
   * 编辑前端多语言信息
   * @param {*} values
   */
  updateWebLocale(values) {
    return httpFetch.put(`${config.baseUrl}/api/front/locale`, values);
  },

  /**
   * 删除前端多语言信息
   * @param {*} id
   */
  deleteWebLocale(id) {
    return httpFetch.delete(`${config.baseUrl}/api/front/locale/${id}`);
  },

  /**
   * 新增前端多语言信息
   * @param {*} values
   */
  addServeLocale(values) {
    return httpFetch.post(`${config.baseUrl}/api/serve/locale`, values);
  },

  /**
   * 编辑前端多语言信息
   * @param {*} values
   */
  updateServeLocale(values) {
    return httpFetch.put(`${config.baseUrl}/api/serve/locale`, values);
  },

  /**
   * 删除前端多语言信息
   * @param {*} id
   */
  deleteServeLocale(id) {
    return httpFetch.delete(`${config.baseUrl}/api/serve/locale/${id}`);
  },

  /**
   * 获取其他语言
   * @param {*} params
   */
  getOtherMessages(params) {
    return httpFetch.get(
      `${config.baseUrl}/api/front/locale/query/other/front/locale/by/cond`,
      params
    );
  },

  /**
   * 批量添加前台多语言
   * @param {*} params
   */
  addBatchFrontKey(params) {
    return httpFetch.post(`${config.baseUrl}/api/front/locale/batch`, params);
  },

  /**
   * 批量更新前台多语言
   * @param {*} params
   */
  updateBatchFrontKey(params) {
    return httpFetch.put(`${config.baseUrl}/api/front/locale/batch`, params);
  },
  /**
   * 获取服务端 表格数据
   * @param {*} params
   */
  getServerMessage(params) {
    return httpFetch.get(
      `${config.baseUrl}/api/serve/locale/query/other/serve/locale/by/cond`,
      params
    );
  },
  /**
   * 批量添加前台多语言
   * @param {*} params
   */
  addBatchServer(params) {
    return httpFetch.post(`${config.baseUrl}/api/serve/locale/batch`, params);
  },

  /**
   * 批量更新前台多语言
   * @param {*} params
   */
  updateBatchServer(params) {
    return httpFetch.put(`${config.baseUrl}/api/serve/locale/batch`, params);
  },
};

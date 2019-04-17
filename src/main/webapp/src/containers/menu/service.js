import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 单个新增页面
   * @param {*} values
   */
  addPage(values) {
    return httpFetch.post(`${config.baseUrl}/api/page/list`, values);
  },

  /**
   * 删除单个页面
   * @param {*} id
   */
  deletePage(id) {
    return httpFetch.delete(`${config.baseUrl}/api/page/list/${id}`);
  },

  /**
   * 编辑单个页面
   * @param {*} values
   */
  editPage(values) {
    return httpFetch.put(`${config.baseUrl}/api/page/list`, values);
  },

  /**
   * 单个新增功能
   * @param {*} values
   */
  addFunction(values) {
    return httpFetch.post(`${config.baseUrl}/api/function/list`, values);
  },

  /**
   * 删除单个功能
   * @param {*} id
   */
  deleteFunction(id) {
    return httpFetch.delete(`${config.baseUrl}/api/function/list/${id}`);
  },

  /**
   * 编辑单个功能
   * @param {*} values
   */
  editFunction(values) {
    return httpFetch.put(`${config.baseUrl}/api/function/list`, values);
  },

  /**
   * 功能分配页面
   * @param {*} values
   */
  functionAllowPage(values) {
    return httpFetch.post(`${config.baseUrl}/api/function/page/relation`, values);
  },

  /**
   * 单个新增目录
   * @param {*} values
   */
  addContent(values) {
    return httpFetch.post(`${config.baseUrl}/api/content/list`, values);
  },

  /**
   * 删除单个目录
   * @param {*} id
   */
  deleteContent(id) {
    return httpFetch.delete(`${config.baseUrl}/api/content/list/${id}`);
  },

  /**
   * 编辑单个目录
   * @param {*} values
   */
  editContent(values) {
    return httpFetch.put(`${config.baseUrl}/api/content/list`, values);
  },

  /**
   * 目录分配功能
   * @param {*} values
   */
  contentAllowFunction(values) {
    return httpFetch.post(`${config.baseUrl}/api/content/function/relation`, values);
  },

  /**
   * 获取页面列表
   */
  getPageList(params) {
    return httpFetch.get(`${config.baseUrl}/api/page/list/query/by/cond`, params);
  },

  /**
   * 获取子目录
   */
  getChildrenContent(parentId) {
    return httpFetch.get(`${config.baseUrl}/api/content/list/query/son/content/${parentId}`);
  },

  /**
   * 获取已经分配的页面
   * @param {} params
   */
  getALlowedPages(params) {
    return httpFetch.get(`${config.baseUrl}/api/function/page/relation/query/by/cond`, params);
  },

  /**
   * 批量删除功能页面关联
   * @param {*} ids
   */
  deleteFunctionPage(ids) {
    return httpFetch.post(`${config.baseUrl}/api/function/page/relation/deleted/by/ids`, ids);
  },

  /**
   * 批量删除目录功能关联
   * @param {*} ids
   */
  deleteContentFunction(ids) {
    return httpFetch.post(`${config.baseUrl}/api/content/function/relation/deleted/by/ids`, ids);
  },

  /**
   * 获取目录
   * @param {*} params
   */
  getContentList(params) {
    return httpFetch.get(`${config.baseUrl}/api/content/list/query/by/cond`, params);
  },
  /**
   * 获取应用列表
   */
  getAppList() {
    return httpFetch.get(`${config.baseUrl}/api/application`, { page: 0, size: 999 });
  },
};

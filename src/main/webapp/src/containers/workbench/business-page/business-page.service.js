import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 分页条件查询
   * @param {*} page 当前页
   * @param {*} size 每页大小
   * @param {*} businessTypeId 业务类型Id
   * @param {*} searchParams 查询参数
   */
  queryBusinessPage(page, size, businessTypeId, searchParams) {
    let url = `${
      config.workbenchUrl
    }/api/workbench/businessPage/query?page=${page}&size=${size}&businessTypeId=${businessTypeId}`;
    for (let searchName in searchParams) {
      url += searchParams[searchName] ? `&${searchName}=${searchParams[searchName]}` : '';
    }
    return httpFetch.get(url);
  },

  /**
   * 保存方法过程
   * @param {*} param
   */
  saveBusinessPage(params) {
    let url = `${config.workbenchUrl}/api/workbench/businessPage/save`;
    return httpFetch.post(url, params);
  },
  /**
   * 删除过程方法
   * @param {*} id
   */
  deleteBusinessPage(id) {
    let url = `${config.workbenchUrl}/api/workbench/businessPage/${id}`;
    return httpFetch.delete(url);
  },

  /**
   * 获取单个页面
   * @param {*} id
   */
  getBusinessPageById(id) {
    let url = `${config.workbenchUrl}/api/workbench/businessPage/${id}`;
    return httpFetch.get(url);
  },
};

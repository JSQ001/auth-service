import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 分页条件查询
   * @param {*} page 当前页
   * @param {*} size 每页大小
   * @param {*} searchParams 查询参数
   */
  queryBusinessType(page, size, searchParams) {
    let url = `${config.workbenchUrl}/api/workbench/businessType/query?page=${page}&size=${size}`;
    for (let searchName in searchParams) {
      url += searchParams[searchName] ? `&${searchName}=${searchParams[searchName]}` : '';
    }
    return httpFetch.get(url);
  },
  /**
   * 保存业务类型
   * @param {*} params
   */
  saveBusinessType(params) {
    let url = `${config.workbenchUrl}/api/workbench/businessType/save`;
    return httpFetch.post(url, params);
  },

  /**
   * 根据Id获取业务类型
   * @param {*} id 主键Id
   */
  getBusinessTypeById(id) {
    let url = `${config.workbenchUrl}/api/workbench/businessType/query/${id}`;
    return httpFetch.get(url);
  },
};

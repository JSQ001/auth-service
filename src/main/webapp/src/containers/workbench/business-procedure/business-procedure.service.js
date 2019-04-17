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
  queryBusinessProcedure(page, size, businessTypeId, searchParams) {
    let url = `${
      config.workbenchUrl
    }/api/workbench/businessProcedure/query?page=${page}&size=${size}&businessTypeId=${businessTypeId}`;
    for (let searchName in searchParams) {
      url += searchParams[searchName] ? `&${searchName}=${searchParams[searchName]}` : '';
    }
    return httpFetch.get(url);
  },

  /**
   * 保存方法过程
   * @param {*} param
   */
  saveBusinessProcedure(params) {
    let url = `${config.workbenchUrl}/api/workbench/businessProcedure/save`;
    return httpFetch.post(url, params);
  },
  /**
   * 删除过程方法
   * @param {*} id
   */
  deleteBusinessProcedure(id) {
    let url = `${config.workbenchUrl}/api/workbench/businessProcedure/${id}`;
    return httpFetch.delete(url);
  },
  /**
   * 根据Id获取数据
   * @param {*} id
   */
  getBusinessProcedureById(id) {
    let url = `${config.workbenchUrl}/api/workbench/businessProcedure/${id}`;
    return httpFetch.get(url);
  },
};

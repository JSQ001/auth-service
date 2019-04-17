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
  queryRule(page, size, businessTypeId, searchParams) {
    let url = `${
      config.workbenchUrl
    }/api/workbench/dispatchRule/query?page=${page}&size=${size}&businessTypeId=${businessTypeId}`;
    for (let searchName in searchParams) {
      url += searchParams[searchName] ? `&${searchName}=${searchParams[searchName]}` : '';
    }
    return httpFetch.get(url);
  },
  /**
   * 创建规则
   * @param {*} params
   */
  createRule(params) {
    let url = `${config.workbenchUrl}/api/workbench/dispatchRule`;
    return httpFetch.post(url, params);
  },
  /**
   * 根据Id获取派工规则
   * @param {*} id
   */
  getRuleById(id) {
    let url = `${config.workbenchUrl}/api/workbench/dispatchRule/${id}`;
    return httpFetch.get(url);
  },
  /**
   * 删除派工规则
   * @param {*} id
   */
  deleteRule(id) {
    let url = `${config.workbenchUrl}/api/workbench/dispatchRule/${id}`;
    return httpFetch.delete(url);
  },
  /**
   * 更新规则
   * @param {*} params
   */
  updateRule(params) {
    let url = `${config.workbenchUrl}/api/workbench/dispatchRule`;
    return httpFetch.put(url, params);
  },
  /**
   * 根据规则Id获取规则明细数据
   * @param {*} page
   * @param {*} size
   * @param {*} ruleId
   */
  queryRuleDetail(page, size, ruleId) {
    let url = `${
      config.workbenchUrl
    }/api/workbench/dispatchRule/detail/query?page=${page}&size=${size}&ruleId=${ruleId}`;
    return httpFetch.get(url);
  },
  /**
   * 保存规则明细
   * @param {*} params
   */
  saveRuleDeatil(params) {
    let url = `${config.workbenchUrl}/api/workbench/dispatchRule/detail`;
    return httpFetch.post(url, params);
  },
  /**
   * 根据Id删除规则明细
   * @param {*} id
   */
  deleteRuleDetail(id) {
    let url = `${config.workbenchUrl}/api/workbench/dispatchRule/detail/${id}`;
    return httpFetch.delete(url);
  },
};

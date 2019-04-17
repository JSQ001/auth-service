import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取工作组数据
   * @param {*} params
   */
  getWorkTeamList(params = {}) {
    const url = `${config.workbenchUrl}/api/workbench/businessWorkTeam/query`;
    return httpFetch.get(url);
  },
  /**
   * 根据关键字获取工作组数据
   * @param {*} keyword
   */
  getWorkTeamBySearch(params) {
    const url = `${config.workbenchUrl}/api/workbench/businessWorkTeam/query/keyword?keyword=${
      params.keyword
    }`;
    return httpFetch.get(url);
  },
  /**
   * 获取工作组详情数据
   * @param {*} id
   */
  getCurWorkTeamDetail(id) {
    const url = `${config.workbenchUrl}/api/workbench/businessWorkTeam/query/${id}`;
    return httpFetch.get(url);
  },
  /**
   * 新增工作组
   * @param {*} params
   */
  addWorkTeamValue(params) {
    const url = `${config.workbenchUrl}/api/workbench/businessWorkTeam/save`;
    return httpFetch.post(url, params);
  },
  /**
   * 删除工作组
   * @param {*} id
   */
  deleteWorkTeamValue(id) {
    const url = `${config.workbenchUrl}/api/workbench/businessWorkTeam/delete/${id}`;
    return httpFetch.delete(url);
  },
  /**
   * 编辑工作组详情信息
   * @param {*} params
   */
  editWorkTeamValue(params) {
    const url = `${config.workbenchUrl}/api/workbench/businessWorkTeam/save`;
    return httpFetch.post(url, params);
  },
  /**
   * 获取员工数据【右侧列表】
   * @param {*} params
   */
  getEmployeeList(params) {
    const url = `${config.workbenchUrl}/api/workbench/businessWorkTeamDetail/query`;
    return httpFetch.get(url, params);
  },
  /**
   * 按关键字获取查询员工数据
   * @param {*} params
   */
  getEmployeeBySearch(params) {
    const url = `${config.workbenchUrl}/api/workbench/businessWorkTeamDetail/query/keyword`;
    return httpFetch.get(url, params);
  },
  /**
   * 新增员工
   * @param {*} params
   */
  addEmployeeValue(params) {
    const url = `${config.workbenchUrl}/api/workbench/businessWorkTeamDetail/save`;
    return httpFetch.post(url, params);
  },
  /**
   * 删除员工
   * @param {*} id
   */
  deleteEmployeeValue(workTeamId, id) {
    const url = `${config.workbenchUrl}/api/workbench/businessWorkTeamDetail/delete?id=${id}`;
    return httpFetch.delete(url);
  },
  /**
   * 批量删除员工
   * @param {*} params
   */
  batchDeleteEmployeeValue(params) {
    const url = `${config.workbenchUrl}/api/workbench/businessWorkTeamDetail/delete/batch`;
    return httpFetch.delete(url, params);
  },
  /**
   * 查询给定工作组下所有员工的id
   */
  getAllUserByWorkTeamId(params) {
    const url = `${config.workbenchUrl}/api/workbench/businessWorkTeamDetail/query/all/ids`;
    return httpFetch.get(url, params);
  },
};

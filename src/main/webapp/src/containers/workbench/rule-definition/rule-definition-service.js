import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取业务类型
   */
  getBusinessType() {
    return httpFetch.get(`${config.workbenchUrl}/api/workbench/businessType/query/system/type`);
  },
  /**
   * 获取完成时执行
   */
  getEndProcedure(params) {
    return httpFetch.get(
      `${config.workbenchUrl}/api/workbench/businessProcedure/query/by/businessType`,
      params
    );
  },
  /**
   * 保存作业规则
   * @param {*} data
   */
  saveRuleDefinition(data) {
    return httpFetch.post(`${config.workbenchUrl}/api/workbench/businessRule/save`, data);
  },
  /**
   * 获取操作界面
   * @param {*} id
   */
  getInterfaceList(id) {
    return httpFetch.get(
      `${config.workbenchUrl}/api/workbench/businessPage/query/by/businessTypeId`,
      id
    );
  },
  /**
   * 动作过程
   */
  getActionProcedure() {
    return httpFetch.get(
      `${config.baseUrl}/api/custom/enumerations/template/by/type?type=WBC_ACTION_PROCEDURE`
    );
  },
  /**
   * 处理方式
   */
  getHandleMethod() {
    return httpFetch.get(
      `${config.baseUrl}/api/custom/enumerations/template/by/type?type=WBC_HANDLE_MODEL`
    );
  },
  /**
   * 获取节点动作表格行的 节点过程
   * @param {*} params
   */
  getNodeProcedureByAction(params) {
    return httpFetch.get(
      `${config.workbenchUrl}/api/workbench/businessNodeProcedure/query`,
      params
    );
  },
  /**
   * 保存节点信息
   * @param {*} params
   */
  saveNodeInfo(params) {
    return httpFetch.post(`${config.workbenchUrl}/api/workbench/businessNode/save`, params);
  },
  /**
   * 删除节点
   * @param {*} id
   */
  deleteNode(id) {
    return httpFetch.delete(`${config.workbenchUrl}/api/workbench/businessNode/${id}`);
  },
  /**
   * 改变节点顺序
   * @param {*} params
   */
  updateNodePriority(params) {
    return httpFetch.post(
      `${config.workbenchUrl}/api/workbench/businessNode/update/priority?businessRuleId=${
        params.businessRuleId
      }&oldPriority=${params.oldPriority}&newPriority=${params.newPriority}&nodeId=${params.nodeId}`
    );
  },
  /**
   * 获取作业规则下的节点
   * @param {*} id
   */
  getNodeList(id) {
    return httpFetch.get(`${config.workbenchUrl}/api/workbench/businessNode/list/node`, id);
  },
  /**
   * 获取节点的全部信息
   * @param {*} id
   */
  getNodeInfoDetail(id) {
    return httpFetch.get(`${config.workbenchUrl}/api/workbench/businessNode/query/detail`, id);
  },
  /**
   * 保存节点动作
   * @param {*} params
   */
  saveNodeAction(params) {
    return httpFetch.post(`${config.workbenchUrl}/api/workbench/businessNodeAction/save`, params);
  },
  /**
   * 删除节点动作
   * @param {*} actionId
   * @param {*} nodeId
   */
  deleteNodeAction(actionId, nodeId) {
    return httpFetch.delete(
      `${config.workbenchUrl}/api/workbench/businessNodeAction/${actionId}/${nodeId}`
    );
  },
  /**
   * 保存派工规则
   * @param {*} params
   */
  saveRule(params) {
    return httpFetch.post(
      `${config.workbenchUrl}/api/workbench/businessNodeTeamRule/insert?businessNodeTeamId=${
        params.businessNodeTeamId
      }`,
      params.dispatchRuleId
    );
  },
  /**
   * 获取工作组下 的规则
   * @param {*} id
   */
  getWorkTeamRule(id) {
    return httpFetch.get(
      `${config.workbenchUrl}/api/workbench/businessNodeTeamRule/query/nodeTeam/dispatchRule`,
      id
    );
  },
  /**
   * 删除派工规则
   * @param {*} id
   */
  deleteRule(id) {
    return httpFetch.delete(
      `${config.workbenchUrl}/api/workbench/businessNodeTeamRule/delete/by/${id}`
    );
  },
  /**
   * 添加节点派工方式
   * @param {*} params
   */
  saveNodeTasking(params) {
    return httpFetch.post(
      `${config.workbenchUrl}/api/workbench/businessNodeTeam/save/team/and/modVal`,
      params
    );
  },
  /**
   * 获取全部工作组数据
   * @param {*} id
   */
  getWorkTeamDetail(id) {
    return httpFetch.get(
      `${config.workbenchUrl}/api/workbench/businessNodeTeam/query/nodeTeamDetail`,
      id
    );
  },
  /**
   * 删除工作组
   * @param {*} id
   */
  deleteWorkTeam(id) {
    return httpFetch.delete(`${config.workbenchUrl}/api/workbench/businessNodeTeam/${id}`);
  },
  /**
   * 获取节点过程 表格数据
   * @param {*} id
   */
  getNodeActionList(id) {
    return httpFetch.get(
      `${config.workbenchUrl}/api/workbench/businessNodeAction/query/by/businessNodeId`,
      id
    );
  },
  /**
   * 获取规则 信息
   * @param {*} params
   */
  getRuleDetail(params) {
    return httpFetch.get(`${config.workbenchUrl}/api/workbench/businessRule/detail`, params);
  },
};

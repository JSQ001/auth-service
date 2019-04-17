import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取头信息
   */
  getHeaderInfo(headerId) {
    const url = `${config.contractUrl}/api/project/requisition/${headerId}`;
    return httpFetch.get(url);
  },
  /**
   * bottom-bar -保存
   */
  saveProject(params) {
    const url = ``;
    return httpFetch.post(url, params);
  },
  /**
   * bottom-bar -提交
   */
  submitProject(params) {
    const url = `${config.contractUrl}/api/project/requisition/approval/submit`;
    return httpFetch.post(url, params);
  },
  /**
   * bottom-bar -删除
   */
  deleteProject(id) {
    const url = `${config.contractUrl}/api/project/requisition/${id}`;
    return httpFetch.delete(url);
  },
  /**
   * 合同行信息删除
   */
  deleteContractValue(params) {
    const url = `${config.contractUrl}/api/project/requisition/contract?proReqId=${
      params.proReqId
    }&contractHeaderId=${params.contractHeaderId}`;
    return httpFetch.delete(url);
  },
  /**
   * 获取合同详情
   * @param {*} id
   */
  getContractHeaderInfo(id) {
    const url = `${config.contractUrl}/api/contract/header/${id}`;
    return httpFetch.get(url);
  },
  /**
   * 添加合同
   */
  saveContractList(params) {
    const url = `${config.contractUrl}/api/project/requisition/contract/batch`;
    return httpFetch.post(url, params);
  },
  /**
   * 新建或编辑项目申请详情
   * @param {*} params
   */
  saveDetailsValues(params) {
    const url = `${config.contractUrl}/api/project/requisition`;
    return httpFetch.put(url, params);
  },
  /**
   * 撤回
   * @param {*} params
   */
  withDrawApplyInfo(params) {
    const url = `${config.workflowUrl}/api/workflow/withdraw`;
    return httpFetch.post(url, params);
  },
};

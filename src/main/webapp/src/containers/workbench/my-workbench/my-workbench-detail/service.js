import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   *通过 头id 查询
   */
  getBase(params) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/report/header/by/id`, params);
  },
  /**
   * 获取按钮信息
   * @param {*} params
   */
  getButtonBar(params) {
    return httpFetch.get(
      `${config.workbenchUrl}/api/workbench/business/data/get/action/button`,
      params
    );
  },
  /**
   * 按钮操作
   * @param {*} params
   */
  operateButton(params) {
    return httpFetch.post(`${config.workbenchUrl}/api/workbench/business/data/execute`, params);
  },
  /**
   * 获取是否是自动阅单
   */
  getDocumentStatus() {
    return httpFetch.get(`${config.workbenchUrl}/api/workbench/business/data/get/status`);
  },
  /**
   * 改变阅单状态
   * @param {*} status
   */
  changeDocumentStatus(status) {
    return httpFetch.post(
      `${config.workbenchUrl}/api/workbench/business/data/start/automatic/${status}`
    );
  },
  /**
   * 暂挂
   * @param {*} param
   */
  hold(params) {
    return httpFetch.post(`${config.workbenchUrl}/api/workbench/business/data/hold`, params);
  },
  /**
   * 取消暂挂
   * @param {*} param
   */
  cancelhold(params) {
    return httpFetch.post(`${config.workbenchUrl}/api/workbench/business/data/cancel/hold`, params);
  },
};

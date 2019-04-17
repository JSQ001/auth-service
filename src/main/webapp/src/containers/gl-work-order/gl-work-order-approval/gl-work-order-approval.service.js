import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取未审批以及已审批
   */
  getList(params) {
    return httpFetch.get(
      `${config.accountingUrl}/api/general/ledger/work/order/head/approvals/filters`,
      params
    );
  },
  /**
   * 审批通过
   */
  pass(params) {
    return httpFetch.post(`${config.baseUrl}/api/workflow/pass`, params);
  },
  /**
   * 审批驳回
   */
  reject(params) {
    return httpFetch.post(`${config.baseUrl}/api/workflow/reject`, params);
  },
};

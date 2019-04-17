import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 创建凭证
   * @param {*} params
   */
  createEvidence(params) {
    const url = ``;
    return httpFetch.post(url, params);
  },
  /**
   * 审核通过
   */
  passCurApproval(id, params) {
    const url = `${config.expenseUrl}/api/input/header/updateStatus?id=${id}&status=1004`;
    return httpFetch.post(url, params);
  },
  /**
   * 审核拒绝
   */
  refuseCurApproval(id, params) {
    const url = `${config.expenseUrl}/api/input/header/updateStatus?id=${id}&status=1005`;
    return httpFetch.post(url, params);
  },
};

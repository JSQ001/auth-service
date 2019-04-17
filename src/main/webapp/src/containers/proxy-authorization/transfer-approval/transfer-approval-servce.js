import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 新增转交
   * @param {*} params
   */
  addPassingSetting(params) {
    return httpFetch.post(`${config.workflowUrl}/api/workflow/transfer`, params);
  },
  /**
   * 更改转交
   * @param {*} params
   */
  editPassingSetting(params) {
    return httpFetch.put(`${config.workflowUrl}/api/workflow/transfer`, params);
  },
};

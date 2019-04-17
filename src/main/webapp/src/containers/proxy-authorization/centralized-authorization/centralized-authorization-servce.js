import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 新增集体代理
   * @param {*} params
   */
  addCentralizedSetting(params) {
    return httpFetch.post(`${config.mdataUrl}/api/authorize/form/centralized/auth`, params);
  },
  /**
   * 更改集体代理
   * @param {*} params
   */
  editCentralizedSetting(params) {
    return httpFetch.put(`${config.mdataUrl}/api/authorize/form/centralized/auth`, params);
  },
};

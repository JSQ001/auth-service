import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取付款安全策略信息
   */
  getPaymentSecurityQuery() {
    return httpFetch.get(`${config.fundUrl}/api/payment/security/strategy/query`);
  },

  /**
   * 付款安全策略保存
   */
  savePaymentSecurity(record) {
    return httpFetch.post(`${config.fundUrl}/api/payment/security/strategy/createorupdate`, record);
  },
};

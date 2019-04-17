import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取支付列表数据
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   * @param {*} status
   */
  getPayList(page, size, searchParams, status) {
    // let url = `http://10.211.97.86:9099/api/payment/baseInfo/pageGetPaymentList?queryType=${status}`;
    let url = `${
      config.fundUrl
    }/api/payment/baseInfo/pageGetPaymentList?page=${page}&size=${size}&queryType=${status}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 发送银行
   */
  sendBank(id) {
    const url = `${config.fundUrl}/api/payment/baseInfo/sendBank/${id}`;
    // const url = `http://10.211.97.86:9099/api/payment/baseInfo/sendBank/${id}`;
    return httpFetch.post(url);
  },

  /**
   * 线下付款
   */
  unlinePay(id) {
    const url = `${config.fundUrl}/api/payment/baseInfo/offlinePayment/${id}`;
    // const url = `http://10.211.97.86:9099/api/payment/baseInfo/offlinePayment/${id}`;
    return httpFetch.post(url);
  },

  /**
   * 发送银企直联结果更新
   */
  sendBankCompany(id) {
    const url = `${config.fundUrl}/api/payment/baseInfo/linkBank/${id}`;
    // const url = `http://10.211.97.86:9099/api/payment/baseInfo/linkBank/${id}`;
    return httpFetch.post(url);
  },

  /**
   * 删除按钮
   */
  deleteAccount(id) {
    const url = `${config.fundUrl}/api/payment/baseInfo/batch/delete`;
    // const url = `http://10.211.97.86:9099/api/payment/baseInfo/batch/delete`;
    return httpFetch.delete(url, id);
  },

  /**
   * 明细页查询
   */
  getManualList(page, size, id) {
    const url = `${
      config.fundUrl
    }/api/payment/lineInfo/query?page=${page}&size=${size}&paymentBaseId=${id}`;
    // const url = `http://10.211.97.86:9099/api/payment/lineInfo/query?page=${page}&size=${size}&paymentBaseId=${id}`;
    return httpFetch.get(url);
  },

  /**
   * 获取付款单列表页面
   */
  getPaymentQueryList(page, size, searchParams) {
    // console.log(searchParams);
    let url = `${config.fundUrl}/api/payment/baseInfo/pageByCondition?page=${page}&size=${size}`;
    // let url = `http://10.211.97.86:9099/api/payment/baseInfo/pageByCondition?page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 付款明细支付中的修改保存请求
   */
  updateSave(params) {
    const url = `${config.fundUrl}/api/payment/lineInfo/updatePaymentState`;
    // const url = `http://10.211.97.86:9099/api/payment/lineInfo/updatePaymentState`;
    return httpFetch.put(url, params);
  },
};

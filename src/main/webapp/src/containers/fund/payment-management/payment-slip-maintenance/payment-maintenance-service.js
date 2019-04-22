import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 模糊查询付款记录
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getGatherAccountList() {
    const url = `${
      config.fundUrl
    }/api/payment/record/getPaymentRecordByGatherAccount?gatherAccount=`;
    return httpFetch.get(url);
  },

  /**
   * 获取自动付款规则数据列表{新建，拒绝，收回}
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getPaymentQueryList(page, size, searchParams) {
    // console.log(searchParams);
    // let url = `${
    //   config.fundUrl
    // }/api/payment/baseInfo/pageByConditionMaint?page=${page}&size=${size}`;
    let url = `${
      config.fundUrl
    }/api/payment/baseInfo/pageByConditionMaint?page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 查所有
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getPaymentQueryListAll(page, size, searchParams) {
    // console.log(searchParams);
    // let url = `${
    //   config.fundUrl
    // }/api/payment/baseInfo/pageByConditionMaint?page=${page}&size=${size}`;
    let url = `${config.fundUrl}/api/payment/baseInfo/pageByCondition?page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 获取账户列表页面
   */
  getAccountList(page, size, searchParams) {
    let url = `${
      config.fundUrl
    }/api/account/maintain/normal/pageByConditionMaint?page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 付款单维护删除
   * @param {*} data 删除的数据
   */
  deleteAccount(headDeleteList) {
    // const url = `${config.fundUrl}/api/payment/baseInfo/batch/delete`;
    const url = `${config.fundUrl}/api/payment/baseInfo/batch/delete`;
    // const url = `http://10.211.97.86:9099/api/payment/baseInfo/batch/delete`;
    return httpFetch.delete(url, headDeleteList);
  },

  /**
   * 手工付款为维护查询
   */
  getManualList(page, size, paymentBaseId) {
    // getManualList() {
    const url = `${
      config.fundUrl
    }/api/payment/lineInfo/query?page=${page}&size=${size}&paymentBaseId=${paymentBaseId}`;
    // const url = `http://10.211.97.86:9099/api/payment/lineInfo/query?page=${page}&size=${size}&paymentBaseId=${paymentBaseId}`;
    return httpFetch.get(url);
  },

  /**
   * 手工付款维护删除行
   */
  deleteManualList(deleteIdList) {
    const url = `${config.fundUrl}/api/payment/lineInfo/deleteList`;
    // const url = `http://10.211.97.86:9099/api/payment/lineInfo/deleteList`;
    return httpFetch.delete(url, deleteIdList);
  },

  /**
   * 手工付款维护整单删除
   */
  deleteAllManualList(headId) {
    const arr = [];
    arr.push(headId);
    const url = `${config.fundUrl}/api/payment/baseInfo/batch/delete`;
    // const url = `${config.fundUrl}/api/payment/baseInfo/batch/deleteone`;
    return httpFetch.delete(url, arr);
  },

  /**
   * 获取银行账户列表
   */
  getAccountOpenMaintenanceList(page, size, accountNumber = '') {
    const url = `${
      config.fundUrl
    }/api/account/maintain/normal/pageByCondition?page=${page}&size=${size}&accountNumber=${accountNumber}`;
    return httpFetch.get(url);
  },

  /**
   * 获取开户银行列表
   */
  getAccountBank() {
    return httpFetch.get(`${config.fundUrl}/api/account/open/codeList/ZJ_OPEN_BANK`);
  },

  /**
   * 获取公司列表
   */
  getCompany() {
    return httpFetch.get(`${config.mdataUrl}/api/company/by/condition`);
  },

  /**
   * 手工付款单维护保存
   */
  saveMaintainData(saveList) {
    const url = `${config.fundUrl}/api/payment/baseInfo`;
    // const url = `http://10.211.97.86:9099/api/payment/baseInfo`;
    return httpFetch.post(url, saveList);
  },

  /**
   * 付款单行提交
   */
  submit(submitId) {
    const url = `${config.fundUrl}/api/payment/baseInfo/submit/${submitId}`;
    return httpFetch.put(url);
  },

  /**
   * 付款单头批量提交
   */
  submitAll(deleteIdList) {
    const url = `${config.fundUrl}/api/payment/baseInfo/submitList`;
    // const url = `http://10.211.97.86:9099/api/payment/baseInfo/submitList`;
    return httpFetch.put(url, deleteIdList);
  },

  /**
   * 获取现金流量项
   */
  getCashFlowItem(params) {
    return httpFetch.get(`${config.payUrl}/api/cash/flow/items/query?setOfBookId=${params}`);
  },
};

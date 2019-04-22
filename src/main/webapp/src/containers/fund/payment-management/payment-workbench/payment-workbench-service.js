import httpFetch from 'share/httpFetch';
import config from 'config';

// const testUrl = 'http://10.211.98.2:9099';
export default {
  /**
   *
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  accountInformationList(page, size, searchParams) {
    let url = `${
      config.fundUrl
    }/api/account/query/right/pageByCondition?&page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },
  /**
   * 资金付款工作台列表查询
   */
  getPaymentWorkbenchList(page, size, searchParams) {
    let url = `${
      config.fundUrl
    }/api/payment/interface/pageByConditionCount?page=${page}&size=${size}`;

    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },
  /**
   * 创建批
   * @param {*} list
   */
  createBatch(list) {
    return httpFetch.put(`${config.fundUrl}/api/payment/generateBatch/insert`, list);
  },

  /**
   * 单据锁定
   */
  documentLock(list) {
    return httpFetch.put(`${config.fundUrl}/api/payment/interface/documentLock`, list);
    // return httpFetch.put(`${testUrl}/api/payment/interface/documentLock`, list);
  },

  /**
   * 根据规则创建批
   * @param {*} list
   */
  allCreate(searchParams) {
    let url = `${config.fundUrl}/api/payment/interface/generateBatchByRule?`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 单据解锁
   */
  documentUnlock(list) {
    return httpFetch.put(`${config.fundUrl}/api/payment/interface/documentUnlock`, list);
  },

  /**
   * 单据退回
   */
  documentBack(list, reason) {
    return httpFetch.put(
      `${config.fundUrl}/api/payment/interface/documentBack?reason=${reason}`,
      list
    );
  },

  /**
   * 获取创建批列表数据
   */
  getCreateBatchList() {
    return httpFetch.get(`${config.fundUrl}/api/payment/generateBatch/getTempInfo`);
  },

  /**
   * 组批界面删除行
   */
  generateBatchDelete(list) {
    // return httpFetch.delete(`${testUrl}/api/payment/generateBatch/delete`, list);
    return httpFetch.delete(`${config.fundUrl}/api/payment/generateBatch/delete`, list);
  },

  /**
   * 组批界面保存
   */
  generateBatchSave(record) {
    return httpFetch.post(`${config.fundUrl}/api/payment/generateBatch/save`, record);
    // return httpFetch.post(`http://10.211.97.86:9099/api/payment/generateBatch/save`, record);
  },

  /**
   * 组批界面整单删除
   */
  generateBatchDeleteAll() {
    return httpFetch.delete(`${config.fundUrl}/api/payment/generateBatch/deleteAll`);
  },

  /**
   * 提交
   */
  generateSubmit(id) {
    return httpFetch.put(`${config.fundUrl}/api/payment/baseInfo/submit/${id}`);
  },
};

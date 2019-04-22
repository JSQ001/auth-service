import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * hi hi
   * 根据头id 删除调拨申请单
   * @param {*} list
   */
  deleteList(list) {
    return httpFetch.delete(`${config.fundUrl}/api/cp/transfer/appl/header/deleteList`, list);
  },

  /**
   * 获取调拨申请头列表
   * @param {*} page
   * @param {*} size
   * @param {*} searchParams
   */
  getTransferApplHeader(page, size, searchParams, queryFlag) {
    // let url = `http://10.211.97.86:9099/api/cp/transfer/appl/header/getTransferApplHeader?&page=${page}&size=${size}&queryType=${queryFlag}`;
    let url = `${
      config.fundUrl
    }/api/cp/transfer/appl/header/getTransferApplHeader?&page=${page}&size=${size}&queryType=${queryFlag}`;

    // let url = `${config.fundUrl}/api/cp/transfer/appl/header/getTransferApplHeader?&page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 插入更新调拨申请
   * @param {*} record
   */
  createOrUpdate(record) {
    // return httpFetch.post(`${config.fundUrl}/api/cp/transfer/appl/header/createOrUpdate`, record);
    return httpFetch.post(
      // `http://10.211.97.86:9099/api/cp/transfer/appl/header/createOrUpdate`,
      `${config.fundUrl}/api/cp/transfer/appl/header/createOrUpdate`,
      record
    );
  },

  /**
   * 明细页删除行
   */
  deleteManualList(deleteIdList) {
    const url = `${config.fundUrl}/api/cp/transfer/appl/line/deleteList`;
    // const url = `http://10.211.97.86:9099/api/cp/transfer/appl/line/deleteList`;
    return httpFetch.delete(url, deleteIdList);
  },

  /**
   * 插入账户信息申请
   */
  insertAccountModifyDetail(record) {
    // return httpFetch.post(`http://10.211.97.86:9099/api/account/change`, record);
    return httpFetch.post(`${config.fundUrl}/api/account/change`, record);
  },

  /**
   * 根据ID获取账户变更详情
   */
  getModifyAccountDetail(id) {
    //
    return httpFetch.get(`${config.fundUrl}/api/cp/transfer/appl/line/getTransferApplLine/${id}`);
    // return httpFetch.get(
    //   `http://10.211.97.86:9099/api/cp/transfer/appl/line/getTransferApplLine/${id}`
    // );
  },

  /**
   * 调拨申请单提交
   */
  submit(id) {
    return httpFetch.put(`${config.fundUrl}/api/cp/transfer/appl/header/submit/${id}`);
    // return httpFetch.put(`http://10.211.97.86:9099/api/cp/transfer/appl/header/submit/${id}`);
  },

  close(id) {
    return httpFetch.put(`${config.fundUrl}/api/cp/transfer/appl/line/close/${id}`);
    // return httpFetch.put(`http://10.211.97.86:9099/api/cp/transfer/appl/line/close/${id}`);
  },
};

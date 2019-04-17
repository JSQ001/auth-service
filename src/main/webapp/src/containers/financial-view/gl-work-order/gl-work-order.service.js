import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 首页查询
   */
  workOrderFinanceHeadQuery(params) {
    return httpFetch.get(
      `${config.accountingUrl}/api/general/ledger/work/order/head/finance/query`,
      params
    );
  },

  /**
   * 根据头id获取单据头信息
   */
  getHeaderData(id, page, size) {
    return httpFetch.get(
      `${config.accountingUrl}/api/general/ledger/work/order/head/${id}?page=${page}&size=${size}`
    );
  },
  /**
   * 导出
   */
  export(params, exportParams) {
    let url = `${
      config.accountingUrl
    }/api/general/ledger/work/order/head/finance/export?roleType=TENANT`;
    for (let searchName in exportParams) {
      url += exportParams[searchName] ? `&${searchName}=${exportParams[searchName]}` : '';
    }
    return httpFetch.post(url, params, {}, { responseType: 'arraybuffer' });
  },
  /**
   * 获取审批历史
   */
  getHistory(documentOid) {
    return httpFetch.get(
      `${
        config.workflowUrl
      }/api/workflow/approval/history?entityType=801008&entityOid=${documentOid}`
    );
  },
};

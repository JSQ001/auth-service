import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  // 新增价税分离
  insertInvoicingSite(params) {
    return httpFetch.post(`${config.taxUrl}/api/tax/vat/separate/rule`, params);
  },

  // 修改价税分离
  updateInvoicingSite(params) {
    return httpFetch.put(`${config.taxUrl}/api/tax/vat/separate/rule`, params);
  },
  // 删除价税分离
  delectInvoicingSite(id) {
    return httpFetch.delete(`${config.taxUrl}/api/tax/vat/rule/dimension/${id}`);
  },

  // 获取价税分离
  pageInvoicingSiteByCond() {
    return httpFetch.get(`${config.taxUrl}/api/tax/vat/separate/rule/query/condition`);
  },
  // 获取动态列
  getColumns() {
    return httpFetch.get(
      `${config.taxUrl}/api/tax/vat/rule/dimension/query/condition?ruleCode=VAT_SEPARATE_RULE`
    );
  },
  // 获取维度下拉列表
  getSystemValueList() {
    return httpFetch.get(`${config.mdataUrl}/api/dimension/page/by/cond`);
  },
  // 获取维值下拉列表
  getSystemValueList1(id) {
    return httpFetch.get(
      `${
        config.mdataUrl
      }/api/dimension/item/page/by/dimensionId?roleType=TENANT&page=0&size=10&dimensionId=${id}&enabled=true`
    );
  },

  // 导出自定义银行：接口测试ok
  exportSelfTax(result, ps, exportParams) {
    let url = `${config.taxUrl}/api/tax/taxRegister/export/data?page=${ps.page}&size=${ps.size}`;
    // eslint-disable-next-line guard-for-in
    for (const searchName in exportParams) {
      url += exportParams[searchName] ? `&${searchName}=${exportParams[searchName]}` : '';
    }
    return new Promise((resolve, reject) => {
      httpFetch
        .post(url, result, {}, { responseType: 'arraybuffer' })
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          // eslint-disable-next-line no-undef
          errorMessage(err.response);
          reject(err);
        });
    });
  },
  /**
   * 确认导入
   * @param {*} transactionId
   */
  confirmImporter(transactionId) {
    return httpFetch.post(`${config.mdataUrl}/api/user/import/new/confirm/${transactionId}`);
  },
};

import httpFetch from 'share/httpFetch';
import config from 'config';

export default {
  /**
   * 获取发票数据
   * @param {*} params
   */
  getInvoiceValues(params) {
    const url = `${config.expenseUrl}/api/invoice/type/query`;
    return httpFetch.get(url, params);
  },

  /**
   * 新增发票数据
   * @param {*} params
   */
  addInvoiceValues(params) {
    const url = `${config.expenseUrl}/api/invoice/type`;
    return httpFetch.post(url, params);
  },
  /**
   * 编辑发票数据
   * @param {*} params
   */
  editInvoiceValues(params) {
    const url = `${config.expenseUrl}/api/invoice/type`;
    return httpFetch.put(url, params);
  },
  /**
   * 获取模板数据
   * @param {*} invoiceTypeId
   */
  getInvoiceTemplate(invoiceTypeId) {
    const url = `${config.expenseUrl}/api/invoice/type/mould/query/${invoiceTypeId}`;
    return httpFetch.get(url);
  },
  /**
   * 新增/修改模板数据
   * @param {*} params
   */
  updateInvoiceTemplate(params) {
    const url = `${config.expenseUrl}/api/invoice/type/mould/insertOrUpdate`;
    return httpFetch.post(url, params);
  },
  /**
   * 获取所有账套
   * @param {*} params
   */
  getSetOfBooksValue(params = {}) {
    const url = `${config.mdataUrl}/api/setOfBooks/query/dto`;
    return httpFetch.get(url, params);
  },
};

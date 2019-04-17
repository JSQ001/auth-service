import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取发票类型
   */
  getInvoiceType() {
    return httpFetch.get(`${config.expenseUrl}/api/invoice/type/sob/tenant/query`);
  },
  /**
   * 获取新增发票的模板信息
   * @param {*} id
   */
  getInvoiceTemplate(id) {
    return httpFetch.get(`${config.expenseUrl}/api/invoice/type/mould/query/${id}`);
  },
  /**
   * 添加发票 保存
   * @param {*} params
   */
  invoiceSave(params) {
    return httpFetch.post(`${config.expenseUrl}/api/invoice/head/insert/invoice`, params);
  },
  /**
   * 删除发票
   * @param {*} ids
   */
  deleteInvoice(ids) {
    return httpFetch.delete(`${config.expenseUrl}/api/invoice/head/delete/invoice/by/headIds`, ids);
  },
  /**
   * 验真发票
   * @param {*} ids
   */
  validateInvoice(ids) {
    return httpFetch.post(`${config.expenseUrl}/api/invoice/head/check/invoice/by/headIds`, ids);
  },
  /**
   * 导出发票信息
   * @param {*} params
   */
  exportInvoiceInfo(params, id) {
    const url = `${config.expenseUrl}/api/invoice/head/export/invoice/head/info?createdBy=${id}`;
    return httpFetch.post(url, params, {}, { responseType: 'arraybuffer' });
  },
  /**
   * 获取发票详细信息
   * @param {*} params
   */
  getInvoiceInfo(id) {
    return httpFetch.get(`${config.expenseUrl}/api/invoice/head/${id}`);
  },
  /**
   * 导出发票报账明细
   * @param {*} params
   */
  exportInvoiceDetail(params, id) {
    const url = `${
      config.expenseUrl
    }/api/invoice/head/export/invoice/line/dist/info?createdBy=${id}`;
    return httpFetch.post(url, params, {}, { responseType: 'arraybuffer' });
  },
  /**
   * 获取币种
   */
  getCurrencyType() {
    return httpFetch.get(`${config.mdataUrl}/api/currency/rate/company/standard/currency/getAll`);
  },
  /**
   * 获取税率
   */
  getTaxRate() {
    return httpFetch.get(
      `${config.baseUrl}/api/custom/enumerations/template/by/type?type=TAX_RATE`
    );
  },
  /**
   * 校验发票头代码、号码
   * @param {*} params
   */
  validateInvoiceCode(params) {
    return httpFetch.get(
      `${config.expenseUrl}/api/invoice/head/check/invoiceCode/invoiceNo`,
      params
    );
  },
};

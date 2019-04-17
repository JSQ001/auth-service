import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  //获取币种
  getCurrency() {
    return httpFetch.get(`${config.mdataUrl}/api/currency/rate/company/standard/currency/getAll`);
  },
  //获取税率
  getTaxRate() {
    return httpFetch.get(
      `${config.baseUrl}/api/custom/enumerations/template/by/type?type=TAX_RATE`
    );
  },
  getInvoiceInfo(id) {
    return httpFetch.get(`${config.expenseUrl}/api/invoice/head/${id}`);
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

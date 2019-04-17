import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  // 新增发票管理配置
  insertTaxVatInvoiceInfo(params) {
    return httpFetch.post(`${config.taxUrl}/api/tax/vat/invoice/info`, params);
  },

  // 修改发票管理配置
  updateTaxVatInvoiceInfo(params) {
    return httpFetch.put(`${config.taxUrl}/api/tax/vat/invoice/info`, params);
  },

  // 根据ID获取发票管理配置
  getTaxVatInvoiceInfoById(params) {
    return httpFetch.get(`${config.taxUrl}/api/tax/vat/invoice/info`, params);
  },

  // 根据纳税主体ID获取发票管理配置
  pageTaxVatInvoiceInfoByCond(id) {
    return httpFetch.get(
      `${config.taxUrl}/api/tax/vat/invoice/info/pageByCondition?taxpayerId=${id}`
    );
  },
};

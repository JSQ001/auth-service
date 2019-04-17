import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  // 条件查询交易明细数据
  getTaxVatTranInterfaceByCond() {
    return httpFetch.get(`${config.taxUrl}/api/tax/vat/trans/interface/pageByCondition`);
  },
  // 分页查询方法
  getCangeRecord(params) {
    return httpFetch.get(`${config.taxUrl}/api/tax/vat/trans/interface/pageByCondition`, params);
  },
  // 新增开票点
  insertInvoicingSite(params) {
    return httpFetch.post(`${config.taxUrl}/api/invoicing/site`, params);
  },

  // 修改开票点
  updateInvoicingSite(params) {
    return httpFetch.put(`${config.taxUrl}/api/invoicing/site`, params);
  },

  // 条件查询开票点信息
  pageInvoicingSiteByCond(params) {
    return httpFetch.get(`${config.taxUrl}/api/invoicing/site/pageByCondition`, params);
  },

  // 根据开票点ID查询开票点详情
  getInvoicingSiteById(id) {
    return httpFetch.get(`${config.taxUrl}/api/invoicing/site/${id}`);
  },

  // 根据税种ID查询税率详情
  // pageTaxRateByTaxCategoryId(taxCategoryId) {
  //   return httpFetch.get(`${config.taxUrl}/api/tax/rate/pageTaxRateByTaxCategoryId/${taxCategoryId}`);
  // },

  // 根据税种ID查询税率详情
  //   getTaxCategory(taxCategoryId) {
  //     return httpFetch.get(`${config.taxUrl}/api/tax/rate/details/${taxCategoryId}`);
  //   },
  // 导出价税分离数据查询信息
  exportSelfTax(result) {
    const url = `${config.taxUrl}/api/invoicing/site/export/data`;
    // eslint-disable-next-line guard-for-in
    // for (const searchName in exportParams) {
    //   url += exportParams[searchName] ? `&${searchName}=${exportParams[searchName]}` : '';
    // }
    return httpFetch.post(url, result, {}, { responseType: 'arraybuffer' });
  },
};

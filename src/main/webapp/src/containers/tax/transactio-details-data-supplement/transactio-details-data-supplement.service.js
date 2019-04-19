import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  // 条件查询交易明细数据
  getTaxVatTranInterfaceByCond() {
    return httpFetch.get(`${config.taxUrl}/api/tax/vat/trans/interface/pageByCondition`);
  },
  // 分页查询方法
  getCangeRecord(params) {
    return httpFetch.get(
      `${config.taxUrl}/api/tax/vat/trans/interface/pageByCondition?errorFlag=E`,
      params
    );
  },
  // 提交方法
  submitData(params) {
    return httpFetch.post(`${config.taxUrl}/api/tax/vat/trans/interface/submit`, params);
  },
  // 获取动态列
  getColumns() {
    return httpFetch.get(
      `${config.taxUrl}/api/tax/vat/rule/dimension/query/condition?ruleCode=VAT_SEPARATE_RULE`
    );
  },

  // 修改开票点
  updateInvoicingSite(params) {
    return httpFetch.put(`${config.taxUrl}/api/tax/vat/trans/interface`, params);
  },

  // 条件查询开票点信息
  pageInvoicingSiteByCond(params) {
    return httpFetch.get(`${config.taxUrl}/api/invoicing/site/pageByCondition`, params);
  },

  // 根据开票点ID查询开票点详情
  getInvoicingSiteById(id) {
    return httpFetch.get(`${config.taxUrl}/api/invoicing/site/${id}`);
  },
  // 获取币种下拉列表
  getSystemValueList1(id) {
    return httpFetch.get(
      `${config.mdataUrl}/api/currency/rate/company/standard/currency/getAll?setOfBooksId=${id}`
    );
  },
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

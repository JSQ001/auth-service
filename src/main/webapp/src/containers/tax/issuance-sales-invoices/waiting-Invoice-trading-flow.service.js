import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  // 查询
  getWaitInvoiceList(params) {
    return httpFetch.get(`${config.taxUrl}/tax/vat/tran/invoice/detail/data/query`, params);
  },
  routerNewPage(list) {
    return httpFetch.post(`${config.taxUrl}/tax/vat/tran/invoice/detail/create/application`, list);
  },
  // 获取动态列
  getColumns() {
    return httpFetch.get(
      `${config.taxUrl}/api/tax/vat/rule/dimension/query/condition?ruleCode=VAT_SEPARATE_RULE`
    );
  },
  saveWaitInvoice(params) {
    return httpFetch.post(`${config.taxUrl}/tax/vat/tran/invoice/detail/data/update`, params);
  },
};

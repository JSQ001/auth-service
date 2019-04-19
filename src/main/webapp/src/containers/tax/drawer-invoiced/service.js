import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  // 获取开票规则定义
  getInvoicexRuleList(params) {
    return httpFetch.get(`${config.taxUrl}/api/tax/vat/invoice/rule/select/page`, params);
  },
  //编辑开票规则
  updateInvoiceRule(params) {
    return httpFetch.post(`${config.taxUrl}/api/tax/vat/invoice/rule/update/data`, params);
  },
  // 获取维度下拉列表
  getSystemValueList() {
    return httpFetch.get(`${config.mdataUrl}/api/dimension/page/by/cond`);
  },
  // 获取动态列
  getColumns() {
    return httpFetch.get(
      `${config.taxUrl}/api/tax/vat/rule/dimension/query/condition?ruleCode=VAT_SEPARATE_RULE`
    );
  },
  //导出
  exportSelfTax(result) {
    const url = `${config.taxUrl}/api/tax/vat/invoice/rule/export/data`;
    return httpFetch.post(url, result, {}, { responseType: 'arraybuffer' });
  },
};

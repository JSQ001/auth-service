import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  // 分页查询方法
  getCangeRecord(params) {
    return httpFetch.get(
      `${config.taxUrl}/api/tax/vat/output/detail/pageByCondition?&page=0&size=10&roleType=TENANT`,
      params
    );
  },

  // 修改开票点
  updateInvoicingSite(params) {
    return httpFetch.put(`${config.taxUrl}/api/invoicing/site`, params);
  },
  // 获取动态列
  getColumns() {
    return httpFetch.get(
      `${config.taxUrl}/api/tax/vat/rule/dimension/query/condition?ruleCode=VAT_SEPARATE_RULE`
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

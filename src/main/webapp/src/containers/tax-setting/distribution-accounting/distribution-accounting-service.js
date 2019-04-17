import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  // 新增核算主体分配
  insertTaxTaxpayerOrg(params) {
    return httpFetch.post(`${config.taxUrl}/api/tax/taxpayer/org/insert/data`, params);
  },

  // 根据id删除数据
  deleteTaxTaxpayerOrgById(id) {
    return httpFetch.delete(`${config.taxUrl}/api/tax/taxpayer/org/${id}`);
  },
  // 批量删除
  deleteTaxTaxpayerOrgBatch(ids) {
    return httpFetch.delete(`${config.taxUrl}/api/tax/taxpayer/org/batch/delete`, ids);
  },

  // 修改开票点
  // updateInvoicingSite(params) {
  //   return httpFetch.put(`${config.taxUrl}/api/invoicing/dimension`, params);
  // },

  // 根据纳税主体ID获取核算主体信息
  pageTaxTaxpayerOrgByCond(params) {
    return httpFetch.get(`${config.taxUrl}/api/tax/taxpayer/org/pageByCondition`, params);
  },
};

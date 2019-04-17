import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  // 新增税率
  insertTaxRate(params) {
    return httpFetch.post(`${config.taxUrl}/api/tax/rate`, params);
  },

  // 修改税率
  updateTaxRate(params) {
    return httpFetch.put(`${config.taxUrl}/api/tax/rate`, params);
  },

  // 根据税率查询税率详情
  pageTaxRateByCond(params) {
    return httpFetch.get(`${config.taxUrl}/api/tax/rate/pageByCondition`, params);
  },

  // 根据税率ID查询税率详情
  getTaxRateById(id) {
    return httpFetch.get(`${config.taxUrl}/api/tax/rate/${id}`);
  },

  // 根据税种ID查询税率详情
  // pageTaxRateByTaxCategoryId(taxCategoryId) {
  //   return httpFetch.get(`${config.taxUrl}/api/tax/rate/pageTaxRateByTaxCategoryId/${taxCategoryId}`);
  // },

  // 根据税种ID查询税率详情
  getTaxCategory(taxCategoryId) {
    return httpFetch.get(`${config.taxUrl}/api/tax/rate/details/${taxCategoryId}`);
  },
};

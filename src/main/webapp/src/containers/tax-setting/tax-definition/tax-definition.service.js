import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  // 新增税种
  insertTaxCategory(params) {
    return httpFetch.post(`${config.taxUrl}/api/tax/category`, params);
  },

  // 修改税种
  updateTaxCategory(params) {
    return httpFetch.put(`${config.taxUrl}/api/tax/category`, params);
  },

  // 根据税种代码和税种名称查询税种详情
  pageTaxCategoryByCond(params) {
    return httpFetch.get(`${config.taxUrl}/api/tax/category/pageByCondition`, params);
  },

  // 根据税种ID查询税种详情
  getTaxCategoryById(id) {
    return httpFetch.get(`${config.taxUrl}/api/tax/category/${id}`);
  },
};

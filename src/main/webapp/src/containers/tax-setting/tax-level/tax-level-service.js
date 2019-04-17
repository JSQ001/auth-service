import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  // 新增增值税信息维护
  insertTaxValueAddedTaxInfo(params) {
    return httpFetch.post(`${config.taxUrl}/api/tax/value/added/tax/info`, params);
  },

  // 修改增值税信息维护
  updateTaxValueAddedTaxInfo(params) {
    return httpFetch.put(`${config.taxUrl}/api/tax/value/added/tax/info`, params);
  },

  // 行政层级
  pageTaxValueAddedTaxInfoByCond() {
    return httpFetch.get(`${config.taxUrl}/api/tax/taxRegister/basic//hierarchy/register`);
  },
  // 增值税层级
  pageTaxValueAddedTaxInfoByCond1() {
    return httpFetch.get(`${config.taxUrl}/api/tax/taxRegister/basic//hierarchy/added`);
  },

  // 根据ID获取增值税信息
  getTaxValueAddedTaxInfoById(id) {
    return httpFetch.get(`${config.taxUrl}/api/tax/value/added/tax/info/${id}`);
  },
};

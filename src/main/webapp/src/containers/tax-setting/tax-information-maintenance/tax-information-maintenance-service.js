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

  // 根据纳税主体ID获取增值税信息
  pageTaxValueAddedTaxInfoByCond(id) {
    return httpFetch.get(
      `${config.taxUrl}/api/tax/value/added/tax/info/pageByCondition?taxpayerId=${id}`
    );
  },

  // 根据ID获取增值税信息
  getTaxValueAddedTaxInfoById(id) {
    return httpFetch.get(`${config.taxUrl}/api/tax/value/added/tax/info/${id}`);
  },
};

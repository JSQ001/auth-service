import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  // 新增开票点
  insertInvoicingSite(params) {
    return httpFetch.post(`${config.taxUrl}/api/tax/taxRegister/basic/query/condition`, params);
  },

  // 修改开票点
  updateInvoicingSite(params) {
    return httpFetch.put(`${config.taxUrl}/api/tax/taxRegister/basic/query/condition`, params);
  },

  // 条件查询开票点信息
  pageInvoicingSiteByCond(params) {
    return httpFetch.get(
      `${config.taxUrl}/api/tax/taxRegister/basic/query/condition/pageByCondition`,
      params
    );
  },

  // 根据开票点ID查询开票点详情
  getInvoicingSiteById(id) {
    return httpFetch.get(`${config.taxUrl}/api/tax/taxRegister/basic/query/condition/${id}`);
  },

  // 根据税种ID查询税率详情
  // pageTaxRateByTaxCategoryId(taxCategoryId) {
  //   return httpFetch.get(`${config.taxUrl}/api/tax/rate/pageTaxRateByTaxCategoryId/${taxCategoryId}`);
  // },

  // 根据税种ID查询税率详情
  //   getTaxCategory(taxCategoryId) {
  //     return httpFetch.get(`${config.taxUrl}/api/tax/rate/details/${taxCategoryId}`);
  //   },
  // 导出自定义银行：接口测试ok
  exportSelfTax(result, ps, exportParams) {
    let url = `${config.taxUrl}/api/tax/taxRegister/export/data?page=${ps.page}&size=${ps.size}`;
    // eslint-disable-next-line guard-for-in
    for (const searchName in exportParams) {
      url += exportParams[searchName] ? `&${searchName}=${exportParams[searchName]}` : '';
    }
    return new Promise((resolve, reject) => {
      httpFetch
        .post(url, result, {}, { responseType: 'arraybuffer' })
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          // eslint-disable-next-line no-undef
          errorMessage(err.response);
          reject(err);
        });
    });
  },
};

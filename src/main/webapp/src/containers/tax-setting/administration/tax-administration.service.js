/**
 * Created by 5716 on 2019/3/7.
 */
import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取税务机关管理(分页)
   * */
  getSelfTaxList(params) {
    return httpFetch.get(`${config.taxUrl}/api/tax/department/page/data`, params);
  },

  /**
   * 保存税务机关管理
   * */
  addOrganManagement(params) {
    return httpFetch.post(`${config.taxUrl}/api/tax/department/insert/data`, params);
  },

  /**
   * 修改税务机关管理
   * */
  upDateOrganManagement(params) {
    return httpFetch.post(`${config.taxUrl}/api/tax/department/update/data`, params);
  },

  // 导出自定义银行：接口测试ok
  exportSelfTax(result, ps, exportParams) {
    let url = `${config.taxUrl}/api/tax/department/export/data?page=${ps.page}&size=${ps.size}`;
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

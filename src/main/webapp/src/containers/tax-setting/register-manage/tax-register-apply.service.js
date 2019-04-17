/**
 * Created by 5716 on 2019/3/7.
 */
import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取税务登记申请(分页)
   * */
  getSelfTaxList(params) {
    return httpFetch.get(`${config.taxUrl}/api/tax/taxRegister/apply/query/condition`, params);
  },

  /**
   * 保存税务登记
   * */
  addTax(parmas) {
    return httpFetch.post(`${config.taxUrl}/api/tax/taxRegister/apply/create`, parmas);
  },

  /**
   * 修改税务登记
   * */
  updateTax(parmas) {
    return httpFetch.post(`${config.taxUrl}/api/tax/taxRegister/apply/update`, parmas);
  },

  /**
   * 提交税务登记
   * */
  submitTax(parmas) {
    return httpFetch.post(`${config.taxUrl}/api/tax/taxRegister/submit`, parmas);
  },

  /**
   * 附件
   * */
  fileTax(parmas) {
    return httpFetch.post(`${config.taxUrl}/api/tax/taxRegister/submit`, parmas);
  },

  // 导出自定义银行：接口测试ok
  exportAppleTax(result, ps, exportParams) {
    let url = `${config.taxUrl}/api/tax/taxRegister/apply/export/data?page=${ps.page}&size=${
      ps.size
    }`;
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

  // 导出自定义银行：接口测试ok
  exportBasicTax(result, ps, exportParams) {
    let url = `${config.taxUrl}/api/tax/taxRegister/basic/export/data?page=${ps.page}&size=${
      ps.size
    }`;
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

  /**
   * 根据单据头ID查询单据头详情
   */
  getApplicationDetail(id) {
    return httpFetch.get(`${config.taxUrl}/api/tax/taxRegister/apply/${id}`);
  },

  /**
   * 获取税务登记申请(分页)
   * */
  getTaxSeeList(params) {
    return httpFetch.get(`${config.taxUrl}/api/tax/taxRegister/basic/query/condition`, params);
  },

  /**
   * 查看变更历史
   */
  getCangeRecord(id, params) {
    return httpFetch.get(`${config.taxUrl}/api/tax/taxRegister/apply/change/record/${id}`, params);
  },
};

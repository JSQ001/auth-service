import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 新增个人代理
   * @param {*} params
   */
  addPersonalSetting(params) {
    return httpFetch.post(`${config.mdataUrl}/api/authorize/form/personal/auth`, params);
  },
  /**
   * 更改个人代理
   * @param {*} params
   */
  editPersonalSetting(params) {
    return httpFetch.put(`${config.mdataUrl}/api/authorize/form/personal/auth`, params);
  },
  /**
   * 根据账套id获得单据大类
   * @param {*} id
   */
  getDocumentType(setOfBooksId) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/application/type/query/${setOfBooksId}`);
  },
};

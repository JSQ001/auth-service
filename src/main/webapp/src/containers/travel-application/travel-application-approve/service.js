import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 根据单据头ID查询单据头详情
   * @param {*} id
   */
  getApplicationDetail(id) {
    return httpFetch.get(`${config.expenseUrl}/api/travel/application/header/` + id);
  },
};

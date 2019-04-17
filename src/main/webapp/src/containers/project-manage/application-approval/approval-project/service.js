import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 查询当前机构下所有已创建的申请单的申请人（查询下拉框)
   * @param {*} params
   */
  getCreatedApplicationUserList() {
    return httpFetch.get(`${config.expenseUrl}/api/expense/application/header/query/created`);
  },
};

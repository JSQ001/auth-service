import config from 'config';
import httpFetch from 'share/httpFetch';
import SobService from 'containers/finance-setting/set-of-books/set-of-books.service';
import valueListService from 'containers/setting/value-list/value-list.service';

export default {
  /**
   * 获得当前租户下所有账套
   */
  getTenantAllSob(params) {
    return new Promise(function(resolve, reject) {
      SobService.getTenantAllSob(params)
        .then(function(res) {
          resolve(res);
        })
        .catch(function(err) {
          errorMessage(err.response);
          reject(err.response);
        });
    });
  },
  /**
   * 获得费用政策列表
   */
  getExpensePolicyList(params) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/policy/pageByCondition`, params);
  },

  /**
   * 保存费用政策
   */
  saveExpensePolicy(type, params) {
    return httpFetch[type](`${config.expenseUrl}/api/expense/policy`, params);
  },
  /**
   * 获取币种集合
   */
  getCurrency(params) {
    return httpFetch.get(`${config.mdataUrl}/api/currency/rate/list`, params);
  },

  getExpensePolicyById(id) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/policy/${id}`);
  },
  deletePolicy(id) {
    return httpFetch.delete(`${config.expenseUrl}/api/expense/policy/${id}`);
  },
};

import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取账户开户维护列表页面
   */
  getAccountOpenMaintenanceList(page, size, searchParams) {
    // http://10.211.110.57:8080
    // let url = `${config.fundUrl}/api/account/maintain/pageByCondition?&page=${page}&size=${size}`;
    let url = `http://10.211.110.57:8080/api/account/maintain/pageByCondition?&page=${page}&size=${size}`;
    const params = searchParams;
    for (const paramsName in params) {
      if (Object.prototype.hasOwnProperty.call(params, paramsName)) {
        url += params[paramsName] ? `&${paramsName}=${params[paramsName]}` : '';
      }
    }
    return httpFetch.get(url);
  },

  /**
   * 根据ID查询账户开户维护详情
   */
  getAccountOpenMaintenanceDetail(id) {
    return httpFetch.get(`${config.fundUrl}/api/account/maintain/${id}`);
  },

  /**
   * 更新账户开户详情页
   */
  updateAccountOpenMaintenanceDetail(record) {
    return httpFetch.put(`${config.fundUrl}/api/account/maintain`, record);
  },

  /**
   * 作废
   */
  obsolete(id) {
    return httpFetch.delete(`${config.fundUrl}/api/account/maintain/${id}`);
  },

  /**
   * 提交
   */
  submit(id) {
    return httpFetch.put(`${config.fundUrl}/api/account/maintain/submit/${id}`);
  },
};

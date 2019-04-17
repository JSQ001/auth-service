import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  // 新增客户权限
  insertClientOther(params) {
    return httpFetch.post(`${config.taxUrl}/api/tax/client/application/other/insert/data`, params);
  },

  deleteClientOtherBatch(ids) {
    return httpFetch.delete(`${config.taxUrl}/api/tax/client/application/other/batch/delete`, ids);
  },

  // 根据客户id获取开票权限信息
  pageClientOtherByCond(params) {
    return httpFetch.get(
      `${config.taxUrl}/api/tax/client/application/other/pageByCondition`,
      params
    );
  },
};

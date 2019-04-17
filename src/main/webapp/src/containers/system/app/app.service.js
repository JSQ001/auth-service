import config from 'config';
import httpFetch from 'utils/fetch';

export default {
  // 新建应用
  new(params) {
    return httpFetch.post(`${config.baseUrl}/api/application`, params);
  },
  // 编辑应用信息
  update(params) {
    return httpFetch.put(`${config.baseUrl}/api/application`, params);
  },
  // 获取应用列表
  getList(searchParams) {
    return httpFetch.get(`${config.baseUrl}/api/application`, searchParams);
  },

  // 获取应用列表all
  getAll(searchParams) {
    return httpFetch.get(`${config.baseUrl}/api/application/all`, searchParams);
  },
  // 获取应用服务列表
  getServiceList() {
    return httpFetch.get(`${config.baseUrl}/api/application/services`);
  },
};

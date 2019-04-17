import config from 'config';
import httpFetch from 'utils/fetch';

export default {
  // 新建
  add(params) {
    return httpFetch.post(`${config.baseUrl}/api/lov`, params);
  },
  // 编辑
  edit(params) {
    return httpFetch.put(`${config.baseUrl}/api/lov`, params);
  },
  // 获取列表
  getList(searchParams) {
    return httpFetch.get(`${config.baseUrl}/api/lov/page`, searchParams);
  },
  // 根据id获取详情
  getInfoById(id) {
    return httpFetch.get(`${config.baseUrl}/api/lov/` + id);
  },
  delete(id) {
    return httpFetch.delete(`${config.baseUrl}/api/lov/` + id);
  },
};

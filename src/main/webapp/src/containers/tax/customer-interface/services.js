import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  // 获取接口行详细信息
  getInterfaceDetail(id) {
    return httpFetch.get(`${config.baseUrl}/tax/api/tax/client/interface/` + id);
  },
};

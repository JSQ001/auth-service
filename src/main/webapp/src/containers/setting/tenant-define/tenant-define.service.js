import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  addTenant(params) {
    return httpFetch.post(`${config.baseUrl}/api/tenant/register`, params);
  },
};

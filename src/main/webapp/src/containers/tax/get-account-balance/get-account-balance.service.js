import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  getAccountBalance(params) {
    return httpFetch.get(`${config.taxUrl}/api/tax/acc/balance/interface/importData`, params);
  },
};

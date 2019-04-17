import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  baseInfoCreateOrUpdate(record) {
    // const url = `http://10.211.110.100:9099/api/cp/adjust/formal/baseInfo/createOrUpdate`;
    const url = `${config.fundUrl}/api/cp/adjust/formal/baseInfo/createOrUpdate`;
    return httpFetch.post(url, record);
  },
};

import config from 'config';
import httpFetch from 'share/httpFetch';

export async function queryMessages(local = 'zh_cn') {
  const params = {
    page: 0,
    size: 9999,
    lang: local,
  };
  return httpFetch.get(`${config.baseUrl}/api/frontKey/query/keyword`, params);
}

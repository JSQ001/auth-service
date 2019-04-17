import httpFetch from 'share/httpFetch';
import config from 'config';

export default {
  submit(values, callback) {
    httpFetch
      .post(`${config.authUrl}/api/data/auth/table/properties`, values)
      .then(res => {
        callback(true);
      })
      .catch(err => {
        callback(false);
      });
  },
};

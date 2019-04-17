import config from 'config';
import httpFetch from 'share/httpFetch';
export default {
  /**
   * 首页查询
   */
  getList(params) {
    return httpFetch.get(`${config.expenseUrl}/api/input/header/query/expinput/finance`, params);
  },

  export(params, exportParams) {
    let url = `${config.expenseUrl}/api/input/header/query/expinput/finance/export?`;
    for (let searchName in exportParams) {
      url += exportParams[searchName] ? `&${searchName}=${exportParams[searchName]}` : '';
    }
    return httpFetch.post(url, params, {}, { responseType: 'arraybuffer' });
  },
};

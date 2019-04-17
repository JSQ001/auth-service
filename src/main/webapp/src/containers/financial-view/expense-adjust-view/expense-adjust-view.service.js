import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 导出
   */
  export(params, exportParams) {
    debugger;
    let url = `${config.expenseUrl}/api/expense/adjust/headers/export/query/dto`;
    for (let searchName in exportParams) {
      url += exportParams[searchName] ? `&${searchName}=${exportParams[searchName]}` : '';
    }
    return httpFetch.post(url, params, {}, { responseType: 'arraybuffer' });
  },
};

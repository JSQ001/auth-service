import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 导出
   */
  export(params, exportParams) {
    let url = `${config.budgetUrl}/api/budget/journals/export/finance/query`;
    for (let searchName in exportParams) {
      url += exportParams[searchName] ? `&${searchName}=${exportParams[searchName]}` : '';
    }
    return httpFetch.post(url, params, {}, { responseType: 'arraybuffer' });
  },
};

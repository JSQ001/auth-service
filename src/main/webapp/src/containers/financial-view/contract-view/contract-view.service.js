import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 导出
   */
  export(params, exportParams) {
    let url = `${config.contractUrl}/api/contract/header/export/finance/query`;
    for (const searchName in exportParams) {
      url += exportParams[searchName] ? `&${searchName}=${exportParams[searchName]}` : '';
    }
    return httpFetch.post(url, params, {}, { responseType: 'arraybuffer' });
  },
};

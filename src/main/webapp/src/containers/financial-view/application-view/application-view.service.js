import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 首页查询
   */
  getList(params) {
    return httpFetch.get(
      `${config.expenseUrl}/api/expense/application/form/query/applicationFinancaiaList`,
      params
    );
  },
  /**
   * 导出
   */
  export(params, exportParams) {
    let url = `${
      config.expenseUrl
    }/api/expense/application/form/header/applicationFinancaia/export?`;
    for (let searchName in exportParams) {
      url += exportParams[searchName] ? `&${searchName}=${exportParams[searchName]}` : '';
    }
    return httpFetch.post(url, params, {}, { responseType: 'arraybuffer' });
  },
  /**
   * 查询费用报账单分摊行
   */
  getReportDist(params) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/report/getDistfromApplication`, params);
  },
};

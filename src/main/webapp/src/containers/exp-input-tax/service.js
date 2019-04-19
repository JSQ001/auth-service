import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 获取币种列表
   * @param {*} id
   */
  getCurrencyList(id) {
    return httpFetch.get(
      `${
        config.mdataUrl
      }/api/currency/rate/company/standard/currency/getAll/companyOid?companyOid=${id}`
    );
  },
  /**
   * 头保存更新
   * @param {*}  params
   */
  headerInsertOrUpdate(params) {
    console.log(params);
    return httpFetch.post(`${config.expenseUrl}/api/input/header/insertOrUpdate`, params);
  },
  /**
   * 获取新建进项税业务单页面表格数据
   */
  getHeaderSources(params) {
    const url = `${config.expenseUrl}/api/input/line/getReportData`;
    return httpFetch.get(url, params);
  },
  /**
   * 保存新建进项税业务单页面表格行数据
   */
  setHeaderSources(params) {
    const url = `${config.expenseUrl}/api/input/line/insertOrUpdate`;
    return httpFetch.post(url, params);
  },
  /**
   * 获取模态框分摊行数据
   */
  getSplitLineList(params) {
    const url = `${config.expenseUrl}/api/expense/report/dist/query/by/lineId`;
    return httpFetch.get(url, params);
  },
  /**
   * 获取进项税业务单页面行数据(进项税转出、视同销售)
   * @param {*} params
   */
  getBusinessReceiptList(params) {
    const url = `${config.expenseUrl}/api/input/line/queryByHeaderId`;
    return httpFetch.get(url, params);
  },
  /**
   * 获取进项税业务单页面头数据
   */
  getBusinessReceiptHeadValue(headerId) {
    const url = `${config.expenseUrl}/api/input/header/queryById?id=${headerId}`;
    return httpFetch.get(url);
  },
  /**
   * 提交
   * @param {*} headerId
   */
  submitList(headerId) {
    const url = `${config.expenseUrl}/api/input/header/updateStatus?id=${headerId}&status=1002`;
    return httpFetch.post(url, { approvalRemark: '' });
  },
  /**
   * 进项税业务单页面行删除
   */
  deleteLineValue(lineId) {
    const url = `${config.expenseUrl}/api/input/line/delete?id=${lineId}`;
    return httpFetch.delete(url);
  },
  /**
   *进项税业务单页面行编辑
   * @param {*} params
   */
  updateLineValue(params) {
    const url = `${config.expenseUrl}/api/input/header/insertOrUpdate`;
    return httpFetch.put(url, params);
  },
  /**
   * 进项税业务单页面底部删除
   * @param {*} headerId
   */
  deleteInputTax(headerId) {
    const url = `${config.expenseUrl}/api/input/header/delete?id=${headerId}`;
    return httpFetch.get(url);
  },
  /**
   * 获取费用行
   * @param {*} params
   */
  getExpenseLine(params) {
    const url = `${config.expenseUrl}/api/input/line/getReportData`;
    return httpFetch.get(url, params);
  },
  /**
   * 保存费用行
   * @param {*} params
   */
  saveExpenseLine(params) {
    const url = `${config.expenseUrl}/api/input/line/insertOrUpdate`;
    return httpFetch.post(url, params);
  },
};

import config from 'config';
import httpFetch from 'share/httpFetch';
import moment from 'moment';

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
   * 获取汇率
   * @param {*} key
   */
  getCurrencyExchangeRate(key) {
    const currencyDate = moment(new Date()).format('YYYY-MM-DD');
    const url = `${
      config.mdataUrl
    }/api/currency/rate/company/standard/currency/get?currency=${key}&currencyDate=${currencyDate}`;
    return httpFetch.get(url);
  },
  /**
   * 获取项目申请单类型定义数据组
   */
  getProjectDefinedDetails(params = {}) {
    //const url = `${config.contractUrl}/api/project/requisition/type/pageByCondition`;
    const url = `${config.contractUrl}/api/project/requisition/type/query/all?enabled=true`;
    return httpFetch.get(url, params);
  },
  /**
   * 根据项目申请单id获取当前项目类型详情
   * @param {*} id
   */
  getCurProjectDetail(id) {
    // const url = `${config.contractUrl}/api/project/type/query`;
    //const url = `${config.contractUrl}/api/project/type/query/by/setofbooksid/enable`;
    //return httpFetch.get(url, { enable: true, setOfBooksId: id });
    const url = `${config.contractUrl}/api/project/requisition/type/query/project/type`;
    return httpFetch.get(url, { applicationTypeId: id, page: 0, size: 1000 });
  },
  /**
   * 新建申请
   * @param {*} params
   */
  saveProjectApplyValue(params) {
    const url = `${config.contractUrl}/api/project/requisition`;
    return httpFetch.post(url, params);
  },
  /**
   * 更新申请（项目详情界面的编辑btn对应接口）
   */
  updateProjectApplyValue(params) {
    const url = `${config.contractUrl}/api/project/requisition`;
    return httpFetch.put(url, params);
  },
};

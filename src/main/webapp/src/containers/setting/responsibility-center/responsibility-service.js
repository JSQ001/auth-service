import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   *
   * 保存新建责任中心
   */
  saveResponsibility(params) {
    let url = `${config.mdataUrl}/api/responsibilityCenter/insertOrUpdate`;
    return httpFetch.post(url, params);
  },

  /**
   *
   * 保存新建责任中心组
   */
  saveResponsibilityGroup(params) {
    let url = `${config.mdataUrl}/api/responsibilityCenter/group/insertOrUpdate`;
    return httpFetch.post(url, params);
  },
  deleteResponsibility(id) {
    let url = `${config.mdataUrl}/api/responsibilityCenter/delete/${id}`;
    return httpFetch.delete(url);
  },
  deleteResponsibilityGroup(id) {
    let url = `${config.mdataUrl}/api/responsibilityCenter/group/delete/${id}`;
    return httpFetch.delete(url);
  },

  /**
   *
   * 责任中心组添加责任中心
   */
  responsibilityGroupAddCenter(params) {
    let url = `${config.mdataUrl}/api/responsibilityCenter/group/insert/relationship`;
    return httpFetch.post(url, params);
  },

  /**
   *
   * 首页的批量创建公司
   */
  companyBatch(params) {
    let url = `${config.mdataUrl}/api/responsibilityCenter/company/assign/batch`;
    return httpFetch.post(url, params);
  },

  /**
   *
   * 查询获取分配公司
   */
  searchCompanyBatch(params) {
    let url = `${config.mdataUrl}/api/responsibilityCenter/company/assign/query`;
    return httpFetch.get(url, params);
  },

  /**
   * 获取责任中心详情信息
   * @param {}
   */
  getCurrentDimensionValue(params) {
    const url = `${config.mdataUrl}/api/responsibilityCenter/${params}`;
    return httpFetch.get(url);
  },

  /**
   * 确定导入
   * @param {*} params
   */
  confirmImporter(params) {
    return httpFetch.post(`${config.mdataUrl}/api/responsibilityCenter/import/confirm/${params}`);
  },

  /**
   * 确定导出
   * @param {*} params
   * @param {*} setOfBooksId
   */
  exportMethod(params, setOfBooksId) {
    let url = `${config.mdataUrl}/api/responsibilityCenter/export?setOfBooksId=${setOfBooksId}`;
    return httpFetch.post(url, params, {}, { responseType: 'arraybuffer' });
  },

  /**
   * 分配公司启用
   */
  enableCompany(params) {
    return httpFetch.put(
      `${config.mdataUrl}/api/responsibilityCenter/company/assign/batch/`,
      params
    );
  },

  /**
   * 责任中心组-添加责任中心
   */
  addResponsibilityCenterToGroup(groupId, params) {
    let url = `${
      config.mdataUrl
    }/api/responsibilityCenter/group/insert/relationship?centerGroupId=${groupId}`;
    return httpFetch.post(url, params);
  },
};

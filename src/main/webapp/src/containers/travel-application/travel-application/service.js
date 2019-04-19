import config from 'config';
import httpFetch from 'share/httpFetch';

export default {
  /**
   * 查询当前机构下所有的申请单类型（查询下拉框)
   * @param {*} params
   */
  getApplicationTypeList(params) {
    return httpFetch.get(`${config.expenseUrl}/api/travel/application/type/query/all`, params);
  },
  /**
   * 查询当前机构下所有已创建的申请单类型（查询下拉框)
   * @param {*} params
   */
  getCreatedApplicationTypeList(params) {
    return httpFetch.get(`${config.expenseUrl}/api/travel/application/type/query/created`, params);
  },
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
   * 获取维度值
   * @param {*} parmas
   */
  getDimensionValues(id, companyId) {
    return httpFetch.get(`${config.mdataUrl}/api/dimension/item/list/By/dimensionId/enabled`, {
      dimensionId: id,
      enabled: true,
      companyId: companyId,
    });
  },

  /**
   * 创建一个费用申请单头
   * @param {*} params
   */
  addTravelApplictionForm(params) {
    return httpFetch.post(`${config.expenseUrl}/api/travel/application/header`, params);
  },

  /**
   * 根据单据头ID查询单据头详情
   * @param {*} id
   */
  getApplicationDetail(id) {
    return httpFetch.get(`${config.expenseUrl}/api/travel/application/header/` + id);
  },

  /**
   * 删除申请单
   * @param {*} id
   */
  deleteTravelApplication(id) {
    return httpFetch.delete(`${config.expenseUrl}/api/travel/application/header/` + id);
  },

  /**
   * 根据单据头ID分页查询单据行信息
   * @param {*} params
   */
  getApplicationLines(id, params) {
    return httpFetch.get(`${config.expenseUrl}/api/travel/application/line/query/` + id, params);
  },

  /**
   * 申请单行创建时查询维度信息默认值
   * @param {*} params
   */
  getNewInfo(params) {
    return httpFetch.get(`${config.expenseUrl}/api/travel/application/line/query/info`, params);
  },

  /**
   * 新增申请单行
   * @param {*} params
   */
  addApplicationLine(params) {
    return httpFetch.post(`${config.expenseUrl}/api/travel/application/line`, params);
  },

  /**
   * 编辑申请单行
   * @param {*} params
   */
  updateApplicationLine(params) {
    return httpFetch.put(`${config.expenseUrl}/api/travel/application/line`, params);
  },

  /**
   * 提交
   * @param {*} params
   */
  submit(params) {
    let url = `${config.expenseUrl}/api/travel/application/submit`;
    return httpFetch.post(url, params);
  },

  /**
   * 删除行数据
   * @param {*} id
   */
  deleteLine(id) {
    return httpFetch.delete(`${config.expenseUrl}/api/travel/application/line/` + id);
  },

  /**
   * 根据ID查询申请单头信息(编辑申请单头时调用)
   * @param {*} id
   */
  getEditInfo(id) {
    return httpFetch.get(`${config.expenseUrl}/api/travel/application/header/query?id=` + id);
  },

  /**
   * 更新申请单头
   * @param {*} params
   */
  updateHeaderData(params) {
    return httpFetch.put(`${config.expenseUrl}/api/travel/application/header`, params);
  },

  /**
   * 撤回
   * @param {*} params
   */
  withdraw(params) {
    return httpFetch.post(`${config.workflowUrl}/api/workflow/withdraw`, params);
  },

  /**
   * 获取动态维度表头信息
   * @param {*} id
   */
  getColumnInfo(id) {
    return httpFetch.get(`${config.expenseUrl}/api/travel/application/line/column/` + id);
  },
  /**
   * 申请单头新建时查询
   * @param {*} typeId
   */
  getHeaderInfoByNew(typeId) {
    return httpFetch.get(`${config.expenseUrl}/api/travel/application/type/query/header/${typeId}`);
  },
  /**
   * 根据维度id集合和公司id查询启用的维值
   * @param {*} diemensionIds
   * @param {*} companyId
   */
  getDimensionItemsByIds(diemensionIds, companyId, unitId, userId) {
    let url = `${
      config.mdataUrl
    }/api/dimension/item/list/By/dimensionIds/companyId/enabled?companyId=${companyId}&unitId=${unitId}&userId=${userId}&enabled=true`;
    return httpFetch.post(url, diemensionIds);
  },
  /**
   * 编辑行明细
   * @param {*} params
   */
  updateApplicationLineDetail(params) {
    return httpFetch.put(`${config.expenseUrl}/api/travel/application/line/detail`, params);
  },
  /**
   * 获取差旅类型
   * @param {*} id
   */
  getTravelApplicationTypeById(id) {
    return httpFetch.get(`${config.expenseUrl}/api/travel/application/type/` + id);
  },
  /**
   * 获取申请大类
   * @param {*} id
   */
  getExpenseTypesCategoryBySetOfBooksId(setOfBooksId) {
    return httpFetch.get(`${config.expenseUrl}/api/expense/types/category`, { setOfBooksId });
  },
  /**
   * 根据id获取城市
   * @param {*} id
   */
  getLocalizationCityById(id) {
    return httpFetch.post(`${config.mdataUrl}/api/location/city/ids`, [id]);
  },
  /**
   * 根据id批量获取用户
   * @param {*} ids
   */
  getUserByIds(ids) {
    return httpFetch.post(`${config.mdataUrl}/api/list/user/batch/ids`, ids);
  },
};
